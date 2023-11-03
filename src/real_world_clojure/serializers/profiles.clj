(ns real-world-clojure.serializers.profiles)

(defn profile
  [{:keys [username bio image following] :as record}]
  {:user {:email email :token token :username username :bio bio :image image}})
