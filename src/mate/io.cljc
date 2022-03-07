(ns mate.io
  (:require [clojure.string :as str]
            #?(:clj [clojure.java.io :as io]))
  #?(:cljs (:require-macros [mate.io])))

#?(:clj
   (defmacro inline-resource
     "Inline the content of the resource named `resource-name`.

      If the resource name starts with a '/', the resource is opened
      as usual by dropping the '/'.
      Otherwise, it is opened from parent directory of the namespace
      from which this macro is invoked.
      "
     [resource-name]
     (let [resource-name (eval resource-name)
           path (if (str/starts-with? resource-name "/")
                  (subs resource-name 1) ;; drop the leading "/"
                  (-> (name (ns-name *ns*))
                      (str/split #"\.")
                      butlast
                      (->> (str/join "/"))
                      (str/replace #"-" {"-" "_"})
                      (str "/" resource-name)))
           resource (io/resource path)]
       (assert (some? resource)
               (str "Resource \"" path "\" not found."))
       (slurp resource))))
