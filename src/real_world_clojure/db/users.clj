(ns real-world-clojure.db.users
  (:require 
    [real-world-clojure.db.core :refer [ds transaction]]
    [buddy.hashers :as hs]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]))

(defn sanitize-user
  [user]
  (dissoc user :password-hash))

(defn create-user
  ([user] (create-user user ds))
  ([user ds]
   (let [record (sql/insert! ds :users user {:return-keys true})]
     (sanitize-user record))))

(defn create-profile
  ([profile] (create-profile profile ds))
  ([profile ds]
   (sql/insert! ds :profiles profile {:return-keys true}))) 

(defn create-user-with-profile
  [user profile]
  (transaction 
    (fn [tx ds]
      (let [user-record (create-user user tx)
            p (assoc profile :user-id (:id user-record))]
        (create-profile p tx)))))

(defn get-user
  [id]
  (jdbc/execute-one! ds ["SELECT email, token, username, bio, image
                         FROM users
                         JOIN profiles ON users.id = profiles.user_id
                         WHERE users.id = ?"
                         id]))

(defn get-profile
  [username]
  (jdbc/execute-one! ds ["SELECT email, token, username, bio, image
                         FROM users
                         JOIN profiles ON users.id = profiles.user_id
                         WHERE profiles.username = ?"
                         username]))

(defn update-user
  [user-id diff]
  (sql/update! ds :users diff {:id user-id}))

(defn update-profile
  [profile diff]
  (sql/update! ds :users diff {:id (:id profile)}))

; (get-users)
; (create-user-with-profile 
;   {:email "bar@bar.com" :password-hash (hs/encrypt "password")}
;   {:username "no" :bio "a foo man" :image nil}) 
; (create-user
;   {:email "bar@bar.com" :password-hash (hs/encrypt "password")})
