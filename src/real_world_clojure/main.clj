(ns real-world-clojure.main
  (:require
   [clojure.java.jdbc :as j]))

(def pg-db {:dbtype "postgresql"
            :port 5433
            :user "root"
            :dbname "real_world"
            :password "root"})

(defn db
  []
  (j/query pg-db ["SELECT current_database()"]))
