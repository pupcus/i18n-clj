(ns org.pupcus.i18n.bundle
  (:refer-clojure :exclude [get load])
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [org.pupcus.i18n.value :as value]
            [org.pupcus.i18n.locale :refer [*locale* as-locale valid-locale?]])
  (:import org.pupcus.i18n.ResourceBundle))

(defprotocol IBundle
  (locate [this locale]))

(defn get-resource-name
  [^java.util.ResourceBundle$Control rbc basename locale format]
  (let [bundle-name (.toBundleName rbc basename locale)]
    (.toResourceName rbc bundle-name format)))

(defn get-stream [resource-name reload]
  (if reload
    (when-let [url (io/resource resource-name)]
      (when-let [connection (.openConnection url)]
        (.setUseCaches connection false)
        (io/input-stream (.getInputStream connection))))
    (io/input-stream (io/resource resource-name))))

(defn control [formats]
  (let [fs (into #{} formats)]
    (proxy [java.util.ResourceBundle$Control] []
      (needsReload   [basename locale format loader bundle loadTime]
                     (let [^java.util.ResourceBundle$Control this this]
                       (or (= java.util.Locale/ROOT locale)
                           (proxy-super needsReload basename locale format loader bundle loadTime))))
      (getTimeToLive [basename locale] 0)
      (getFormats    [basename] (doto (java.util.ArrayList.) (.addAll formats)))
      (newBundle     [basename locale format loader reload]
                     (when (fs format)
                       (let [resource-name (get-resource-name this basename locale format)]
                         (with-open [bis ^java.io.BufferedInputStream (get-stream resource-name reload)]
                           (when bis
                             (org.pupcus.i18n.ResourceBundle. format bis)))))))))

(defn get-bundle [basename locale formats]
  (try
    (java.util.ResourceBundle/getBundle ^java.lang.String (name basename)
                                        ^java.util.Locale locale
                                        ^java.util.ResourceBundle$Control (control formats))
    (catch Exception e
      (log/info (.getMessage e))
      (log/debug e e))))

(defrecord BundleManager [basename formats]
  component/Lifecycle
  (start [{:keys [basename] :as this}]
    (log/info (format "start BundleManager with basename [%s]" basename))
    (assoc this :bundles (ref {}) :basename (if (keyword? basename) basename (keyword basename))))

  (stop [{:keys [basename] :as  this}]
    (log/info (format "stop BundleManager with basename [%s]" basename))
    (dissoc this :bundles :basename))

  IBundle
  (locate
   [{:keys [bundles basename formats] :as this} locale]
   (if-let [bundle (get-in @bundles [(keyword (str locale))])]
     bundle
     (when-let [bundle (get-bundle basename locale formats)]
       (dosync (commute bundles assoc (keyword (str locale)) bundle))
       bundle)))

  clojure.lang.IFn
  (invoke
   [this key]
   (this key (or *locale* (java.util.Locale/getDefault))))
  (invoke
   [this key locale]
   (let [locale (as-locale locale)]
     (when-let [rb (locate this locale)]
       (value/value rb key)))))

(defn create-bundle-manager
  "build a bundle manager for the given args:

   basename: string (or keyword) that defines the base name of the file ex. \"messages\"
    formats: vector of string extensions for the file format ex. [\"yml\" \"yaml\"]"
  [basename formats]
  (component/start (BundleManager. basename formats)))
