(ns real-world-clojure.db.users
  (:require 
    [real-world-clojure.db.core :refer [ds transaction]]
    [buddy.hashers :as hs]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]))

(defn create-user
  ([user] (create-user user ds))
  ([user ds]
   (let [record (sql/insert! ds :users user {:return-keys true})]
     (dissoc record :users/password-hash))))

(defn create-profile
  ([profile] (create-profile profile ds))
  ([profile ds]
   (sql/insert! ds :profiles profile {:return-keys true}))) 

(defn create-user-with-profile
  [user profile]
  (transaction 
    (fn [tx ds]
      (let [user-id (:users/id (create-user user tx))
            p (assoc profile :user-id user-id)]
        (println p)
        (create-profile p tx)))))

; (create-user-with-profile 
;   {:email "foo@bar.com" :password-hash (hs/encrypt "password")}
;   {:username "foo" :bio "a foo man" :image nil}) 
