(ns org.pupcus.i18n.middleware-test
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]
            [ring.mock.request :as mock]
            [org.pupcus.i18n.bundle :refer [create-bundle-manager]]
            [org.pupcus.i18n.middleware :refer :all]
            [clojure.test :refer :all]))

(def messages (create-bundle-manager :messages ["properties"]))

(defroutes test-routes
  (GET "/*" req (resp/response (str "Color=[" (messages :color) "]"))))

(def app1
  (-> #'test-routes
      wrap-i18n))

(def app2
  (-> #'test-routes
      (wrap-i18n :default :de_DE)))

(defn simple-request []
  {:uri "/test" :request-method :get})

(defn accept-language-request [lang]
  (merge (simple-request) {:headers {"accept-language" lang}}))

(defn query-string-locale-request [lang]
  (merge (simple-request) {:query-string (str "locale=" lang)}))

(defn params-locale-request [lang]
  (merge (simple-request) {:params {:locale lang}}))

(defn session-locale-request [lang]
  (merge (simple-request) {:session {:locale lang}}))

(defn domain-locale-request [lang]
  (merge (simple-request) {:server-name (str "domain." lang)}))

(defn request [app req ]
  (app req))


(deftest test-wrap-i18n-with-default-locale
  (is (= "Color=[purple]"  (:body (request app1 (simple-request)))))

  (is (= "Color=[purpura]" (:body (request app1 (accept-language-request "es")))))
  (is (= "Color=[purpura]" (:body (request app1 (query-string-locale-request "es")))))
  (is (= "Color=[purpura]" (:body (request app1 (params-locale-request "es")))))
  (is (= "Color=[purpura]" (:body (request app1 (session-locale-request "es")))))
  (is (= "Color=[purpura]" (:body (request app1 (domain-locale-request "es")))))

  (is (= "Color=[purpurne]" (:body (request app1 (accept-language-request "de_DE")))))
  (is (= "Color=[purpurne]" (:body (request app1 (query-string-locale-request "de_DE")))))
  (is (= "Color=[purpurne]" (:body (request app1 (params-locale-request "de_DE")))))
  (is (= "Color=[purpurne]" (:body (request app1 (session-locale-request "de_DE"))))))


(deftest test-wrap-i18n-with-de_DE-locale
  (is (= "Color=[purpurne]"  (:body (request app2 (simple-request)))))

  (is (= "Color=[purpura]" (:body (request app2 (accept-language-request "es")))))
  (is (= "Color=[purpura]" (:body (request app2 (query-string-locale-request "es")))))
  (is (= "Color=[purpura]" (:body (request app2 (params-locale-request "es")))))
  (is (= "Color=[purpura]" (:body (request app2 (session-locale-request "es")))))
  (is (= "Color=[purpura]" (:body (request app2 (domain-locale-request "es")))))

  (is (= "Color=[purple]" (:body (request app2 (accept-language-request "en")))))
  (is (= "Color=[purple]" (:body (request app2 (query-string-locale-request "en")))))
  (is (= "Color=[purple]" (:body (request app2 (params-locale-request "en")))))
  (is (= "Color=[purple]" (:body (request app2 (session-locale-request "en")))))
  (is (= "Color=[purple]" (:body (request app2 (domain-locale-request "e2"))))))
