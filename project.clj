(defproject org.pupcus/i18n-clj "0.2.0"

  :description "i18n message strings for clojure"

  :url "https://github.com/mdpendergrass/i18n-clj"

  :scm {:url "git@github.com:mdpendergrass/i18n-clj"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[clj-yaml "0.4.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/tools.reader  "1.1.0"]]


  :profiles {:dev {:dependencies [[compojure "1.6.0"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [org.slf4j/slf4j-log4j12 "1.7.25"]
                                  [ring "1.6.2"]
                                  [ring/ring-mock "0.3.1"]]}}

  :aot [org.pupcus.i18n.ResourceBundle]

  :deploy-repositories [["snapshots"
                         {:url "https://clojars.org/repo"
                          :sign-releases false
                          :creds :gpg}]
                        ["releases"
                         {:url "https://clojars.org/repo"
                          :sign-releases false
                          :creds :gpg}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :global-vars {*warn-on-reflection* true
                *assert* false})
