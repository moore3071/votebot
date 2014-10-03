(ns j3rnbot.core
  (:require
    [irclj.core :refer :all]
    [clojure.data.json :as json]))

; Load secrets
(def secrets
  (json/read_str
    (slurp "secrets.json")))

; Constants
(def host "irc.freenode.net")
(def port 6667)
(def nick "J3RNBOT")
(def pass (get secrets "bot_pass"))

; SendGrid
(def api_user (get secrets "sendgrid_user"))
(def api_key (get secrets "sendgrid_pass"))

(defn start []
  (def connection (connect host port nick))
  (identify connection pass)
  (join connection "#osuosc-hangman"))
