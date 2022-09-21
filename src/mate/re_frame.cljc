(ns mate.re-frame)

;; The functions below are used for composing together different functions
;; inside a re-frame event handler via the thread macro `->`.
;; The `effects` parameter is what is returned by a typical re-frame event handlers,
;; and has the general shape `{:db db, :fx [...]}`.

;; This pattern hopes to encourage:
;; - grouping together different changes inside the event-handler, reducing the usage of `{:fx [[:dispatch next-thing-to-do-event]]}`,
;; - defining the logic in top-level functions outside of the `(rf/reg-event-fx ...)` expressions.

(defn update-db
  "Update the value of `:db` in the `effects` hashmap."
  [effects f & args]
  (apply update effects :db f args))

(defn into-fx
  "Appends a collection of effects to the value of `:fx` in the `effects` hashmap."
  [effects fxs]
  (update effects :fx (fnil into []) (remove nil?) fxs))

(defn conj-fx
  "Appends one or more effects to the value of `:fx` in the `effects` hashmap."
  ([effects fx]
   (cond-> effects
     (some? fx)
     (update :fx (fnil conj []) fx)))
  ([effects fx & fxs]
   (into-fx effects (cons fx fxs))))

(defn conj-fx-using-db
  "Invoke `f` with the value of `:db` from the `effects` hashmap and conj its result
   to the `:fx` collection in `effects`."
  [effects f & args]
  (conj-fx effects (apply f (:db effects) args)))

(defn into-fx-using-db
  "Invoke `f` with the value of `:db` from the `effects` hashmap and appends its resulting collection
   into the `:fx` collection in `effects`."
  [effects f & args]
  (into-fx effects (apply f (:db effects) args)))
