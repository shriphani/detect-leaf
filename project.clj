(defproject detect_leaf "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.9.0"]
                 [clj-logging-config "1.9.7"]
                 [clj-ml "0.0.3-SNAPSHOT"]
                 [com.github.kyleburton/clj-xpath "1.4.2"]
                 [enlive "1.1.5"]
                 [log4j/log4j "1.2.16"
                  :exclusions
                  [javax.mail/mail
                   javax.jms/jms
                   com.sun.jdmk/jmxtools
                   com.sun.jmx/jmxri]]
                 [net.sourceforge.htmlcleaner/htmlcleaner "2.6"]
                 [org.apache.commons/commons-lang3 "3.1"]
                 [org.bovinegenius/exploding-fish "0.3.3"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.clojure/clojure "1.5.1"]
                 [structural_similarity/structural_similarity "0.1.0-SNAPSHOT"]]
  :plugins [[lein-gorilla "0.2.0"]])
