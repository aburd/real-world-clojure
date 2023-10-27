(ns real-world-clojure.api.responses)

(defn ok [body] {:status 200 :body body})
(defn created [] {:status 201})
(defn unauthorized [] {:status 401})
(defn forbidden [] {:status 403})
(defn not-found [] {:status 404})
