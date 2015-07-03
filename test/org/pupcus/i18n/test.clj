(ns org.pupcus.i18n.test
  (:refer-clojure :exclude [format])
  (:require [clojure.test :refer :all]
            [org.pupcus.i18n.utils :refer :all]
            [org.pupcus.i18n.locale :refer [with-locale]]
            [org.pupcus.i18n.bundle :as bundle]))

;; ----
;; .properties file tests
;; ----

(let [messages (bundle/create-bundle-manager :messages ["properties"])
      errors   (bundle/create-bundle-manager :errors   ["properties"])]

  (deftest test-lookup-with-properties-bundle
    (is (= "purple"   (messages :color)))
    (is (= "purpura"  (messages :color "es")))
    (is (= "purpurne" (messages :color "de_DE")))
    (is (= "purple"   (messages :color "en")))
    (is (= "purple"   (messages :color "fr"))))

  (deftest error-messages-with-properties-bundle
    (is (= "blue" (errors :color)))
    (is (= "Venus" (errors :planet))))

  (deftest test-with-locale-with-properties-bundle
    (is (= "purple" (with-locale :en (messages :color))))
    (is (= "purpura" (with-locale :es (messages :color))))
    (is (= "purpurne" (with-locale :de_DE (messages :color)))))

  (deftest test-java-clj-format-with-properties-bundle
    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
          time (.format (java.text.SimpleDateFormat. "h:mm a") d)
          date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]

      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (format messages :template.format time date 6 (messages :planet))))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (format messages :template.format :es time date 6 (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (format messages :template.format :de_DE time date 6 (messages :planet :de_DE))))))

  (deftest test-message-format-with-properties-bundle
    (let [date (doto (java.util.Date.) (.setTime 1426194140054))]
      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (message-format messages :template.message (messages :planet) 6 date)))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (message-format messages :template.message :es (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (message-format messages :template.message :de_DE (messages :planet :de_DE) 6 date)))))

  (deftest test-lisp-format-with-properties-bundle
    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
          time (.format (java.text.SimpleDateFormat. "h:mm a") d)
          date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]
      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (cl-format messages :template.lisp time date 6 (messages :planet))))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (cl-format messages :template.lisp :es time date 6 (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (cl-format messages :template.lisp :de_DE time date 6 (messages :planet :de_DE)))))))


;; ----
;; .yaml bundle tests
;; ----

(let [messages (bundle/create-bundle-manager "messages" ["yml" "yaml"])
      errors   (bundle/create-bundle-manager "errors"   ["yml" "yaml"])]

  (deftest test-lookup-with-yaml-bundle
    (is (= "purple"   (messages :color)))
    (is (= "purpura"  (messages :color "es")))
    (is (= "purpurne" (messages :color "de_DE")))
    (is (= "purple"   (messages :color "en")))
    (is (= "purple"   (messages :color "fr"))))

  (deftest error-messages-with-yaml-bundle
    (is (= "blue" (errors :color)))
    (is (= "Venus" (errors :planet))))

  (deftest test-with-locale-with-yaml-bundle
    (is (= "purple" (with-locale :en (messages :color))))
    (is (= "purpura" (with-locale :es (messages :color))))
    (is (= "purpurne" (with-locale :de_DE (messages :color)))))

  (deftest test-java-clj-format-with-yaml-bundle
    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
          time (.format (java.text.SimpleDateFormat. "h:mm a") d)
          date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]

      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (format messages [:template :format] time date 6 (messages :planet))))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (format messages [:template :format] :es time date 6 (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (format messages [:template :format] :de_DE time date 6 (messages :planet :de_DE))))))

  (deftest test-message-format-with-yaml-bundle
    (let [date (doto (java.util.Date.) (.setTime 1426194140054))]
      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (message-format messages [:template :message] (messages :planet) 6 date)))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (message-format messages [:template :message] :es (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (message-format messages [:template :message] :de_DE (messages :planet :de_DE) 6 date)))))

  (deftest test-lisp-format-with-yaml-bundle
    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
          time (.format (java.text.SimpleDateFormat. "h:mm a") d)
          date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]
      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (cl-format messages [:template :lisp] time date 6 (messages :planet))))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (cl-format messages [:template :lisp] :es time date 6 (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (cl-format messages [:template :lisp] :de_DE time date 6 (messages :planet :de_DE))))))


  (deftest test-nested-lookup-with-yaml-bundle
    (is (= \p  (messages [:color 3])))
    (is (= \p  (messages [:color 3] "es")))
    (is (= \p  (messages [:color 3] "de_DE")))
    (is (= \p  (messages [:color 3] "en")))
    (is (= \p  (messages [:color 3] "fr")))))



;; ----
;; .edn bundle tests
;; ----

(let  [messages (bundle/create-bundle-manager :messages ["edn" "clj"])
       errors   (bundle/create-bundle-manager :errors   ["edn" "clj"])]

  (deftest test-lookup-with-edn-bundle
    (is (= "purple"   (messages :color)))
    (is (= "purpura"  (messages :color "es")))
    (is (= "purpurne" (messages :color "de_DE")))
    (is (= "purple"   (messages :color "en")))
    (is (= "purple"   (messages :color "fr"))))

  (deftest error-messages-with-edn-bundle
    (is (= "blue" (errors :color)))
    (is (= "Venus" (errors :planet))))

  (deftest test-with-locale-with-edn-bundle
    (is (= "purple" (with-locale :en (messages :color))))
    (is (= "purpura" (with-locale :es (messages :color))))
    (is (= "purpurne" (with-locale :de_DE (messages :color)))))

  (deftest test-java-clj-format-with-edn-bundle
    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
          time (.format (java.text.SimpleDateFormat. "h:mm a") d)
          date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]

      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (format messages [:template :format] time date 6 (messages :planet))))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (format messages [:template :format] :es time date 6 (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (format messages [:template :format] :de_DE time date 6 (messages :planet :de_DE))))))

  (deftest test-message-format-with-edn-bundle
    (let [date (doto (java.util.Date.) (.setTime 1426194140054))]
      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (message-format messages [:template :message] (messages :planet) 6 date)))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (message-format messages [:template :message] :es (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (message-format messages [:template :message] :de_DE (messages :planet :de_DE) 6 date)))))

  (deftest test-lisp-format-with-edn-bundle
    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
          time (.format (java.text.SimpleDateFormat. "h:mm a") d)
          date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]
      (is (= "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."
             (cl-format messages [:template :lisp] time date 6 (messages :planet))))

      (is (= "Al 5:02 PM del March 12, 2015, se han detectado 6 naves espaciales en el planeta Marte."
             (cl-format messages [:template :lisp] :es time date 6 (messages :planet :es) 6 date)))

      (is (= "Um 5:02 PM am March 12, 2015 haben wir 6 Raumschiffe auf dem Planeten Mars entdeckt."
             (cl-format messages [:template :lisp] :de_DE time date 6 (messages :planet :de_DE))))))


  (deftest test-nested-lookup-with-edn-bundle
    (is (= \p  (messages [:color 3])))
    (is (= \p  (messages [:color 3] "es")))
    (is (= \p  (messages [:color 3] "de_DE")))
    (is (= \p  (messages [:color 3] "en")))
    (is (= \p  (messages [:color 3] "fr")))))
