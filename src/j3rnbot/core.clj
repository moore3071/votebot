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
(def state (atom {:pizza_count {}}))

; If it is not voted for, vote for it
; If it has been voted for, increment the votes
(defn vote! [flavor]
  (if (contains? (get @state :pizza_count) flavor)
    (reset! state (update-in @state [:pizza_count flavor] inc))
    (reset! state (assoc-in @state [:pizza_count flavor] 1))))

; Clear all votes from the state
(defn clear-votes! []
  (reset! state (dissoc @state :pizza_count)))

; Compose a string summarizing the votes
(defn vote-string [pizza_keys pizza_votes]
  (if (= (count pizza_keys) 0)
    ""
    (let [this_key (first pizza_keys)]
      (reduce
        str
        (str this_key ":" (get-in pizza_votes this_key) " ")
        (vote-string (rest pizza_keys) pizza_votes)))))

; Respond to user's request
(defn obey-user [irc args command sender]
  (case (first command)
    ("hello" "hello!" "hi" "hi!")
      (reply irc args (str "Hello, " sender))
    ("beep" "boop")
      (reply irc args "boop")
    ("help" "halp")
      (do
        (reply irc args "Currently, I support: hello beep halp votes")
        (reply irc args "More is coming soon!"))
    ("votes")
      (let [pizza_keys (keys (get @state :pizza_count))
            pizza_votes (get @state :pizza_count)]
        (reply irc args (vote-string pizza_keys pizza_votes)))
    (reply irc args "I don't know how to do that...")))

; Respond to master's request
(defn obey-master [irc args command]
  (case (first command)
    ; "vote" (vote (get command 1))
    "join"
      (let [channel (get command 1)]
        (join irc channel)
        (reply irc args (str "Joined " channel)))
    "vote"
      (do
        (vote! (get command 1))
        (let [pizza_keys (keys (get @state :pizza_count))
              pizza_votes (get @state :pizza_count)]
          (reply irc args (vote-string pizza_keys pizza_votes))))
    "clear" (clear-votes!)
    (obey-user irc args command master)))

; Message posted callback
(defn callback [irc args]
  (let [sender (string/lower-case (:nick args))
        tokens (vec (string/split (string/lower-case (:text args)) #" "))]

    ; Debugging
    (println sender "said" (string/join " " tokens))

    (let [subject (first tokens)
          command (vec (rest tokens))]
      ; Test if I am the subject
      (if (= subject (string/lower-case (str nick ":")))
        (do
          (println "I have been tasked with \"" command "\"")
          (if (= sender master)
            (obey-master irc args command)
            (obey-user irc args command sender)))))))

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
