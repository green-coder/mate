(ns mate.re-frame-test
  (:require [clojure.test :refer [deftest is testing]]
            [mate.re-frame :as mr]))

(defn- store-user-in-db [db user-id user-name access-token refresh-token]
  (update db :user assoc
          :id user-id
          :name user-name
          :access-token access-token
          :refresh-token refresh-token))

(defn- user-logged-in-notification-fx [db]
  [:notification {:level :info
                  :message (str (-> db :user :name)
                                " logged in")}])

(defn- load-user-data-fx [db]
  [:http-get (-> db :user :id) [:picture-url :accepted-end-user-agreement? :preferred-books]])

;; The composing functions can be used at any level in your code, just keep using the -> pattern
;; to keep things tidy (or not).
(defn- complicated-handler [effects foo-value]
  (-> effects
      (mr/update-db assoc :foo foo-value)
      (mr/conj-fx [:foo-system-changed])))

(deftest usage-example
  (let [db {:foo "bar"}]
    (is (= {:db {:foo "new-bar"
                 :user {:id 1
                        :name "Alice"
                        :access-token 123
                        :refresh-token 456}}
            :fx [[:sound :login-success]
                 [:notification {:level :info
                                 :message "Alice logged in"}]
                 [:http-get 1 [:picture-url :accepted-end-user-agreement? :preferred-books]]
                 [:foo-system-changed]]}
           (-> {:db db}
               (mr/conj-fx [:sound :login-success])
               (mr/update-db store-user-in-db 1 "Alice" 123 456)
               (mr/conj-fx-using-db user-logged-in-notification-fx)
               (mr/conj-fx-using-db load-user-data-fx)
               (complicated-handler "new-bar"))))))
