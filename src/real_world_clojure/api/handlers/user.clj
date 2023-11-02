(ns real-world-clojure.api.handlers.user
  (:require [compojure.core :refer :all]
            [buddy.hashers :as hs]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.api.responses :refer [ok not-found forbidden]]))

(defn handle-get-user
  [{:keys [authorized? user headers]}] 
  (if authorized?
    (ok (db-users/get-user (get user "id")))
    (not-found)))

(defn handle-update-user 
  [{:keys [user body] :as req}] 
  (if user
    (ok (db-users/update-user-and-profile (get user "id") (:user body)))
    (forbidden)))

(defroutes api-routes-user
  (GET "/" [] handle-get-user)
  (PUT "/" [] handle-update-user)) 
