(ns org.pupcus.i18n.middleware
  (:require [org.pupcus.i18n.locale :refer [as-locale with-locale]]))

(defn locale-from-header
  "Try to discern the locale from headers values in the request"
  [{:keys [headers]}]
  (when headers
    (or (headers "Accept-Language")
        (headers "accept-language"))))

(defn locale-from-param
  "Try to discen the locale from a 'loacle' named parameter"
  [{:keys [query-string params]}]
  (or (:locale params)
      (when params (params "locale"))
      (when query-string (when-let [[_ locale] (re-find #"locale=([^&]*)" query-string)] locale))))

(defn locale-from-session
  "Try to discern the locale from any :locale stored in the session"
  [{:keys [session] :as req}]
  (:locale session))

(defn locale-from-domain
  "Try to discern the locale from the server-name url in the request"
  [{:keys [server-name]}]
  (when server-name
    (when-let [[_ locale] (last (re-seq  #"\.([^\.]{2})(?:[.]|$)" server-name))]
      locale)))

(defn wrap-i18n

  "Tries to determine the locale of the request and sets a :locale value in the request map
   as well as binding a thread-local var that is used as a 'default' for all key lookups
   and message formatting. Note that this locale can be overrriden at the function call
   level if needed.

   Accepts an options for setting the default locale

      :default     the default locale (or the java.util.Locale/getDefault if nil)

   On a request if no locale can be gathered from the locale-fns, the the :default locale will
   be used"

  [handler & {:keys [default] :or {default (str (java.util.Locale/getDefault))}}]
  (let [loc-str-fn (apply some-fn [locale-from-header locale-from-param locale-from-session locale-from-domain])]
    (fn [req]
      (let [loc-str (or (loc-str-fn req) default)]
        (with-locale loc-str
          (handler (assoc req :locale (as-locale loc-str))))))))
