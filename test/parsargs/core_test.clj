(ns parsargs.core-test
  (:use clojure.test)
  (:require [parsargs.core :as p]))


(deftest value-test
  (is (= :foo
         (p/parse (p/value keyword?) [:foo])))
  (is (thrown? IllegalArgumentException
               (p/parse (p/value keyword?) ["foo"]))))


(deftest optval-test
  (is (= nil
         (p/parse (p/optval keyword?) [])))
  (is (= :foo
         (p/parse (p/optval keyword? :foo) [])))
  (is (= :bar
         (p/parse (p/optval keyword? :foo) [:bar]))))


(deftest omit-test
  (is (= nil
         (p/parse (p/omit keyword?) [:foo])))
  (is (thrown? IllegalArgumentException
               (p/parse (p/omit keyword?) ["foo"]))))


(deftest some-test
  (testing "Without bounds"
    (is (= []
           (p/parse (p/some (p/value string?)) [])))
    (is (= ["foo" "bar"]
           (p/parse (p/some (p/value string?)) ["foo" "bar"])))
    (is (= {:strings ["foo"] :k :bar}
           (p/parse (p/sequence :strings (p/some (p/value string?))
                                :k (p/value keyword?)) ["foo" :bar]))))
  (testing "With bounds"
    (is (thrown? IllegalArgumentException
                 (p/parse (p/some 1 2 (p/value keyword?)) [])))
    (is (thrown? IllegalArgumentException
                 (p/parse (p/some 1 2 (p/value keyword?)) [:foo :bar :baz])))))


(deftest sequence-test
  (let [parser (p/sequence :foo (p/value string?)
                           :bar (p/optval number?))]
    (are [r input] (= r (p/parse parser input))
         {:foo "FOO" :bar 42}  ["FOO" 42]
         {:foo "FOO"} ["FOO"])
    (are [invalid-input] (thrown? IllegalArgumentException
                                   (p/parse parser invalid-input))
         []
         [:foo]
         ["FOO" :baz])))


(deftest alternative-test
  (let [parser (p/some (p/alternative (p/value keyword?) (p/value number?)))]
    (are [r input] (= r (p/parse parser input))
         [] []
         [:foo 42 :bar :baz] [:foo 42 :bar :baz])
    (are [invalid-input] (thrown? IllegalArgumentException
                                  (p/parse parser invalid-input))
         [:foo 42 "BAZ"]
         [nil])))


(deftest descent-with-test
  (let [sub-parser (p/some (p/value string?))
        parser (p/sequence :foo (p/value string?)
                           :bar (p/descent-with sub-parser))]
    (is (= {:foo "FOO" :bar ["baz" "baf"]}
           (p/parse parser ["FOO" ["baz" "baf"]])))))


(deftest complex-test
  (let [parser (p/some
                (p/sequence :data-path (p/alternative
                                        (p/value vector?)
                                        (p/value keyword?))
                            :formatter (p/optval fn? str)
                            :signal-path (p/alternative
                                          (p/value #(and (vector? %) (string? (last %))))
                                          (p/value string?))
                            :parser (p/optval fn? identity)))]
    (are [r input] (= r (p/parse parser input))
         [{:data-path [:foo]
           :formatter str
           :signal-path "firstname"
           :parser identity},
          {:data-path :bar
           :formatter clojure.string/upper-case
           :signal-path "lastname"
           :parser identity}
          {:data-path :baz
           :formatter str
           :signal-path [:data "zipcode"]
           :parser identity}] [[:foo] "firstname"
                               :bar clojure.string/upper-case "lastname"
                               :baz [:data "zipcode"]])
    (are [invalid-input] (thrown? IllegalArgumentException
                                  (p/parse parser invalid-input))
         ["foo"]
         [:bar [:foo :baz]])))
