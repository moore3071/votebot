(ns j3rnbot.core-test
  (:require [clojure.test :refer :all]
            [j3rnbot.core :refer :all]))

(deftest whitelist-test
  (testing "basic whitelisting"
    (is (= (whitelist! "J3RN") "J3RN is now whitelisted")))
  (testing "cannot whitelist twice"
    (is (= (whitelist! "J3RN") "J3RN is already whitelisted")))
  (testing "cannot whitelist too long nick"
    (is (=
          (whitelist! "1234567890123456789012345678901")
          "That nick is wayyyy too long"))))

