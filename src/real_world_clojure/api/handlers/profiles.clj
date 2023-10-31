(ns real-world-clojure.api.handlers.profiles
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.db.profiles :as db-profiles]))

(defn profile-shape
  [{:keys [username bio image following] :as profile}]
  {:username username :bio bio :image image :following following})

(defn get-profile
  [{{:keys [username]} :params}]
  (let [profile (db-profiles/get-profile username)]
    (if profile 
      {:status 200 :body (profile-shape profile)})))

(defroutes api-routes-profiles
  (GET "/:username" [] get-profile)
  (POST "/:username/follow" [] "following user")
  (DELETE "/:username/follow" [] "unfollowing user"))
