(ns j3rnbot.core
  (:require
    [irclj.core :refer :all]
    [clojure.pprint :refer :all]
    [clojure.data.json :as json]
    [clojure.string :as string]
    [clojure.core.match :refer (match)]))

; Load settings
(def settings
  (json/read-str
    (slurp "settings.json")))

; Constants
(def host     (get settings "server"))
(def port     (get settings "port"))
(def nick     (get settings "bot_nick"))
(def nick_length (count nick))
(def pass     (get settings "bot_pass"))
(def master   (get settings "master"))
(def channels (get settings "channels"))

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
(defn vote-string [pizza_votes]
  (if (= (count pizza_votes) 0)
    ""
    (let [this_key (first (keys pizza_votes))]
      (reduce
        str
        (str this_key ": " (get pizza_votes this_key) " ")
        (vote-string (dissoc pizza_votes this_key))))))

; Respond to user's request
(defn obey-user [irc args tokens sender]
  (case (first tokens)
    (".votes")
      (let [pizza_votes (get @state :pizza_count)]
        (reply irc args (vote-string pizza_votes)))
    ()))

; Respond to master's request
(defn obey-master [irc args tokens]
  (case (first tokens)
    ".join"
      (let [channel (get tokens 1)]
        (join irc channel)
        (reply irc args (str "Joined " channel)))
    ".leave"
      (let [channel (get tokens 1)]
        (part irc channel))
    ".vote"
      (if (not (nil? (get tokens 1)))
        (do
          (vote! (get tokens 1))
          (let [pizza_votes (get @state :pizza_count)]
            (reply irc args (vote-string pizza_votes)))))
    ".clear" (clear-votes!)
    ".die" (System/exit 0)
    (obey-user irc args tokens master)))

; Messages directly to me
(defn respond [irc args tokens]
  (case (first tokens)
    ("hello" "hello!" "hi" "hi!") (reply irc args (str "Hello, " (:nick args)))
    ("beep" "boop") (reply irc args "boop")
    ("help" "halp")
      (do
        (reply irc args "Currently, I support: .votes")
        (reply irc args "More is coming soon!"))
    ()))

; Message posted callback
(defn callback [irc args]
  ; Debugging
  (println (str "<" (:nick args) "> " (:text args)))

  ; Grab sender and tokens
  (let [sender (string/lower-case (:nick args))
        tokens (vec (string/split (string/lower-case (:text args)) #" "))]

    ; If I am the subject, find response
    ; Otherwise, determine whether it is master or not and obey
    (if (= (first tokens) (string/lower-case (str nick ":")))
      (respond irc args (rest tokens))
      (if (= sender (string/lower-case master))
        (obey-master irc args tokens)
        (obey-user irc args tokens sender)))))

; Main method
(defn start []
  ; Connnect to IRC
  (def connection (connect host port nick :callbacks {:privmsg callback}))
  (identify connection pass)
  (doseq [channel channels]
    (join connection channel)
    (println "Joined " channel)))
