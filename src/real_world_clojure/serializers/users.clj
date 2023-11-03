(ns real-world-clojure.serializers.users)

(defn one
  [{:keys [email token username bio image] :as record}]
  {:user {:email email :token token :username username :bio bio :image image}})

(defn many
  [users]
  (map one users))
