(ns detect-leaf.core
  (:require [clojure.tools.cli :as cli]
            [detect-leaf.utils :as utils]
            [org.bovinegenius [exploding-fish :as uri]])
  (:use [clj-xpath.core :only [$x $x:node $x:node+ $x:text+]]
        [clojure.pprint :only [pprint]]
        [structural-similarity.xpath-text :only [similar?]])
  (:import (org.htmlcleaner HtmlCleaner DomSerializer CleanerProperties)
           (org.w3c.dom Document)))

