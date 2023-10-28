(ns real-world-clojure.api.handlers.user
  (:require [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.api.responses :refer [ok not-found]]))

(defn get-user
  [{:keys [authorized? user headers]}] 
  (if authorized?
    (ok (db-users/get-user (get user "id")))
    (not-found)))

(defn update-user 
  [req] 
  (db-users/get-user))

(defroutes api-routes-user
  (GET "/" [] get-user)
  (PUT "/" [] "update user"))
