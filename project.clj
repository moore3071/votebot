(defproject j3rnbot "0.1.0-SNAPSHOT"
  :description "A bot for J3RN"
  :url "http://github.com/J3RN/J3RNBOT"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [irclj "0.5.0-alpha4"]
                 [org.clojure/data.json "0.2.5"]
                 [sendgrid "0.1.0"]]
  :main "j3rnbot.core/start")
