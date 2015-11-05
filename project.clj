(defproject org.pupcus/i18n-clj "0.1.2-SNAPSHOT"

  :description "i18n message strings for clojure"

  :url "https://github.com/mdpendergrass/i18n-clj"

  :scm {:url "git@github.com:mdpendergrass/i18n-clj"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.reader  "0.10.0"]
                 [com.stuartsierra/component "0.3.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [clj-yaml "0.4.0"]]


  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [compojure "1.4.0"]
                        [ring "1.4.0"]
                        [ring/ring-mock "0.3.0"]
                        [log4j "1.2.17" :exclusions [javax.mail/mail
                                                     javax.jms/jms
                                                     com.sun.jdmk/jmxtools
                                                     com.sun.jmx/jmxri]]
                        [org.slf4j/slf4j-log4j12 "1.7.5"]]}}

  :global-vars {*warn-on-reflection* true
                *assert* false}


  :aot [org.pupcus.i18n.ResourceBundle]

  :deploy-repositories [["snapshots"
                         {:url "https://clojars.org/repo"
                          :creds :gpg}]
                        ["releases"
                         {:url "https://clojars.org/repo"
                          :creds :gpg}]])
