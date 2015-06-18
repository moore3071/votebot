(defproject j3rnbot "0.2.0"
  :description "A bot for J3RN"
  :url "http://github.com/J3RN/J3RNBOT"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [irclj "0.5.0-alpha4"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/core.match "0.2.1"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [korma "0.4.0"]
                 [postgresql/postgresql "8.4-702.jdbc4"]]
  :repl-options {:init-ns j3rnbot.core}
  :main "j3rnbot.core/start")
