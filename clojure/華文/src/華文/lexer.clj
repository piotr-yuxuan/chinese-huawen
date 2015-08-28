;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.lexer
  (:require [instaparse.core :as insta]
            [華文.parser.kawabata :as k]
            [華文.ids-manipulation :as i])
  (:use clojure.test))

;; This is the final datastructure I want. It's realistic but only takes one
;; version. To populate it, one just has to parse then add a line. Before to be
;; queried, it has to be normalised. A structure is normalised iff:
;;  → Every sinographs in IDS have a corresponding key
;;  → Each row, IDS is as collapsed as possible

;(def example-map)
{"䬽" {:codepoint "U+4B3D"
       :ids "⿰兑兑"}
 "飠" {:codepoint "U+98E0"
       :ids "⿱𠆢&CDP-8C42;"}
 "兑" {:codepoint "U+5151"
       :ids "⿱丷兄"}
 "𠆢" {:codepoint "U+201A2"
       :ids "𠆢"}
 "&CDP-8C42;" {:codepoint "CDP-8C42"
               :ids "⿱丶&CDP-8B7C;"}
 "丷" {:codepoint "U+4E37"
       :ids "丷"}
 "兄" {:codepoint "U+5144"
       :ids "⿱口儿"}
 "丶" {:codepoint "U+4E36"
       :ids "丶"}
 "&CDP-8B7C;" {:codepoint "CDP-8B7C"
               :ids "&CDP-8B7C;"}
 "口" {:codepoint "U+53E3"
       :ids "口"}
 "儿" {:codepoint "U+513F"
       :ids "⿰丿乚"}
 "丿" {:codepoint "U+4E3F"
       :ids "丿"}
}

;; Well, for now on we device to address the character issue. It comes from the
;; JVM and the way Clojure directly refers to characters. I believe there must
;; be a way for macros to solve it gracefully but anyway, the example-map
;; variable previously defined is somewhat redundant.

;; So let's define a container less redundant which handle multiple versions and
;; circumvents the Unicode issue by isolating it in the side-effect display
;; function (moving from "does it belong to the BMP?" to "do I have a proper
;; font to render it?"). It becomes less easy for the programmer to know which
;; character they manipulate because of the higher abstraction.

;; nil refers to the default IDS. IDS content must be escaped (except IDC). I'v
;; found some of possible versions: {G, H, J, K, M, T, U, V, X}. :variants is
;; mapped either to nil (then this entry neither has variants nor is variant of
;; another entry) either to a map referencing variants and their axis.
;(def container)
{"&U+4B3D;" {nil "default IDS."
             :G "?"
             :H "Hangul?"
             :J "Japanese?"
             :K "Korean?"
             :M "Mandarin?"
             :T "Taiwan?"
             :U "?"
             :V "Vietnam?"
             :X "Unknown?"
             :variants {:z ["&CDP+53E31;"]}}}

;; How to parse Kawabata-san base:
(def level-0 (k/level-0 "../../data/ids/ids.txt"))
(def level-1 (k/level-1 {} level-0))
(defn level-2
  ([row]
   (level-2 row nil))
  ([row version]
   (i/ids-to-tree (get (first (vals (level-1 row))) version))))

;; Level 0 accesses physical data, that's to say the filesystem. This level only
;; takes a location as single parameter. It gives a way to access each data row.
(level-0 203)

;; Level 1 presents raw data. It take as single parameter a description of the
;; source format.
(level-1 203)

;; Level 2 parses the IDS. Thus is uses the official IDS grammar.
(level-2 203)
(level-2 203 :T)

;; TODO
;; Now we first want to check the file has no errors and once it's dont, we want
;; put all rows in a container. The validation function belongs to k namespace
;; whilst the container construction is to be done here.
(defn harden
  ([]
   (harden 20000))
  ([i]
   (println "starting from " harden)
   (loop [current i]
     (try
       (level-2 current)
       (catch Exception e
         (.printStackTrace e)
         (println current "\t" (level-0 current))))
     (recur (inc current)))))

(harden)

;; harden result
20992 	 ;; -*- coding: utf-8-mcs-er -*-
27575 	 ;; -*- coding: utf-8 -*-
27783 	 ;; -*- coding: utf-8-mcs-er -*-
35976 	 ;; -*- coding: utf-8-mcs-er -*-
44169 	 ;; -*- coding: utf-8-mcs-er -*-
52362 	 ;; -*- coding: utf-8-mcs-er -*-

;; Values of i which cause bugs.
39733 ;; same problem above is no more reported.
38967
38732
36998
36996
36781
36513
20992 ;; Acceptable for it's not a regular row
27575 ;; Acceptable for it's not a regular row
27783 ;; Acceptable for it's not a regular row
27958 ;; Should not be accepted
28095 ;; Should not be accepted
28239 ;; Should not be accepted
28627 ;; Should not be accepted
28851 ;; Should not be accepted
28956 ;; Should not be accepted
29175
29447
30424
30493
30523
30887
30985
31113
32116
32162
32298 ;; Malformed IDS
32489
33084
33510
33925
34393
34948
35976 ;; Acceptable for it's not a regular row
36470
