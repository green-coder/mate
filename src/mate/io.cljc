(ns mate.io
  (:require [clojure.string :as str]
            #?(:clj [clojure.java.io :as io]))
  #?(:cljs (:require-macros [mate.io])))

(defn- resource-path
  "Returns the path of a resource.

   If `resource-name` starts with a './', the path is built
   using the parent directory of `namespace-obj`."
  [namespace-obj resource-name]
  (if (str/starts-with? resource-name "./")
    (let [namespace-str (name (ns-name namespace-obj))
          namespace-dir-path (-> namespace-str
                                 (str/split #"\.")
                                 butlast
                                 (->> (str/join "/"))
                                 (str/replace #"-" {"-" "_"}))]
      (str namespace-dir-path "/" (subs resource-name (count "./"))))
    resource-name))

#_(resource-path *ns* "foo")
#_(resource-path *ns* "./foo")

#?(:clj
   (defn inline-resource*
     ([namespace-obj resource-name]
      (inline-resource* namespace-obj resource-name identity))
     ([namespace-obj resource-name f & args]
      (let [resource-name (eval resource-name)
            path (resource-path namespace-obj resource-name)
            resource (io/resource path)
            _ (assert (some? resource)
                      (str "Resource at path \"" path "\" not found."))
            resource-content (slurp resource)]
        (apply f resource-content args)))))

#_(inline-resource* *ns* "./test-resource.txt")
#_(inline-resource* *ns* "./test-resource.txt" str/upper-case)

#?(:clj
   (defmacro inline-resource
     "Inlines the content of a resource at \"macro expansion\" time,
      optionally transformed by `f & args` in the same way that using `clojure.core/update`."
     ([resource-name]
      (inline-resource* *ns* (eval resource-name)))
     ([resource-name f & args]
      (apply inline-resource*
             *ns*
             (eval resource-name)
             (eval f)
             (map eval args)))))
