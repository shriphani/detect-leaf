(ns detect-leaf.corpus
  "Train/test data download"
  (:require [clojure.java.io :as io]
            [detect-leaf.utils :as utils]))

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
   ["Join the TAZ Review team, applications being accepted now!" "http://www.theadminzone.com/forums/showthread.php?t=106038" true]
   ["Network Disruption" "http://www.theadminzone.com/forums/showthread.php?t=107481" true]
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

   ;; PHPbb official - phpbb
   ["Announcements" "https://www.phpbb.com/community/viewforum.php?f=14" false]
   ["Area 51 Development Board" "https://www.phpbb.com/community/viewforum.php?f=416" false]
   ["3.0.x Support Forum" "https://www.phpbb.com/community/viewforum.php?f=46" false]
   ["Preventing Spam in phpBB3" "https://www.phpbb.com/community/viewtopic.php?f=46&t=2122696" true]
   ["System panel says version 3.0.12 but that I need to update" "https://www.phpbb.com/community/viewtopic.php?f=46&t=2236826" true]
   ["ACP .MODS tab" "https://www.phpbb.com/community/viewtopic.php?f=46&t=2236906" true]
   ["DV1" "https://www.phpbb.com/community/memberlist.php?mode=viewprofile&u=1291506" false]
   ["baconbuttyman" "https://www.phpbb.com/community/memberlist.php?mode=viewprofile&u=1351099" false]
   ["mrix2000" "https://www.phpbb.com/community/memberlist.php?mode=viewprofile&u=286205" false]


   ;; Psyonix - phpbb
   ["Official Messages" "http://psyonix.com/forum/viewforum.php?f=19" false]
   ["General Discussion" "http://psyonix.com/forum/viewforum.php?f=2" false]
   ["Tournaments and Challenges" "http://psyonix.com/forum/viewforum.php?f=16" false]
   ["Battle-Cars 2" "http://psyonix.com/forum/viewtopic.php?f=19&t=9278" true]
   ["SARPBC For PS Plus (EU version)" "http://psyonix.com/forum/viewtopic.php?f=19&t=7398" true]
   ["Psyonix Merchandise available for purchase now!" "http://psyonix.com/forum/viewtopic.php?f=19&t=6176" true]
   ["Psyonix-Dave" "http://psyonix.com/forum/memberlist.php?mode=viewprofile&u=60" false]
   ["Fysho" "http://psyonix.com/forum/memberlist.php?mode=viewprofile&u=503" false]
   ["RAHTRICKRAH" "http://psyonix.com/forum/memberlist.php?mode=viewprofile&u=5369" false]


   ;; Mx-Toolbox - phpbb
   ["General Feedback" "http://community.mxtoolbox.com/forums/viewforum.php?f=5" false]
   ["Ask MXToolbox" "http://community.mxtoolbox.com/forums/viewforum.php?f=6" false]
   ["FlexBox Hosted Email" "http://community.mxtoolbox.com/forums/viewforum.php?f=7" false]
   ["Reverse DNS FAILED! This is a problem." "http://community.mxtoolbox.com/forums/viewtopic.php?f=5&t=13303" true]
   ["hi some one harrasing my daughter my daughter" "http://community.mxtoolbox.com/forums/viewtopic.php?f=5&t=1890634" true]
   ["Please help me" "http://community.mxtoolbox.com/forums/viewtopic.php?f=5&t=1890582" true]
   ["vishnuamz" "http://community.mxtoolbox.com/forums/memberlist.php?mode=viewprofile&u=3179" false]
   ["pazza98" "http://community.mxtoolbox.com/forums/memberlist.php?mode=viewprofile&u=3082" false]
   ["MichaelR" "http://community.mxtoolbox.com/forums/memberlist.php?mode=viewprofile&u=2970" false]


   ;; Kaspersky Support - ipboard
   ["Protection for Home Users" "http://forum.kaspersky.com/index.php?showforum=156" false]
   ["Protection for Small Offices" "http://forum.kaspersky.com/index.php?showforum=187" false]
   ["Virus-related issues" "http://forum.kaspersky.com/index.php?showforum=19" false]
   ["Securing Network Access by Application" "http://forum.kaspersky.com/index.php?showtopic=293047" true]
   ["Kapersky blocks valid application with access database after update on 04/09/2014" "http://forum.kaspersky.com/index.php?showtopic=292998" true]
   ["KAV 7.0 database is obsolete, won't update" "http://forum.kaspersky.com/index.php?showtopic=293028" true]
   ["byronb" "http://forum.kaspersky.com/index.php?showuser=511090" false]
   ["Dr. Shred" "http://forum.kaspersky.com/index.php?showuser=511106" false]
   ["Norman S." "http://forum.kaspersky.com/index.php?showuser=510914" false]


   ;; 911CD - ipboard
   ["Bootable CDs" "http://www.911cd.net/forums//index.php?showforum=2" false]
   ["Windows PE" "http://www.911cd.net/forums//index.php?showforum=19" false]
   ["Windows2000/XP/Vista/Windows7 CDs/DvDs" "http://www.911cd.net/forums//index.php?showforum=3" false]
   ["Windows Defender Offline" "http://www.911cd.net/forums//index.php?showtopic=25534" true]
   ["PCUnlocker Live CD" "http://www.911cd.net/forums//index.php?showtopic=25610" true]
   ["BartPE-based recovery disk." "http://www.911cd.net/forums//index.php?showtopic=25609" true]
   ["Erik.Conant" "http://www.911cd.net/forums//index.php?showuser=58599" false]
   ["cad cow" "http://www.911cd.net/forums//index.php?showuser=58581" false]
   ["jimmodsss" "http://www.911cd.net/forums//index.php?showuser=58553" false]


   ;; Moviestorm - ipboard
   ["Announcements" "http://www.moviestorm.co.uk/forums/index.php?showforum=7" false]
   ["Education" "http://www.moviestorm.co.uk/forums/index.php?showforum=37" false]
   ["Website maintenance" "http://www.moviestorm.co.uk/forums/index.php?showforum=17" false]
   ["Moviestorm 1.6.3 release" "http://www.moviestorm.co.uk/forums/index.php?showtopic=14787" true]
   ["Bug in 1.6.1" "http://www.moviestorm.co.uk/forums/index.php?showtopic=14772" true]
   ["Movie Hosting Service to be withdrawn" "http://www.moviestorm.co.uk/forums/index.php?showtopic=14501" true]
   ["Moviestorm" "http://www.moviestorm.co.uk/forums/index.php?showuser=658" false]
   ["primaveranz" "http://www.moviestorm.co.uk/forums/index.php?showuser=2534" false]
   ["Ben_S" "http://www.moviestorm.co.uk/forums/index.php?showuser=116" false]])

(def *train-corpus-file* "train.corpus")

(defn download-train-corpus
  []
  (clojure.pprint/pprint
   (map
    (fn [[anchor-text url label]]
      (do
        (Thread/sleep 1000)
        {:anchor-text anchor-text
         :url url
         :body (utils/download-with-cookie url)
         :label label}))
    *train-examples*)
   (io/writer *train-corpus-file*)))
