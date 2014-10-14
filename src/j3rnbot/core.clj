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
(def channels [ "#osuosc-hangman" ])

; SendGrid
(def api_user (get secrets "sendgrid_user"))
(def api_key (get secrets "sendgrid_pass"))

; Set  state
(def state (atom {:states [], :presence [], :pizza_count {}}))

; Respond to user's request
(defn obey-user [irc args command sender]
  (case (first command)
    ("hello" "hello!" "hi" "hi!")
      (reply irc args (string/join " " ["Hello," sender]))
    ("beep" "boop")
      (reply irc args "boop")
    ("help" "halp")
      (do
        (reply irc args "Currently, I support: hello beep halp")
        (reply irc args "More is coming soon!"))
    (reply irc args "I don't know how to do that...")))

; (defn vote [flavor]
;   (if (contains? (keys (get state 2)) flavor)
;     (reset! state (update-in @state []))
;     (reset! state )))

; Respond to master's request
(defn obey-master [irc args command]
  (case (first command)
    ; "vote" (vote (get command 1))
    (obey-user irc args command master)))

; Message posted callback
(defn callback [irc args]
  (let [sender (string/lower-case (:nick args))
        tokens (string/split (string/lower-case (:text args)) #" ")]

    ; Debugging
    (println sender "said" (string/join " " tokens))

    (let [subject (first tokens)
          command (rest tokens)]
      ; Test if I am the subject
      (if (= subject (string/lower-case (string/join "" [nick ":"])))
        (do
          (println "I have been tasked with" command)
          (if (= sender master)
            (obey-master irc args command)
            (obey-user irc args command)))))))

; Main method
(defn start []
  ; Authenticate with SendGrid
  (def auth {:api_user api_user :api_key api_key})

  ; Connnect to IRC
  (def connection (connect host port nick :callbacks {:privmsg callback}))
  (identify connection pass)
  (doseq [channel channels]
    (join connection channel)
    (println "Joined " channel)))
