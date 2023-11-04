(ns real-world-clojure.api.handlers.profiles
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.api.responses :refer [ok]]
            [real-world-clojure.db.profiles :as db-profiles]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.db.follows :as db-follows]
            [real-world-clojure.serializers.profiles :as s-profiles]))

(defn get-profile
  [{{:keys [username]} :params user :user}]
  (ok
   (s-profiles/one
    (db-profiles/get-profile-by "username" username (:user-id user)))))

(defn follow-profile
  [{{:keys [username]} :params user :user}]
  (let [following-id (:id (db-users/get-user-by-username username))
        follower-id (:id (db-users/get-user-by-username (:username user)))]
    (db-follows/upsert-follow follower-id following-id)
    (ok
     (s-profiles/one
      (db-profiles/get-profile-by "username" username (:user-id user))))))

(defn unfollow-profile
  [{{:keys [username]} :params user :user}]
  (let [following-id (:id (db-users/get-user-by-username username))
        follower-id (:id (db-users/get-user-by-username (:username user)))]
    (db-follows/delete-follow follower-id following-id)
    (ok
     (s-profiles/one
      (db-profiles/get-profile-by "username" username (:user-id user))))))

(defroutes api-routes-profiles
  (GET "/:username" [] get-profile)
  (POST "/:username/follow" [] follow-profile)
  (DELETE "/:username/follow" [] unfollow-profile))
