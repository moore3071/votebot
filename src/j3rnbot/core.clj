(ns j3rnbot.core
  (:require
    [irclj.core :refer :all]
    [clojure.pprint :refer :all]
    [clojure.data.json :as json]
    [clojure.string :as string]))

; Load secrets
(def secrets
  (json/read-str
    (slurp "secrets.json")))

; Constants
(def host "irc.freenode.net")
(def port 6667)
(def nick "J3RNBOT")
(def nick_length (count nick))
(def pass (get secrets "bot_pass"))
(def master "j3rn")

; SendGrid
(def api_user (get secrets "sendgrid_user"))
(def api_key (get secrets "sendgrid_pass"))

; Respond to master's request
(defn obey-master [irc args command]
  (case command
    "hello"
      (reply irc args "Hello, master")
    (reply irc args "I can't do that for you, J3RN")))

; Respond to user's request
(defn obey-user [irc args command]
  (case command
    (reply irc args "No!")))

; Simply parse out the "J3RNBOT" part
(defn parse-command [text]
  (if (and
        (> (count text) (+ 1 nick_length))
        (= (.charAt text nick_length) \:))
    (string/trim (subs text (+ 1 nick_length)))
    (string/trim (subs text nick_length))))

; Message posted callback
(defn callback [irc args]
  (def sender (string/lower-case(get args :nick)))
  (def text (string/lower-case(get args :text)))
  ; Debugging
  (println sender "said" text)
  ; Test if I can be the subject
  (def subject (first (string/split text #" ")))
  (if (< nick_length (count subject))
    ; Test if I am the subject
    (if (= (subs subject 0 nick_length) (string/lower-case nick))
      (do
        (println "They're talking to me!")
        (def command (parse-command text))
        (println "I have been tasked with" command)
        (if (= sender master)
          (obey-master irc args command)
          (obey-user irc args command))))))

; Main method
(defn start []
  ; Authenticate with SendGrid
  (def auth {:api_user api_user :api_key api_key})

  ; Connnect to IRC
  (def connection (connect host port nick :callbacks {:privmsg callback}))
  (identify connection pass)
  (join connection "#osuosc-hangman"))
  (println "Connected!")
