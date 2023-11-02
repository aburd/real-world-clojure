(ns real-world-clojure.db.profiles
  (:require 
    [buddy.hashers :as hs]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]
    [real-world-clojure.db.core :refer [ds transaction]]
    [real-world-clojure.utils.map :as map-utils]
    [real-world-clojure.db.follows :refer [is-following?]]))

(defn get-profile
  [username follower-id]
  (jdbc/execute-one! ds ["SELECT username, bio, image, EXISTS(
                          SELECT 1
                          FROM follows
                          WHERE follower_id = ? AND following_id = users.id
                         ) as following
                         FROM profiles
                         JOIN users 
                           ON users.id = user_id
                         WHERE profiles.username = ?"
                         follower-id
                         username]))


(defn get-profile-id
  [user-id]
  (jdbc/execute-one! ds ["SELECT profiles.id
                         FROM users
                         JOIN profiles ON users.id = profiles.user_id
                         WHERE users.id = ?"
                         user-id]))

(defn update-profile
  [user-id diff]
  (let [profile-id (:id (get-profile-id user-id))]
    (sql/update! ds :profiles diff {:id profile-id})))

(defn create-profile
  ([profile] (create-profile profile ds))
  ([profile ds]
   (sql/insert! ds :profiles profile {:return-keys true})))
