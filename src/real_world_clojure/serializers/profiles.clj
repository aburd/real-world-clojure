(ns real-world-clojure.serializers.profiles)

(defn inner [record]
  (select-keys record [:email :username :bio :image :following]))

(defn one [record]
  {:user (inner record)})
