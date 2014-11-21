(ns j3rnbot.core-test
  (:require [clojure.test :refer :all]
            [j3rnbot.core :refer :all]))

(deftest test-vote-single
  (do
    (vote! "pep")
    (is (get-in @state [:pizza_count "pep"]) 1)))

(deftest test-vote-multi
  (do
    (vote! "pep")
    (vote! "pep")
    (is (get-in @state [:pizza_count "pep"]) 2)))

(deftest test-vote-poly
  (do
    ; Vote
    (vote! "pep")
    (vote! "cheese")
    ; Test that votes are recorded
    (is (contains? (get @state :pizza_count) "pep") true)
    (is (contains? (get @state :pizza_count) "cheese") true)
    (is (get-in @state [:pizza_count "pep"]) 1)
    (is (get-in @state [:pizza_count "cheese"]) 1)))

(deftest test-clear-votes
  (do
    ; Vote
    (vote! "pep")
    (vote! "cheese")
    ; Clear Votes
    (clear-votes!)
    ; Test votes cleared
    (is (count (get-in @state [:pizza_count])) 0)))

(deftest test-can-revote
  (do
    (clear-votes!)
    (vote! "pep")
    (is (get-in @state [:pizza_count "pep"]) 1)))

(deftest test-vote-string
  (is
    (vote-string {"pep" 2 "cheese" 1})
    "pep: 2 cheese: 1 "))

(deftest test-vote-string-empty
  (is
    (vote-string {})
    ""))

(deftest rm-vote
  (do
    (vote! "accident")
    (is (vote-string (get @state :pizza_count)) "accident: 1")
    (rm-vote! "accident")
    (is (vote-string (get @state :pizza_count)) "")))
