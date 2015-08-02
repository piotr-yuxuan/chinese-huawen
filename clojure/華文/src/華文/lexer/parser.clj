(ns 華文.lexer.parser
  (:require [instaparse.core :as insta]))

(def ids "../../data/ids/ids.txt")

(.exists (clojure.java.io/as-file ids))

;; Scanner

(def src (line-seq (clojure.java.io/reader ids)))

(defn readi
  [i]
  (nth src i))

;; For these data, return [pointcode sinogram & IDS]
;; IDS might be wrapped.
(defn split
  [i]
  (clojure.string/split (readi i) #"\t"))

(def operators
  #{"⿰" "⿱" "⿲" "⿳" "⿴" "⿵" "⿶" "⿷" "⿸" "⿹" "⿺" "⿻"})

;; Lexer

;; Parser
;; This is not precise enough because ⿰ can be passed more than two arguments or ⿲ less than three. So we should use conditionals.
(def as-and-bs
  (insta/parser
   "S = F+ (* Parsing tree may have not head *)
    F = IDC (L | F)+ (* form *)
    IDC = #'\\P{InIdeographic_Description_Characters}'
    L = #'\\P{InIdeographic_Description_Characters}' (* letter *)"))
;; (as-and-bs "⿰⿱ab⿱cd")
;; => [:S [:F [:IDC "⿰"] [:F [:IDC "⿱"] [:L "a"] [:L "b"]] [:F [:IDC "⿱"] [:L "c"] [:L "d"]]]]

;; This is better as it fixes the aforementionned problem, however it's not perfect yet. It's would be better to outpit generic IDC and not inner-scoped IDC2 or IDC3.
(def as-and-bs
  (insta/parser
   "S = F+ (* Parsing tree may have not head *)
    F = ((IDC3 (L | F)) | IDC2) (L | F) (L | F) (* form *)
    IDC2 = '⿰' | '⿱' | '⿴' | '⿵' | '⿶' | '⿷' | '⿸' | '⿹' | '⿺' | '⿻'
    IDC3 = '⿲' | '⿳'
    IDC = #'\\P{InIdeographic_Description_Characters}'
    L = #'\\P{InIdeographic_Description_Characters}' (* letter *)"))
;; (as-and-bs "⿰⿱ab⿱cd")
;; => [:S [:F [:IDC2 "⿰"] [:F [:IDC2 "⿱"] [:L "a"] [:L "b"]] [:F [:IDC2 "⿱"] [:L "c"] [:L "d"]]]]

;; Last try, still to be tried :-)
(def as-and-bs
  (insta/parser
   "S = F+ (* Parsing tree may have not head *)
    F = ((IDC3 (L | F)) | IDC2) (L | F) (L | F) (* form *)
    IDC2 = '⿰' | '⿱' | '⿴' | '⿵' | '⿶' | '⿷' | '⿸' | '⿹' | '⿺' | '⿻'
    IDC3 = '⿲' | '⿳'
    IDC = #'\\P{InIdeographic_Description_Characters}'
    L = #'\\P{InIdeographic_Description_Characters}' (* letter *)"))
