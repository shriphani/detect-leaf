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
   ["Large amounts of files to cat" "http://ubuntuforums.org/showthread.php?t=2216388" true]
   ["Discrepancy in GB noted during Xubuntu 12.04.4 install" "http://ubuntuforums.org/showthread.php?t=2216238" true]
   ["Installed 13.10 fails login using nVidia GeForce 7300 LE" "http://ubuntuforums.org/showthread.php?t=2198725" true]
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
   ["Community Success Stories Area" "http://www.theadminzone.com/forums/showthread.php?t=99801" true]
   ["New Sub-forum for Forum Contests/Competitions Announcements" "http://www.theadminzone.com/forums/showthread.php?t=74393" true]
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
   ["Heartbleed vulnerability in OpenSSL" "http://forum.doom9.org/showthread.php?t=170457" true]
   ["track of audio MPEG2 into MP4 (batch mode)" "http://forum.doom9.org/showthread.php?t=170427" true]
   ["How To Remove Vertical Lines Or cutting of frames?" "http://forum.doom9.org/showthread.php?t=170408" true]
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
   ["Logging Out - Not keeping Passwords / Auto log in / Settings" "https://www.phpbb.com/community/viewtopic.php?f=46&t=2237026" true]
   ["Moving host: v3.0.10 no login + v3.0.12 no install" "https://www.phpbb.com/community/viewtopic.php?f=46&t=2236941" true]
   ["Broken Border" "https://www.phpbb.com/community/viewtopic.php?f=74&t=2236961" true]
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
   ["Mailspike.net blacklist" "http://community.mxtoolbox.com/forums/viewtopic.php?f=5&t=1890076" true]
   ["The ip shows as okays although I think it has been blocked" "http://community.mxtoolbox.com/forums/viewtopic.php?f=5&t=14207" true]
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
   ["upgrade from kas 2014 to pure" "http://forum.kaspersky.com/index.php?showtopic=293250" true]
   ["KIS2014 previous application launch failed windows 8.1" "http://forum.kaspersky.com/index.php?showtopic=293238" true]
   ["Kapersky blocks valid application with access database after update on 04/09/2014" "http://forum.kaspersky.com/index.php?showtopic=292998" true]
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
   ["How to Break Windows 7 Password? Child's Play" "http://www.911cd.net/forums//index.php?showtopic=25518" true]
   ["Install Win XP along with Windows 7" "http://www.911cd.net/forums//index.php?showtopic=24545" true]
   ["Registry cannot be updated" "http://www.911cd.net/forums//index.php?showtopic=24614" true]
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
   ["How to fix everything being black and broken" "http://www.moviestorm.co.uk/forums/index.php?showtopic=12108" true]
   ["looking for transparency" "http://www.moviestorm.co.uk/forums/index.php?showtopic=14846" true]
   ["Cant find addon after installing through defualt method" "http://www.moviestorm.co.uk/forums/index.php?showtopic=14793" true]
   ["Moviestorm" "http://www.moviestorm.co.uk/forums/index.php?showuser=658" false]
   ["primaveranz" "http://www.moviestorm.co.uk/forums/index.php?showuser=2534" false]
   ["Ben_S" "http://www.moviestorm.co.uk/forums/index.php?showuser=116" false]])

(def *test-examples*
  ;; vbulletin 1
  [["Small Compact Digitals by Canon" "http://photography-on-the.net/forum/forumdisplay.php?f=15" false]
   ["Canon G-series Digital Cameras" "http://photography-on-the.net/forum/forumdisplay.php?f=17" false]
   ["Canon EF and EF-S Lenses" "http://photography-on-the.net/forum/forumdisplay.php?f=33" false]
   ["Canon releases PowerShot SX50 HS 50x superzoom" "http://photography-on-the.net/forum/showthread.php?t=1228984" true]
   ["Powershots N100, SX600HS and Elph 340HS" "http://photography-on-the.net/forum/showthread.php?t=1352539" true]
   ["Four new PowerShots - A1400, A2600, ELPH 130 and N" "http://photography-on-the.net/forum/showthread.php?t=1263895" true]
   ["Sylvia Q" "http://photography-on-the.net/forum/member.php?u=373160" false]

   ;; digital grin - vb
   ["Cameras" "http://www.dgrin.com/forumdisplay.php?f=3" false]
   ["Flea Market" "http://www.dgrin.com/forumdisplay.php?f=18" false]
   ["Journeys" "http://www.dgrin.com/forumdisplay.php?f=33" false]
   ["The Never Ending Alphabet Game Challenge!" "http://www.dgrin.com/showthread.php?t=237186" true]
   ["Challenge #5 Feathered Friends" "http://www.dgrin.com/showthread.php?t=246355" true]
   ["#4 OOYCZ -- Silhouettes = \"Stuck in a hotel\"" "http://www.dgrin.com/showthread.php?t=246025" true]

   ;; statcounter - vb
   ["Service Status" "http://forum.statcounter.com/vb/forumdisplay.php?f=39" false]
   ["BANNED? Please read!" "http://forum.statcounter.com/vb/forumdisplay.php?f=44" false]
   ["Help" "http://forum.statcounter.com/vb/forumdisplay.php?f=31" false]
   ["Checking your Partition Number" "http://forum.statcounter.com/vb/showthread.php?t=38882" true]
   ["facebook" "http://forum.statcounter.com/vb/showthread.php?t=30074" true]
   ["Came From - no stats from External - Aha! Check this out" "http://forum.statcounter.com/vb/showthread.php?t=17559" true]
   ["Rory_A" "http://forum.statcounter.com/vb/member.php?find=lastposter&t=34582" false]
   ["theBast" "http://forum.statcounter.com/vb/member.php?find=lastposter&t=22559" false]
   ["kristell" "http://forum.statcounter.com/vb/member.php?find=lastposter&t=42422" false]

   ;; the west - vb
   ["The Western Star" "http://forum.the-west.net/forumdisplay.php?f=2" false]
   ["Ideas & Brainfarts" "http://forum.the-west.net/forumdisplay.php?f=10" false]
   ["Questions & Guides" "http://forum.the-west.net/forumdisplay.php?f=4" false]
   ["Community Report #5" "http://forum.the-west.net/showthread.php?t=53928" true]
   ["Main Story Part 9" "http://forum.the-west.net/showthread.php?t=53974" true]
   ["Facebook: Valentine's Day heart campaign" "http://forum.the-west.net/showthread.php?t=54759" true]
   ["New update and Christmas Specials!" "http://forum.the-west.net/showthread.php?t=54331" true]
   ["Da Twista" "http://forum.the-west.net/member.php?find=lastposter&t=53928" false]
   ["Desi Boukerse" "http://forum.the-west.net/member.php?find=lastposter&t=54863" false]
   ["Diggo11" "http://forum.the-west.net/member.php?find=lastposter&t=36907" false]

   ;; guru - 3d 
   ["The Guru's Pub" "http://forums.guru3d.com/forumdisplay.php?f=28" false]
   ["Folding@Home - Join Team Guru3D !" "http://forums.guru3d.com/forumdisplay.php?f=35" false]
   ["The HTPC, HDTV & High Definition section" "http://forums.guru3d.com/forumdisplay.php?f=50" false]
   ["Just built a HTPC, need simple fan control software" "http://forums.guru3d.com/showthread.php?t=388266" true]
   ["looking to buy a sound card" "http://forums.guru3d.com/showthread.php?t=385500" true]
   ["Question about 3d LED tv being used as a pc monitor." "http://forums.guru3d.com/showthread.php?t=387364" true]
   ["Smooth Video Project - Frame Interpolation" "http://forums.guru3d.com/showthread.php?t=363239" true]
   ["Can't go full screen on TV (1080P) with HD 3670 - Laptop" "http://forums.guru3d.com/showthread.php?t=313274"  true]
   ["WMC video/audio codecs? (mp4 not working for new DVD rips)" "http://forums.guru3d.com/showthread.php?t=386717" true]

   ;; kidney-forums
   [nil "http://www.kidneyspace.com/" false]
   ["Get started here: Rules, Help" "http://www.kidneyspace.com/index.php/board,27.0.html" false]
   ["Welcome New Members/Introduce Yourself/Share Your Story" "http://www.kidneyspace.com/index.php/board,4.0.html" false]
   ["Dialysis" "http://www.kidneyspace.com/index.php/board,77.0.html" false]
   ["Care Partners" "http://www.kidneyspace.com/index.php/board,47.0.html" false]
   ["MOVED: Stage 3 CKD" "http://www.kidneyspace.com/index.php/topic,4797.0.html" true]
   ["Suggestion for Tiny link for RSN on Facebook" "http://www.kidneyspace.com/index.php/topic,3552.0.html" true]
   ["Were the forums down?" "http://www.kidneyspace.com/index.php/topic,2616.0.html" true]
   ["kidneysp" "http://www.kidneyspace.com/index.php?action=profile;u=1" false]
   ["getlife" "http://www.kidneyspace.com/index.php?action=profile;u=27" false]
   ["Angie" "http://www.kidneyspace.com/index.php?action=profile;u=190" false]

   ;; davita
   [nil "http://forums.davita.com/forum.php" false]
   ["Share your experiences - General dialysis" "http://forums.davita.com/forumdisplay.php?32-Share-your-experiences-General-dialysis" false]
   ["Health conditions - General dialysis" "http://forums.davita.com/forumdisplay.php?87-Health-conditions-General-dialysis" false]
   ["Employment & insurance - General dialysis" "http://forums.davita.com/forumdisplay.php?99-Employment-amp-insurance-General-dialysis" false]
   ["Dialysis and Medicare" "http://forums.davita.com/showthread.php?5931-Dialysis-and-Medicare" true]
   ["Do you work and manage dialysis?" "http://forums.davita.com/showthread.php?6746-Do-you-work-and-manage-dialysis" true]
   ["Dialysis and the Fiscal Cliff Bill" "http://forums.davita.com/showthread.php?6094-Dialysis-and-the-Fiscal-Cliff-Bill" true]
   ["HELP!!!how can I be a volunteer nurse" "http://forums.davita.com/showthread.php?6036-HELP!!!how-can-I-be-a-volunteer-nurse" true]
   ["View Profile" "http://forums.davita.com/member.php?117564-malibu" false]
   ["View Forum Posts" "http://forums.davita.com/search.php?do=finduser&userid=117564&contenttype=vBForum_Post&showposts=1" false]])

(def *test-negatives-only*
  (filter
   (fn [[_ _ v]]
     v)
   *test-examples*))

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

(def *test-corpus-file* "test.corpus")

(defn download-test-corpus
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
    *test-examples*)
   (io/writer *test-corpus-file*)))

(defn read-train-corpus
  []
  (read
   (java.io.PushbackReader.
    (io/reader *train-corpus-file*))))

(defn read-test-corpus
  []
  (read
   (java.io.PushbackReader.
    (io/reader *test-corpus-file*))))
