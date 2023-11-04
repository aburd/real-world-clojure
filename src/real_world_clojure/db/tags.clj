(ns real-world-clojure.db.tags
  (:require
   [next.jdbc :as jdbc]
   [real-world-clojure.db.core :refer [ds transaction]]))

(defn get-tags-by
  [k v]
  (let [sql-statement (format "SELECT * 
                              FROM tags
                              WHERE 
                               tags.%s = ?" k)]
    (jdbc/execute! ds [sql-statement v])))
