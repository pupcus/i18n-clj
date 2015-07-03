(ns org.pupcus.i18n.locale
  (:import java.util.Locale))

(def ^:dynamic *locale* nil)

(defprotocol IAsLocale
  (as-locale [this]))

(let [locales (reduce
               (fn [m loc]
                 (let [k (str loc)]
                   (if-not (empty? k)
                     (assoc m (str loc)
                            loc) m)))
               {}
               (Locale/getAvailableLocales))]

  (extend-protocol IAsLocale
    java.lang.String
    (as-locale [s]
      (or (get locales s)
          (Locale. s)))

    clojure.lang.Keyword
    (as-locale [k]
      (as-locale (name k)))

    java.util.Locale
    (as-locale [l] l)))

(defprotocol IValidLocale
  (valid-locale? [this]))

(let [choices (into (set (Locale/getISOLanguages))
                    (filter not-empty (map str (Locale/getAvailableLocales))))]

  (extend-protocol IValidLocale

    java.lang.String
    (valid-locale? [s]
      (choices s))

    clojure.lang.Keyword
    (valid-locale? [k]
      (valid-locale? (name k)))

    java.util.Locale
    (valid-locale? [l] true)

    java.lang.Object
    (valid-locale? [o] nil)))

(defmacro with-locale
  "Wraps all subsequent call to use the given locale"
  [loc & body]
  `(binding [~'org.pupcus.i18n.locale/*locale* (as-locale ~loc)]
     (do
       ~@body)))
