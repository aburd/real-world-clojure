(ns real-world-clojure.api.handlers.user
  (:require [compojure.core :refer :all]
            [buddy.hashers :as hs]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.serializers.users :as user-serializers]
            [real-world-clojure.api.responses :refer [ok not-found forbidden]]))

(defn handle-get-user
  [{:keys [authorized? user headers]}] 
  (if authorized?
    (ok (user-serializers/user (db-users/get-user (:user-id user))))
    (not-found)))

(defn handle-update-user 
  [{:keys [user body] :as req}] 
  (println :user user)
  (if user
    (ok (user-serializers/user (db-users/update-user-and-profile (:user-id user) (:user body))))
    (forbidden)))

(defroutes api-routes-user
  (GET "/" [] handle-get-user)
  (PUT "/" [] handle-update-user)) 
