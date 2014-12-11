(ns j3rnbot.core
  (:use
    [irclj.core]
    [clojure.pprint]
    [korma.db]
    [korma.core :exclude (join)])
  (:require
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

; Database settings
(def db-name  (get settings "db-name"))
(def db-user  (get settings "db-user"))
(def db-pass  (get settings "db-pass"))

; Setup database
(defdb db (postgres {:db db-name
                     :user db-user
                     :password db-pass}))

(defentity users)
(defentity votes)

; Compose a string summarizing the votes
(defn vote-string []
  (reduce-kv
    #(str %1 %2 ": " (count %3) " ")
    ""
    (group-by :item (select votes
                            (where {:old false})))))

; If it is not voted for, vote for it
; If it has been voted for, increment the votes
(defn vote! [nick item]
  (let [user (first
               (select users
                       (where {:nick nick})
                       (limit 1)))]
    (if user
      (if (= (count (select votes
                            (where {:user_id (:id user)
                                    :old false})))
             0)
        (do
          (insert votes
                (values {:user_id (:id user)
                         :item item}))
          (vote-string))
        "You have already voted!")
      "You are not whitelisted, sorry")))

; Clear all votes from the state
(defn clear-votes! []
  (do
    (update votes
            (set-fields {:old true})
            (where {:old false}))
    "Votes cleared"))

; Remove a single vote for the given item
; If the new count is 0, remove it from the count
(defn rm-vote! [nick]
  (let [user (first (select users (where {:nick nick})))]
    (if user
      (do
        (delete votes
          (where {:user_id (:id user)
                  :old false}))
        "Vote deleted")
      "You are not whitelisted!")))

(defn whitelist! [nick]
  (if (not (first (select users (where {:nick nick}))))
    (do
      (insert users
            (values {:nick nick}))
      (str nick " is now whitelisted"))
    (str nick " is already whitelisted")))


;;; Process revelant commands

; Respond to user's request
(defn obey-user [irc args tokens sender]
  (case (first tokens)
    ".votes"
      (reply irc args (vote-string))
    ".vote"
      (if (not (nil? (get tokens 1)))
        (reply irc args (vote! sender (get tokens 1))))
    ".rm-vote"
        (rm-vote! sender)
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
    ".whitelist"
      (let [nick (get tokens 1)]
        (reply irc args (whitelist! nick)))
    ".clear" (reply irc args (clear-votes!))
    ".die" (System/exit 0)
    (obey-user irc args tokens (string/lower-case master))))

; Messages directly to me
(defn respond [irc args tokens]
  (case (first tokens)
    ("hello" "hello!" "hi" "hi!") (reply irc args (str "Hello, " (:nick args)))
    ("beep" "boop") (reply irc args "boop")
    ("help" "halp")
      (do
        (reply irc args "Currently, I support: .votes .vote [item] .rm-vote")
        (reply irc args "More is coming soon!"))
    ()))

;;; Callback and start

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
