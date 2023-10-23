(ns real-world-clojure.db.core
   (:require 
     [next.jdbc :as jdbc]))

(def pg-db {:dbtype "postgresql"
            :port 5433
            :user "root"
            :dbname "real_world"
            :password "root"})

(def ds (jdbc/with-options (jdbc/get-datasource pg-db) jdbc/unqualified-snake-kebab-opts))

(defn transaction
  [body-fn]
  (jdbc/with-transaction [tx d]
    (let [tx-wrapped (jdbc/with-options tx jdbc/unqualified-snake-kebab-opts)]
      (body-fn tx-wrapped ds))))
