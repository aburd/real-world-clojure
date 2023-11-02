(ns real-world-clojure.db.follows
  (:require 
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]
    [real-world-clojure.db.core :refer [ds transaction]]))

(defn is-following?
  [follower-id following-id]
  (boolean 
    (jdbc/execute-one! ds ["SELECT id
                           FROM follows
                           WHERE follower_id = ? AND following_id = ?"
                           follower-id
                           following-id])))

(defn upsert-follow
  [follower-id following-id]
  (when (not (is-following? follower-id following-id))
    (sql/insert! ds :follows {:follower-id follower-id :following-id following-id})))

(defn delete-follow
  [follower-id following-id]
  (when (is-following? follower-id following-id)
    (sql/delete! ds :follows {:follower-id follower-id :following-id following-id})))
