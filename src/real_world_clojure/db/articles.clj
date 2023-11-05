(ns real-world-clojure.db.articles
  (:require
   [clojure.string :refer [join]]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [real-world-clojure.db.core :refer [ds transaction]]
   [real-world-clojure.utils.string :refer [sluggify]]))

(def favorited-sql "EXISTS(
                     SELECT 1
                     FROM favorite_articles
                     WHERE profile_id = profiles.id AND article_id = articles.id
                    ) as favorited")

(def favorite-count-sql "(SELECT COUNT(*)
                          FROM favorite_articles
                          WHERE favorite_articles.article_id = articles.id 
                        ) as favorites_count")

(def following-sql "EXISTS(
                    SELECT 1
                    FROM follows
                    WHERE follower_id = ? AND following_id = users.id
                   ) as following")

(defn select-statement
  [user-id]
  (let [selects (vec (filter some? ["*" 
                                    "articles.id as article_id" 
                                    favorited-sql 
                                    favorite-count-sql 
                                    (if (some? user-id) following-sql nil)]))]
    (str "SELECT " (join ",\n" selects))))

(defn query-articles
  [{:keys [tag author favorited limit offset user-id]}]
  (let [args (vec (filter (comp not nil?) [user-id author tag limit offset]))
        sql-statement (format "%s
                              FROM articles 
                              JOIN profiles ON profiles.id = author_id
                              JOIN users ON users.id = profiles.user_id
                              %s
                              %s
                                %s %s %s 
                              %s 
                              %s"
                         (select-statement user-id) 
                         (if (some? tag) "JOIN tags ON tags.article_id = articles.id" "")
                         (if (some? (or tag author)) "WHERE" "")
                         (if (some? author) "profiles.username = ?" "")
                         (if (some? (and tag author)) "AND" "")
                         (if (some? tag) " tags.label = ?" "")
                         (if (some? limit) "LIMIT ?" "")
                         (if (some? offset) "OFFSET ?" ""))]
    (jdbc/execute! ds (vec (concat [sql-statement] args)))))

(defn get-article-by
  [k v user-id]
  (let [sql-statement (format "%s
                          FROM articles
                          JOIN profiles ON profiles.id = author_id
                          JOIN users ON users.id = profiles.user_id
                          WHERE articles.%s = ?"
                         (select-statement user-id)
                         k)]
    (jdbc/execute-one! ds [sql-statement
                           user-id
                           v])))

(defn get-article-by-slug
  [slug]
  (jdbc/execute-one! ds ["SELECT * FROM articles WHERE slug = ?"
                         slug]))

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
          (map (fn [label] {:article-id article-id :label label}) tags)))
       article))))

(defn favorite-article
  [profile-id slug]
  (let [article (get-article-by-slug slug)]
    (sql/insert! ds :favorite-articles {:profile-id profile-id :article-id (:id article)})))
