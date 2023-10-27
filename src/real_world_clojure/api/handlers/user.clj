(ns real-world-clojure.api.handlers.user
  (:require [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]))

(defn get-user
  [req] 
  (db-users/get-user))

(defn update-user 
  [req] 
  (db-users/get-user))

(defroutes api-routes-user
  (GET "/" [] "get current user")
  (PUT "/" [] "update user"))
