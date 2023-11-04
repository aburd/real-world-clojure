(ns real-world-clojure.api.responses
  (:require [real-world-clojure.utils.map :refer [map-keys]]
            [real-world-clojure.utils.symbol :refer [kebab-to-camel]]))

(defn ok [body] {:status 200 :body (map-keys body kebab-to-camel)})
(defn created [] {:status 201})
(defn unauthorized [] {:status 401})
(defn forbidden [] {:status 403})
(defn not-found [] {:status 404})
