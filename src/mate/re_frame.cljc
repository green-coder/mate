(ns mate.re-frame)

;; The functions below are used for composing together different functions
;; inside a re-frame event handler via the thread macro `->`.
;; The `effects` parameter is what is returned by a typical re-frame event handlers,
;; and has the general shape `{:db db, :fx [...]}`.

;; This pattern hopes to encourage:
;; - grouping together different changes inside the event-handler, reducing the usage of `{:fx [[:dispatch next-thing-to-do-event]]}`,
;; - defining the logic in top-level functions outside of the `(rf/reg-fx ...)` expressions.

(defn update-db
  "Update the `:db` in the `effects` structure."
  [effects f & args]
  (apply update effects :db f args))

(defn conj-fx
  "Appends an effect to `:fx` the `effects` structure."
  [effects fx]
  (cond-> effects
    (some? fx)
    (update :fx (fnil conj []) fx)))

(defn conj-fx-using-db
  "Invoke `f` with the `:db` in `effects` and appends it to its `:fx`."
  [effects f & args]
  (conj-fx effects (apply f (:db effects) args)))
