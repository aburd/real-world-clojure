(ns real-world-clojure.db.core
   (:require 
     [next.jdbc :as jdbc]))

(def pg-db {:dbtype "postgresql"
            :port 5433
            :user "root"
            :dbname "real_world"
            :password "root"})

(def ds (jdbc/with-options (jdbc/get-datasource pg-db) jdbc/snake-kebab-opts))

(defn transaction
  [body-fn]
  (jdbc/with-transaction [tx ds]
    (let [tx-wrapped (jdbc/with-options tx jdbc/snake-kebab-opts)]
      (body-fn tx-wrapped ds))))
