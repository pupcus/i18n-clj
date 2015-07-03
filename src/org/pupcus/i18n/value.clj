(ns org.pupcus.i18n.value)

(defprotocol IValue
  (value [this keys]))
