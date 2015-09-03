;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.lexer
  (:require [instaparse.core :as insta]
            [華文.parser.kawabata :as k]
            [華文.ids-manipulation :as i]
            [華文.char-manipulation :as c])
  (:use clojure.test))

;; Let's define a container less redundant which handle multiple versions and
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

;; Level 0 accesses physical data, that's to say the filesystem. This level only
;; takes a location as single parameter. It gives a way to access each data row.
(def level-0 (parser/level-0 "../../data/ids/ids.txt"))

;; Level 1 presents raw data. It take as single parameter a description of the
;; source format.
(def level-1 (parser/level-1 {} level-0))

;; Level 2 parses the IDS. Thus is uses the official IDS grammar.
(defn level-2
  ([row]
   (level-2 row nil))
  ([row version]
   (i/ids-to-tree (get (first (vals (level-1 row))) version))))

;; How to parse Kawabata-san base:
(level-0 203)
(level-1 203)
(level-2 203)
(level-2 203 :T)

;; TODO
;; Now we first want to check the file has no errors and once it's dont, we want
;; put all rows in a container. The validation function belongs to k namespace
;; whilst the container construction is to be done here.
(defn harden
  ([]
   (harden 0))
  ([i]
   (println "starting from " harden)
   (loop [current i]
     (try
       (print current " ")
       (level-2 current)
       (catch Exception e
         (.printStackTrace e)
         (println current "\t" (level-0 current))))
     (recur (inc current)))))
;; Unfortunately this is a single-thread application, hence rather slow.

;; hardenned result
0 	 ;; # Copyright (c) 2014 CJKVI Database
1 	 ;; # Based on CHISE IDS Database
2 	 ;;
20992 	 ;; ;; -*- coding: utf-8-mcs-er -*-
27575 	 ;; ;; -*- coding: utf-8 -*-
27783 	 ;; ;; -*- coding: utf-8-mcs-er -*-
32298    ;; U+211A2	𡆢	⿴囗α
34393    ;; U+219D1	𡧑	⿱宀⿱α大
35976 	 ;; ;; -*- coding: utf-8-mcs-er -*-
44169 	 ;; ;; -*- coding: utf-8-mcs-er -*-
52362 	 ;; ;; -*- coding: utf-8-mcs-er -*-
60555 	 ;; ;; -*- coding: utf-8-mcs-er -*-
68748 	 ;; ;; -*- coding: utf-8-mcs-er -*-
70500 	 ;; ;; -*- coding: utf-8-mcs-er -*-
74650 	 ;; ;; -*- coding: utf-8 -*-
74873 	 ;; ;; -*- coding: utf-8-mcs-er -*-
80636 	 ;; ;; -*- coding: utf-8 -*-
81179 	 ;; ;; -*- coding: utf-8-mcs-er -*-

;; TODO function which reduce any expended IDS into reduced IDS whenever it's possible in the given set.
()

;; TODO
;; It's not pure at all, it has to be corrected. By the way, I should work only with escaped representations and get a glyph back in display functions. Would be way better. We'll also need an export namespace (for export graph and so on).
;; So the next step will be to refactor the code. I should clean it then ask some community for criticising it.
(with-test
  (defn export-graph
  "range is a two-item vector and version a keyword.
  When version doesn't match explicit version for a character, nil is used
  This is a basic version hence it returns a graph based on given ids."
  [range-row]
  (reduce (fn [reduced item]
            (let [[character data] (first (vec item))
                  linked (i/ids-token-set (get data nil))]
              (reduce #(str %
                            (if-not (= % "") "\n")
                            (c/deref-escaped character) "\t" %2)
                      ""
                      linked)))
          ""
          (set (map level-1 (apply range range-row)))))
  (is (= (export-graph [32283 32284])
         "𡆓\t⺄\n𡆓\t𠃊\n𡆓\t&CDP-88D7;\n𡆓\t白\n𡆓\t爫\n𡆓\t⺌\n𡆓\t口")))

(i/ids-token-set "⿰口⿱竹⿰⿱工止㚇")
