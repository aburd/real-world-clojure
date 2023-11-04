(ns real-world-clojure.db.articles
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [real-world-clojure.db.core :refer [ds transaction]]
   [real-world-clojure.utils.string :refer [sluggify]]))

(defn query-articles
  [{:keys [tag author favorited limit offset follower-id]}]
  (let [args (vec (filter (comp not nil?) [follower-id author tag limit offset]))
        sql-statement (format "SELECT articles.id as article_id, *
                                %s
                              FROM articles 
                              JOIN profiles ON profiles.id = author_id
                              JOIN users 
                                ON users.id = profiles.user_id
                              %s
                              %s
                                %s %s %s 
                              %s 
                              %s"
                         (if (some? follower-id) "
                                 ,EXISTS(
                                  SELECT 1
                                  FROM follows
                                  WHERE follower_id = ? AND following_id = users.id
                                 ) as following
                           " "")
                         (if (some? tag) "JOIN tags ON tags.article_id = articles.id" "")
                         (if (some? (or tag author)) "WHERE" "")
                         (if (some? author) "profiles.username = ?" "")
                         (if (some? (and tag author)) "AND" "")
                         (if (some? tag) " tags.label = ?" "")
                         (if (some? limit) "LIMIT ?" "")
                         (if (some? offset) "OFFSET ?" ""))]
    (jdbc/execute! ds (vec (concat [sql-statement] args)))))

(defn get-article-by
  [k v follower-id]
  (jdbc/execute-one! ds [(format "SELECT *, 
                                   EXISTS(
                                    SELECT 1
                                    FROM follows
                                    WHERE follower_id = ? AND following_id = users.id
                                   ) as following
                                  FROM articles
                                  JOIN profiles ON profiles.id = author_id
                                  JOIN users 
                                    ON users.id = profiles.user_id
                                  WHERE articles.%s = ?"
                                 k)
                         follower-id
                         v]))

(defn create-article
  [author-id {:keys [title description body tags]
              :or {title ""
                   description ""
                   body ""
                   tags []}
              :as params}]
  (transaction
   (fn [tx ds]
     (let [slug (sluggify title)
           article (sql/insert!
                    tx
                    :articles
                    {:author-id author-id :title title :description description :body body :slug slug}
                    {:return-keys true})
           article-id (:id article)]
       (when (pos? (count tags))
         (sql/insert-multi!
          tx
          :tags
          (map (fn [label] {:article-id article-id :label label}) tags)))))))
