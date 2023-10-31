(ns real-world-clojure.db.follows
  (:require 
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]
    [real-world-clojure.db.core :refer [ds transaction]]))

(defn update-follow
  [follower-id following-id]
  (sql/insert! ds :follows {:follower-id follower-id :following-id following-id} {:return-keys true}))

(defn is-following?
  [follower-id following-id]
  (boolean 
    (jdbc/execute-one! ds ["SELECT id
                           FROM follows
                           WHERE follower_id = ? AND following_id = ?"
                           follower-id
                           following-id])))

