(ns mate.core-test
  (:require [clojure.test :refer [deftest testing is are]]
            [mate.core :as m]))

(deftest implies-test
  (is (true?  (m/implies true  true)))
  (is (false? (m/implies true  false)))
  (is (true?  (m/implies false true)))
  (is (true?  (m/implies false false))))

(deftest seq-index-test
  (is (= (m/seq-indexed [:a :a :b :a :b])
         '([0 :a] [1 :a] [2 :b] [3 :a] [4 :b]))))

(deftest comp->-test
  (is (= ((m/comp-> inc str) 2) "3")))

(deftest group-by-test
  (let [coll [[:a 1] [:a 2] [:b 3] [:a 4] [:b 5]]]
    (is (= (m/group-by first coll)
           {:a [[:a 1] [:a 2] [:a 4]]
            :b [[:b 3] [:b 5]]}))

    (is (= (m/group-by first second coll)
           {:a [1 2 4]
            :b [3 5]}))

    (is (= (m/group-by first second + coll)
           {:a 7
            :b 8}))

    (is (= (m/group-by first second + 10 coll)
           {:a 17
            :b 18}))))

(deftest index-by-test
  (let [coll [[:a 1] [:a 2] [:b 3] [:a 4] [:b 5]]]
    (is (= (m/index-by first coll)
           {:a [:a 4]
            :b [:b 5]}))

    (is (= (m/index-by first second coll)
           {:a 4
            :b 5}))))

(deftest ungroup-keys-test
  (is (= {:a      1
          :b      1
          :c      2
          :d      3
          :e      3
          {:f :g} 4}
         (m/ungroup-keys {:a       "this value will be overwritten"
                          [:a :b]  1
                          :c       2
                          #{:d :e} 3
                          {:f :g}  4}))))
