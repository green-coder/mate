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

(defn- save-to-royal-storage [db]
  [[:save-to-royal-storage (:queen-browsing-history db)]
   [:one-minute-silence]])

(deftest update-db-test
  (is (= {:db {:foo "bar"
               :a {:b 1}}}
         (-> {:db {}}
             (mr/update-db assoc :foo "bar")
             (mr/update-db update-in [:a :b] (fnil inc 0))))))

(deftest into-fx-test
  (is (= {:db {:foo "bar"}
          :fx []}
         (-> {:db {:foo "bar"}}
             (mr/into-fx nil))))
  (is (= {:db {:foo "bar"}
          :fx []}
         (-> {:db {:foo "bar"}}
             (mr/into-fx [nil]))))
  (is (= {:db {:foo "bar"}
          :fx [[:foo-fx 1]
               [:foo-fx 2]]}
         (-> {:db {:foo "bar"}}
             (mr/into-fx [[:foo-fx 1]
                          nil
                          [:foo-fx 2]])))))

(deftest conj-fx-test
  (is (= {:db {:foo "bar"}}
         (-> {:db {:foo "bar"}}
             (mr/conj-fx nil))))
  (is (= {:db {:foo "bar"}
          :fx [[:foo-fx 1]]}
         (-> {:db {:foo "bar"}}
             (mr/conj-fx [:foo-fx 1]))))
  (is (= {:db {:foo "bar"}
          :fx [[:foo-fx 1]
               [:bar-fx 2]]}
         (-> {:db {:foo "bar"}}
             (mr/conj-fx [:foo-fx 1]
                         [:bar-fx 2])))))

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
                 [:save-to-royal-storage (:queen-browsing-history db)]
                 [:one-minute-silence]
                 [:foo-system-changed]]}
           (-> {:db db}
               (mr/conj-fx [:sound :login-success])
               (mr/update-db store-user-in-db 1 "Alice" 123 456)
               (mr/conj-fx-using-db user-logged-in-notification-fx)
               (mr/conj-fx-using-db load-user-data-fx)
               (mr/into-fx-using-db save-to-royal-storage)
               (complicated-handler "new-bar"))))))
