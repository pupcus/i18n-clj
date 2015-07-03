(ns org.pupcus.i18n.utils
  (:refer-clojure :exclude [format])
  (:require [clojure.pprint :as pprint]
            [org.pupcus.i18n.locale :refer [*locale* as-locale valid-locale?]]))

(defn refresh
  "Clears the map of resource bundles stored in the bundle component."
  [{:keys [bundles] :as bundle}]
  (when bundles
    (dosync (ref-set bundles {}))) bundle)

(defn- format* [f bundle key args]
  (if-let [arg1 (first args)]
    (let [locale-as-arg1? (valid-locale? arg1)
          locale          (if locale-as-arg1? (as-locale arg1) (or *locale* (java.util.Locale/getDefault)))
          args            (if locale-as-arg1? (rest args) args)
          s               (bundle key locale)]
      (apply f s args))
    (bundle key)))

(defn format
  "Produce a message based on a format message located at 'key' in the bundle.

   Takes an optional locale and then args to be used in the format string."
  [bundle key & args]
  (format*
   clojure.core/format
   bundle key args))

(defn message-format
  "Produce a message based on a java MessageFormat message located at 'key' in the bundle.

   Takes an optional locale and then args to be used in the MessageFormat string."
  [bundle key & args]
  (format*
   (fn [s & _args] (.format (java.text.MessageFormat. s) (into-array Object _args)))
   bundle key args))

(defn cl-format
  "Produce a message based on a common lisp format message located at 'key' in the bundle.

   Takes an optional locale and then args to be used in the common lisp format string."
  [bundle key & args]
  (format*
   (fn [s & _args] (apply pprint/cl-format nil s _args))
   bundle key args))
