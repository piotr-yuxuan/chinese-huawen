(ns 華文.lexer.parser
  (:require [instaparse.core :as insta]))

(def ids "../../data/ids/ids.txt")

(.exists (clojure.java.io/as-file ids))

(def src (line-seq (clojure.java.io/reader ids)))

(defn read
  [i]
  (nth src i))

;; For these data, return [pointcode sinogram & IDS]
;; IDS might be wrapped.
(defn split
  [i]
  (clojure.string/split (read i) #"\t"))

(def operators
  #{"⿰" "⿱" "⿲" "⿳" "⿴" "⿵" "⿶" "⿷" "⿸" "⿹" "⿺" "⿻"})
