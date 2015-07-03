(ns org.pupcus.i18n.ResourceBundle
  (:require [clojure.tools.reader.edn :as edn]
            [clj-yaml.core :refer [decode]]
            [org.pupcus.i18n.value :as value])
  (:import org.yaml.snakeyaml.Yaml)
  (:gen-class
   :name org.pupcus.i18n.ResourceBundle
   :extends java.util.ResourceBundle
   :prefix rb-
   :init   init
   :state  state
   :exposes {parent {:get getParent}}
   :constructors {[java.lang.String java.io.InputStream] []}))

(defn load-properties  [^java.io.InputStream is]
  (let [p (doto (java.util.Properties.) (.load is))]
    (reduce (fn [m [k v]] (assoc m k v)) {} p)))

(defn load-yaml [^java.io.InputStream is]
  (decode (.load (Yaml.) is)))

(defn load-edn [^java.io.InputStream is]
  (edn/read-string (slurp is)))

(defn init-state-dispatch-fn [format _]
  (keyword format))

(defmulti init-state #'init-state-dispatch-fn)

(defmethod init-state :edn [_ is]
  (load-edn is))
(defmethod init-state :clj [_ is]
  (load-edn is))

(defmethod init-state :yml [_ is]
  (load-yaml is))
(defmethod init-state :yaml [_ is]
  (load-yaml is))

(defmethod init-state :properties [_ is]
  (load-properties is))

(defn rb-init [format is]
  (let [s (init-state format is)]
    [[] s]))

(extend-protocol value/IValue
  org.pupcus.i18n.ResourceBundle
  (value
   [rb k]
   (let [m (.state rb)
         p (.getParent rb)]
     (if (vector? k)
       (get-in m k)
       (or (get m k)
           (get m (keyword k))
           (get m (name k))
           (when p (value/value p k)))))))
