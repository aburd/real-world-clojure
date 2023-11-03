(ns real-world-clojure.db.profiles
  (:require 
    [buddy.hashers :as hs]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]
    [real-world-clojure.db.core :refer [ds transaction]]
    [real-world-clojure.utils.map :as map-utils]))

(defn get-profile-by
  [k v follower-id & {:keys [table] :or {table "profiles"}}]
  (jdbc/execute-one! ds [(format "SELECT *, 
                                 EXISTS(
                                  SELECT 1
                                  FROM follows
                                  WHERE follower_id = ? AND following_id = users.id
                                 ) as following
                         FROM profiles
                         JOIN users 
                           ON users.id = user_id
                         WHERE %s.%s = ?" table k)
                         follower-id
                         v]))

(defn get-profile-by-user-id
  [user-id follower-id]
  (get-profile-by "id" user-id follower-id :table "users"))

(defn get-profile-id-by-username
  [username]
  (let [record (jdbc/execute-one! ds ["SELECT id
                                       FROM profiles
                                       WHERE username = ?"
                                      username])]
    (when (some? record) (:id record))))

(defn get-profile-by-user-id
  [user-id]
  (jdbc/execute-one! 
    ds 
    ["SELECT profiles.id
     FROM profiles 
     JOIN users ON users.id = profiles.user_id 
     WHERE users.id = ?" user-id])) 

(defn update-profile
  [user-id diff]
  (let [profile (get-profile-by-user-id user-id)]
    (when (some? profile)
      (println :profile profile)
      (sql/update! ds :profiles diff {:id (:id profile)}))))

(defn create-profile
  ([profile] (create-profile profile ds))
  ([profile ds]
   (sql/insert! ds :profiles profile {:return-keys true})))
