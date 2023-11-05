(ns real-world-clojure.api.handlers.validators.common
  (:require [clojure.spec.alpha :as s]))

(s/def :common/limit (s/and int? pos?))
(s/def :common/offset (s/and int? (partial not neg?)))
