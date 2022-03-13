(ns mate.io-test
  (:require [clojure.test :refer [deftest is testing]]
            [mate.io :as mio]))

(deftest inline-resource-test
  (testing "if we can find the resource using a relative path."
    (is (= (mio/inline-resource (str "test-re" "source.txt"))
           "Content of test-resource.txt")))

  (testing "if we can find the resource using an absolute path."
    (is (= (mio/inline-resource (str "/mate/test-re" "source.txt"))
           "Content of test-resource.txt")))

  (testing "if we can find the JSON resource using a relative path."
    (is (= (mio/inline-json-resource (str "test-json-re" "source.txt"))
           [{:a 1} "hello"])))

  (testing "if we can find the JSON resource using an absolute path."
    (is (= (mio/inline-json-resource (str "/mate/test-json-re" "source.txt"))
           [{:a 1} "hello"]))))
