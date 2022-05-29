(ns mate.io-test
  (:require [clojure.test :refer [deftest is testing]]
            [mate.io :as mio]
            #?(:clj [clojure.data.json :as json])))

(deftest inline-resource-test
  (testing "Finds the resource using a relative path."
    (is (= (mio/inline-resource (str "./" "test-resource.txt"))
           "Content of test-resource.txt")))

  (testing "Finds the resource using an absolute path."
    (is (= (mio/inline-resource (str "mate/" "test-resource.txt"))
           "Content of test-resource.txt")))

  (testing "Reads and transform the content using multiple arguments."
    (is (= (mio/inline-resource "./test-resource.txt"
                                str " FOO" " BAR")
           "Content of test-resource.txt FOO BAR")))

  (testing "Reads the JSON content as string and parse it to get a Clojure data structure."
    (is (= (mio/inline-resource "./test-resource.json"
                                json/read-json)
           [{:a 1} "hello"]))))
