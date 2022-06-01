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

(defmacro comp->
  "Same as `comp` but with the arguments in reverse order."
  [& args]
  `(comp ~@(reverse args)))

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
