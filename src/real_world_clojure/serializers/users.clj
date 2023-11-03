(ns real-world-clojure.serializers.users)

(defn user
  [{:keys [email token username bio image] :as record}]
  {:user {:email email :token token :username username :bio bio :image image}})

(defn users
  [us]
  (map user us))
