(ns real-world-clojure.db.users
  (:require
   [buddy.hashers :as hs]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [real-world-clojure.db.core :refer [ds transaction]]
   [real-world-clojure.db.profiles :refer [create-profile update-profile]]
   [real-world-clojure.utils.map :as map-utils]))

(defn get-user
  [id]
  (jdbc/execute-one! ds ["SELECT *
                         FROM users
                         JOIN profiles ON users.id = profiles.user_id
                         WHERE users.id = ?"
                         id]))

(defn get-user-by
  [k v]
  (jdbc/execute-one! ds [(format "SELECT *
                         FROM users
                         JOIN profiles ON users.id = profiles.user_id
                         WHERE users.%s = ?" k)
                         v]))

(defn get-user-by-username
  [v]
  (jdbc/execute-one! ds ["SELECT *
                         FROM users
                         JOIN profiles ON users.id = profiles.user_id
                         WHERE profiles.username = ?"
                         v]))

(defn create-user
  ([user] (create-user user ds))
  ([user ds]
   (sql/insert! ds :users user {:return-keys true})))

(defn create-user-with-profile
  [user profile]
  (transaction
   (fn [tx ds]
     (let [new-user (create-user user tx)
           p (assoc profile :user-id (:id new-user))
           profile (create-profile p tx)])))
  (get-user-by "email" (:email user)))

(defn update-user
  [user-id diff]
  (sql/update! ds :users diff {:id user-id})
  (get-user user-id))

(defn update-user-and-profile
  [user-id {:keys [email username password image bio]}]
  (let [user-update (map-utils/filter-nil {:email email})
        profile-update (map-utils/filter-nil {:username username :image image :bio bio})]
    (when (some? password)
      (update-user user-id {:password-hash (hs/encrypt password)}))
    (when (not (empty? user-update))
      (update-user user-id user-update))
    (when (not (empty? profile-update))
      (update-profile user-id profile-update))
    (get-user user-id)))

; (get-users)
; (create-user-with-profile 
;   {:email "bar@bar.com" :password-hash (hs/encrypt "password")}
;   {:username "no" :bio "a foo man" :image nil}) 
; (create-user ;   {:email "bar@bar.com" :password-hash (hs/encrypt "password")})
