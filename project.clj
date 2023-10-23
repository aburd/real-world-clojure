(defproject real-world-clojure "0.1.0-SNAPSHOT"
  :description "A real-world implementation written in Clojure using Compojure"
  :url "https://github.com/aburd/real-world-clojure"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.1.2"]
                 [buddy/buddy-hashers "2.0.167"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.h2database/h2 "1.3.168"]
                 [cheshire "4.0.3"]
                 [migratus "1.5.3"]
                 [org.postgresql/postgresql "42.3.3"]
                 [com.github.seancorfield/next.jdbc "1.3.894"]]
  :plugins [[lein-ring "0.12.5"]
            [migratus-lein "0.7.3"]]
  :ring {:handler real-world-clojure.handler/app}
  :migratus {:store :database
             :migration-dir "migrations"
             :db {:dbtype "postgresql"
                  :dbname "real_world"
                  :host "localhost"
                  :port "5433"
                  :user "root"
                  :password "root"}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [org.clojure/java.jdbc "0.7.12"]]}})
