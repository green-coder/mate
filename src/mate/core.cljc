(ns mate.core
  #?(:cljs (:require-macros mate.core))
  (:refer-clojure :exclude [group-by])
  (:require [clojure.core :as cc]))

(defmacro implies
  "`(implies x y)` expands to `(or (not x) y)` while being more
   descriptive of the logical intent."
  [x y]
  `(or (not ~x) ~y))

(defn seq-indexed
  "Returns an indexed sequence from the collection `coll`."
  [coll]
  (map-indexed vector coll))

#?(:clj
   (defn indexed-re-groups
     "Same as re-groups, but returns pair(s) `[index group]` instead of group(s)."
     [m]
     (let [group-count (.groupCount m)]
       (if (zero? group-count)
         [(.start m) (.group m)]
         (loop [result []
                index 0]
           (if (<= index group-count)
             (recur (conj result [(.start m index) (.group m index)])
                    (inc index))
             result))))))

#?(:cljs
   (defn re-with-flags
     "Returns a new RegEx with additional flags."
     [^js re flags]
     (js/RegExp. (.-source re) (str (.-flags re) flags))))

#?(:clj
   (defn indexed-re-find
     "Same as re-find, but returns a pair `[index match]` when there is a match."
     ([^java.util.regex.Matcher m]
      (when (.find m)
        (indexed-re-groups m)))
     ([^java.util.regex.Pattern re s]
      (let [m (re-matcher re s)]
        (indexed-re-find m))))

   :cljs
   (defn indexed-re-find
     "Same as re-find, but returns a pair `[index match]` when there is a match."
     [^js re s]
     (when-some [^array m (.exec re s)]
       (let [group-count (count m)]
         (if (== group-count 1)
           [(.-index m) (aget m 0)]
           (loop [result []
                  index 0]
             (if (< index group-count)
               (recur (conj result [(some-> (.-indices m) (aget index 0))
                                    (aget m index)])
                      (inc index))
               result)))))))

(defmacro comp->
  "Same as `comp` but with the arguments in reverse order."
  [& args]
  `(comp ~@(reverse args)))

(defmacro if->
  "If branching threading macro."
  [x test then-form else-form]
  `(let [val# ~x]
     (if ~test
       (-> val# ~then-form)
       (-> val# ~else-form))))

(defmacro let->
  "Let threading macro."
  [x bindings body]
  `(let [val# ~x]
     (let ~bindings
       (-> val# ~body))))

(defn group-by
  "Same as clojure.core/group-by, but with some handy new arities which apply
   custom map & reduce operations to the elements grouped together under the same key."
  ([kf coll]
   ;(group-by kf identity conj [] coll)
   (cc/group-by kf coll))
  ([kf vf coll]
   (group-by kf vf conj [] coll))
  ([kf vf rf coll]
   (group-by kf vf rf (rf) coll))
  ([kf vf rf init coll]
   (->> coll
        (reduce (fn [ret x]
                  (let [k (kf x)
                        v (vf x)]
                    (assoc! ret k (rf (get ret k init) v))))
                (transient {}))
        persistent!)))

(defn index-by
  "Returns a hashmap made of `[key value]` pairs from items in the collection `coll`
   where the keys are `(kf item)` and the values are `(vf item)`.
   `vf` defaults to the identify function."
  ([kf coll]
   (index-by kf identity coll))
  ([kf vf coll]
   (->> coll
        (reduce (fn [ret x]
                  (assoc! ret (kf x) (vf x)))
                (transient {}))
        persistent!)))

(defn ungroup-keys
  "From a hashmap where the keys are grouped using sequential collections (lists, vectors ...) or using sets,
   this function returns a hashmap where those keys are ungrouped."
  [m]
  (into {}
        (mapcat (fn [[k v]]
                  (if (or (sequential? k)
                          (set? k))
                    (mapv (fn [k-item]
                            [k-item v])
                          k)
                    [[k v]])))
        m))
