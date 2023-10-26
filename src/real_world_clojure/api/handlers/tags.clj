(ns real-world-clojure.api.handlers.tags
  (:require [compojure.core :refer :all]))

(defroutes api-routes-tags
  (GET "/" [] "getting tags"))
