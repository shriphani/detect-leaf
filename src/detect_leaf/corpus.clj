(ns detect-leaf.corpus
  "Train/test data download"
  (:require [detect-leaf.utils :as utils]))

;; Training set
;; Was built from 5 vbulletin, 5 phpbb and 5 ipboard sites
;; format is anchor text, target page URL and true or false
(def *train-examples*
  ;; ubuntuforums - runs vb
  [["Absolute Beginners Section" "http://ubuntuforums.org/forumdisplay.php?f=326" false]
   ["Security Discussions" "http://ubuntuforums.org/forumdisplay.php?f=338" false]
   ["The Cafe" "http://ubuntuforums.org/forumdisplay.php?f=11" false]
   ["How to copy the file from a remote server to my laptop?" "http://ubuntuforums.org/showthread.php?t=2216068" true]
   ["\"stealth\" mode for FTP and SSH ports?" "http://ubuntuforums.org/showthread.php?t=2215873" true]
   ["Which of these two laptops should I get?" "http://ubuntuforums.org/showthread.php?t=2215143" true]
   ["ubuntuman001" "http://ubuntuforums.org/member.php?u=76290" false]
   ["mips" "http://ubuntuforums.org/member.php?u=8017" false]
   ["QIII" "http://ubuntuforums.org/member.php?u=628170" false]

   ;; TheAdminZone - vb
   ["TAZ News & Information" "http://www.theadminzone.com/forums/forumdisplay.php?f=201" false]
   ["Suggestions | Feedback | Support" "http://www.theadminzone.com/forums/forumdisplay.php?f=12" false]
   ["Managing an Online Community" "http://www.theadminzone.com/forums/forumdisplay.php?f=3" false]
   ["Problems Logging On To TAZ Using Chrome?" "http://www.theadminzone.com/forums/showthread.php?t=107933" true]
   ["Join the TAZ Review team, applications being accepted now!" "http://www.theadminzone.com/forums/showthread.php?t=107933" true]
   ["Network Disruption" "http://www.theadminzone.com/forums/showthread.php?t=107933" true]
   ["The Sandman" "http://www.theadminzone.com/forums/member.php?u=3" false]
   ["esquire" "http://www.theadminzone.com/forums/member.php?u=52998" false]
   ["PoetJC" "http://www.theadminzone.com/forums/member.php?u=11231" false]

   ;; Doom9 Forums - vb
   ["General Discussion" "http://forum.doom9.org/forumdisplay.php?f=8" false]
   ["News" "http://forum.doom9.org/forumdisplay.php?f=79" false]
   ["Forum / Site Suggestions & Help" "http://forum.doom9.org/forumdisplay.php?f=45" false]
   ["Posting in General Discussion" "http://forum.doom9.org/showthread.php?t=57466" true]
   ["Lowest Resolutions For Phones & Tablets" "http://forum.doom9.org/showthread.php?t=170450" true]
   ["Help to solve this TV broadcasting mystery!" "http://forum.doom9.org/showthread.php?t=170392" true]
   ["goldensun87" "http://forum.doom9.org/member.php?u=158256" false]
   ["davidhorman" "http://forum.doom9.org/member.php?u=47770" false]
   ["SeeMoreDigital" "http://forum.doom9.org/member.php?u=35156" false]

   ;; phpbb
   []
   []
   []
   []
   []
   []
   []
   []
   []


   ;; phpbb
   []
   []
   []
   []
   []
   []
   []
   []
   []


   ;; phpbb
   []
   []
   []
   []
   []
   []
   []
   []
   []


   ;; ipboard
   []
   []
   []
   []
   []
   []
   []
   []
   []


   ;; ipboard
   []
   []
   []
   []
   []
   []
   []
   []
   []


   ;; ipboard
   []
   []
   []
   []
   []
   []
   []
   []
   []])
