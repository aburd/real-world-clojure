(ns real-world-clojure.api.handlers.validators.core
  (:require [clojure.spec.alpha :as s]
            [clojure.string :refer [upper-case]]))

(defn validate [handler {:keys [params-key body-key]}]
  (fn [req]
    (let [params-valid? (if (some? params-key) (s/valid? params-key (:params req)) true)
          body-valid? (if (some? body-key) (s/valid? body-key (:body req)) true)]
      (cond
        (not params-valid?) (throw (ex-info "Invalid params input" (s/explain-data params-key (:params req))))
        (not body-valid?) (throw (ex-info "Invalid body input" (s/explain-data body-key (:body req))))
        :else (handler req)))))

(defn- parse [params parse-map]
  (reduce-kv 
    (fn [m k v] (assoc m k (if-let [f (k parse-map)]
                             (f v) 
                             v)))
    {}
    params))

(defn parse-params [handler parse-map]
  (fn [{:keys [params] :as req}]
    (handler
      (if (some? params)
        (assoc req :params (parse params parse-map))
        req))))
