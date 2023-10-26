(ns real-world-clojure.api.handlers.profiles
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]))

(defn get-profile
  [{:keys [params]}]
  (let [profile (db-users/get-profile (:username params))]
    (if profile 
      {:status 200 :body profile})))

(defroutes api-routes-profiles
  (GET "/:username" [] get-profile)
  (POST "/:username/follow" [] "following user")
  (DELETE "/:username/follow" [] "unfollowing user"))
