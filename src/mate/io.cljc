(ns mate.io
  (:require [clojure.string :as str]
            #?(:clj [clojure.java.io :as io])
            #?(:clj [clojure.data.json :as json]))
  #?(:cljs (:require-macros [mate.io])))

(defn resource-path
  "Returns the path to a resource.

   If the resource name starts with a '/', the path is
   the resource name with a dropped leading \"/\".
   Otherwise, the path is built using the parent directory
   of the namespace from which this macro is invoked."
  [namespace resource-name]
  (if (str/starts-with? resource-name "/")
    (subs resource-name 1) ;; drop the leading "/"
    (-> (name (ns-name namespace))
        (str/split #"\.")
        butlast
        (->> (str/join "/"))
        (str/replace #"-" {"-" "_"})
        (str "/" resource-name))))

#?(:clj
   (defmacro inline-resource
     "Inlines the content of a resource."
     [resource-name]
     (let [resource-name (eval resource-name)
           path (resource-path *ns* resource-name)
           resource (io/resource path)]
       (assert (some? resource)
               (str "Resource \"" path "\" not found."))
       (slurp resource))))

#?(:clj
   (defmacro inline-json-resource
     "Inlines the JSON content of a resource as EDN."
     [resource-name]
     (let [resource-name (eval resource-name)
           path (resource-path *ns* resource-name)
           resource (io/resource path)]
       (assert (some? resource)
               (str "Resource \"" path "\" not found."))
       (-> (slurp resource)
           json/read-json))))
