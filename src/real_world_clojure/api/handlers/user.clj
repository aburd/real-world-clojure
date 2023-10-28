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

(defn filter-nil
  [record]
  (select-keys 
    record 
    (for [[k v] record :when (not (nil? v))] k)))

(defn handle-update-user 
  [{:keys [user body] :as req}] 
  (if user
    (let [user-id (get user "id")
          {:keys [email username password image bio]} (:user body)
          user-update (filter-nil {:email email})
          profile-update (filter-nil {:username username :image image :bio bio})]
      (ok (do
            (when (some? password)
              (db-users/update-user user-id {:password-hash (hs/encrypt password)}))
            (when (not (empty? user-update))
              (db-users/update-user user-id user-update))
            (when (not (empty? profile-update))
              (db-users/update-profile user-id profile-update))
            (db-users/get-user user-id))))
    (forbidden)))

(defroutes api-routes-user
  (GET "/" [] handle-get-user)
  (PUT "/" [] handle-update-user)) 
