(ns real-world-clojure.db.articles
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [real-world-clojure.db.core :refer [ds transaction]]
   [real-world-clojure.utils.string :refer [sluggify]]))

(defn query-articles
  [& {:keys [tag author favorited limit offset]}]
  (let [args (vec (filter (comp not nil?) [author tag limit offset]))]
    (jdbc/execute! ds (vec (concat
                            [(format "SELECT * FROM articles %s %s LIMIT ? OFFSET ?"
                                     (if (some? author) "JOIN profiles ON profiles.username = ?" "")
                                     (if (some? tag) "JOIN tags ON tags.article_id = articles.id WHERE tags.label = ?" ""))]
                            args)))))

(defn get-article-by
  [k v]
  (jdbc/execute-one! ds [(format "SELECT * 
                                  FROM articles
                                  WHERE articles.%s = ?"
                                 k)
                         v]))

(defn create-article
  [author-id {:keys [title description body tagList]
              :or {title ""
                   description ""
                   body ""
                   tagList []}
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
       (when (pos? (count tagList))
         (sql/insert-multi!
          tx
          :tags
          (map (fn [label] {:article-id article-id :label label}) tagList)))))))
