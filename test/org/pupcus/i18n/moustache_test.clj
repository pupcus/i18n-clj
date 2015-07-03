(ns org.pupcus.i18n.moustache-test
  (:require [org.pupcus.i18n.bundle :refer [create-bundle-manager]]
            [org.pupcus.i18n.moustache :refer :all]
            [clojure.test :refer :all]))

;; ----
;; .properties bundle tests
;; ----

(deftest test-moustache-format-with-properties-bundle
  (let [d (doto (java.util.Date.) (.setTime 1426194140054))
        time (.format (java.text.SimpleDateFormat. "h:mm a") d)
        date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)
        messages (create-bundle-manager :messages ["properties"])]

    (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
           (moustache messages :template.moustache {:time time :date date :ships 6 :planet (messages :planet)})))

    (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
           (moustache messages :template.moustache {:time time :date date :ships 6 })))

    (is (= "At 5:02 PM on March 12, 2015, we detected nothing on the planet Mars."
           (moustache messages :template.moustache {:time time :date date})))

    (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
           (moustache messages :template.moustache :es {:time time :date date :ships 6 :planet (messages :planet :es)})))

    (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
           (moustache messages :template.moustache :de_DE {:time time :date date :ships 6 :planet (messages :planet :de_DE)})))))


;; ----
;; .yaml bundle tests
;; ----

(deftest test-moustache-format-with-yaml-bundle
  (let [d (doto (java.util.Date.) (.setTime 1426194140054))
        time (.format (java.text.SimpleDateFormat. "h:mm a") d)
        date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)
        messages (create-bundle-manager :messages ["yml" "yaml"])]

    (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
           (moustache messages [:template :moustache] {:time time :date date :ships 6 :planet (messages :planet)})))

    (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
           (moustache messages [:template :moustache] {:time time :date date :ships 6 })))

    (is (= "At 5:02 PM on March 12, 2015, we detected nothing on the planet Mars."
           (moustache messages [:template :moustache] {:time time :date date})))

    (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
           (moustache messages [:template :moustache] :es {:time time :date date :ships 6 :planet (messages :planet :es)})))

    (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
           (moustache messages [:template :moustache] :de_DE {:time time :date date :ships 6 :planet (messages :planet :de_DE)})))))



;; ----
;; .edn bundle tests
;; ----

(deftest test-moustache-format-with-edn-bundle
  (let [d (doto (java.util.Date.) (.setTime 1426194140054))
        time (.format (java.text.SimpleDateFormat. "h:mm a") d)
        date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)
        messages (create-bundle-manager :messages ["edn" "clj"])]

    (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
           (moustache messages [:template :moustache] {:time time :date date :ships 6 :planet (messages :planet)})))

    (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
           (moustache messages [:template :moustache] {:time time :date date :ships 6 })))

    (is (= "At 5:02 PM on March 12, 2015, we detected nothing on the planet Mars."
           (moustache messages [:template :moustache] {:time time :date date})))

    (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
           (moustache messages [:template :moustache] :es {:time time :date date :ships 6 :planet (messages :planet :es)})))

    (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
           (moustache messages [:template :moustache] :de_DE {:time time :date date :ships 6 :planet (messages :planet :de_DE)})))))
