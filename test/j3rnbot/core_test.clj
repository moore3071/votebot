(ns j3rnbot.core-test
  (:require [clojure.test :refer :all]
            [j3rnbot.core :refer :all]))

(deftest test-parse-command
  (testing "Test command parsing with colon"
    (is (=
         (parse-command "j3rnbot: hello")
         "hello")))
  (testing "Test command parsing without colon"
    (is (=
         (parse-command "j3rnbot hello")
         "hello"))))
