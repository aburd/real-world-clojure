(ns real-world-clojure.api.handlers.user
  (:require [compojure.core :refer :all]))

(defroutes api-routes-user
  (GET "/" [] "get current user")
  (PUT "/" [] "update user"))
