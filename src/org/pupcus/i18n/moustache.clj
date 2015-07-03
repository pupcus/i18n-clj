(ns org.pupcus.i18n.moustache
  (:require [clostache.parser]
            [org.pupcus.i18n.locale :refer [as-locale valid-locale? *locale*]]))

(defn- build-value-map [bundle loc s]
  (let [keys (->> s
                  (re-seq #"\{\{([^!].*?)\}\}")
                  (map second)
                  (map #(keyword (clojure.string/replace % #"[\/#&^{]" "")))
                  set)
        look-for  (fn [k] (try (bundle k loc) (catch Exception e)))
        add-value (fn [m k]
                    (if-let [v (look-for k)]
                      (assoc m k v)
                      m))]
    (reduce add-value {} keys)))

(defn moustache
  "Produce a message based on a format message located at 'key'in the
   bundle.

   The moustache keys found in the message string are looked up in
   the bundle as well.  Any values passed in via the map override
   any that are found.

   Takes an optional locale and then args to be used in the format string."
  ([bundle key]   (moustache bundle key {}))
  ([bundle key m] (moustache bundle key (or *locale* (java.util.Locale/getDefault)) m))
  ([bundle key loc m]
   (let [s (bundle key (as-locale loc))]
     (clostache.parser/render s (merge (build-value-map bundle loc s) m)))))
