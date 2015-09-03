(ns 華文.manipulation.struct
  (:require [instaparse.core :as insta]
            [manipulation.regeces :as range])
  (:use clojure.test)
  (:use 華文.manipulation.char)
  (:require [華文.manipulation.regeces :refe [aggregate]]))

(declare ids-to-tree)

(with-test
  (def deref-tree
    #(cond (keyword? %) %
           (coll? %) ((cond (vector? %) vec
                            :else identity)
                      (map deref-tree %))
           :else (deref-escaped %)))
  (is (= (deref-tree '([:⿱ "&U+53E3;"
                        [:⿰ "&U+201A2;" "&U+4E5A;"]]))
         '([:⿱ "口" [:⿰ "𠆢" "乚"]]))))

(with-test
  (defn escape-tree-set
    [ids-tree]
    (reduce #(into %1 (cond (keyword? %2) (list (name %2))
                            (string? %2) (list %2)
                            (char? %2) (list (str %2))
                            (vector? %2) (escape-tree-set (list %2))
                            :else (list %2)))
            #{} (#(if (coll? %) % (list %)) (first ids-tree))))
  (is (= (escape-tree-set '("&CDP-8C42;")) #{"&CDP-8C42;"}))
  (is (= (escape-tree-set '("兄")) #{"兄"}))
  (is (= (escape-tree-set '([:⿻ "廿" "丙"])) #{"⿻" "廿" "丙"}))
  (is (= (escape-tree-set '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]]
                             [:⿱ "&CDP-85F0;" "一"]]))
         #{"⿳" "⿻" "一" "内" "⿱" "廿" "丙" "&CDP-85F0;"})))

(with-test
  (defn tokens-from-string
  "Beware positive and the negation of cuntrapositive not to overlap. Arguments are regular expressions."
  ([string]
   (tokens-from-string string #"."))
  ([string positive]
   (tokens-from-string string positive positive))
  ([string positive contrapositive]
   ((insta/parser
     (str "<S> = (Letter | Else)*"
          "<Letter> = " (-> (str "^(?!" contrapositive "$).")
                            re-pattern
                            print-str)
          "<Else> = " (-> positive
                          str
                          re-pattern
                          print-str)))
    string)))
  (is (= (tokens-from-string
          "⿳&CDP-8C4D;&CDP-8BF1;九"
          #"&(U\+|CDP\-)[0-9a-zA-Z]+;")
         '("⿳" "&CDP-8C4D;" "&CDP-8BF1;" "九"))))

(with-test
  (defn tree-to-ids
    [field]
    (reduce #(str %1 (cond (keyword? %2) (name %2)
                           (string? %2) %2
                           (vector? %2) (tree-to-ids (list %2))
                           :else (str %2)))
            "" (first field)))
  (is (= (tree-to-ids '([:⿻ "廿" "丙"])) "⿻廿丙"))
  (is (= (tree-to-ids '("兑")) "兑"))
  (is (= (tree-to-ids '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]]
                         [:⿱ "&CDP-85F0;" "一"]]))
         "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")))

(with-test
  (def escape-ids-set
    (comp escape-tree-set ids-to-tree))
  (is (= (escape-ids-set "&CDP-8C42;") #{"&CDP-8C42;"}))
  (is (= (escape-ids-set "兑") #{"兑"}))
  (is (= (escape-ids-set "⿻廿丙") #{"⿻" "廿" "丙"}))
  (is (= (escape-ids-set "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")
         #{"⿳" "⿻" "一" "内" "⿱" "廿" "丙" "&CDP-85F0;"})))

(with-test
  (defn ids-token-set
    [string]
    (filter #(nil? (re-matches
                    (re-pattern (aggregate-regeces #{:IDC}))
                    (escape-token %)))
            (escape-ids-set string)))
  (is (= (ids-token-set "⿻廿丙")
         #{"廿" "丙"})))

;; Now we can escape characters. We're willing to escape all sinographs but no
;; IDC, as in the standard definitions for it allows us to get a tree. We can
;; use te following syntax to achieve this goal.
(def selected-grammar
  (insta/parser
   (str
    "<S> = Letter | Form"
    definitions)))

(with-test
  (defn escaped-lexer
    [ids-string]
    (selected-grammar
     (apply str
            (map #(select-escape-token
                   %
                   common-rules-set)
                 (tokens-from-string ids-string re-codepoint)))))
  (is (= (escaped-lexer "⿰丿乚") '([:⿰ "&U+4E3F;" "&U+4E5A;"])))
  (is (= (escaped-lexer "⿰𠆢乚") '([:⿰ "&U+201A2;" "&U+4E5A;"])))
  (is (= (escaped-lexer "⿱口⿰丿乚") '([:⿰ "&U+201A2;" "&U+4E5A;"])))
  (is (= (escaped-lexer "⿰⿱𠆢⿳丶&CDP-8B7C;𠆢⿱丷⿱口⿰丿乚")
         '([:⿰ [:⿱ "&U+201A2;" [:⿳ "&U+4E36;" "&CDP-8B7C;" "&U+201A2;"]]
            [:⿱ "&U+4E37;" [:⿱ "&U+53E3;" [:⿰ "&U+4E3F;" "&U+4E5A;"]]]]))))

;; We consider sinographs and escaped tokens to be equivalent although the later
;; representation is easier to be dealt with. We can easily get dereferenced
;; tree as follows. However, we don't keep tokens in the same representation: either we get them all escaped, either we get them derefereced.

(with-test
  (def ids-to-tree
    #((comp deref-tree escaped-lexer) %))
  ;; Basic
  (is (= (ids-to-tree "兑") '("兑")))
  (is (= (ids-to-tree "⿳飠兑一") '([:⿳ "飠" "兑" "一"])))
  ;; Only works for single-rooted expression
  (is (= (class (ids-to-tree "飠兑")) instaparse.gll.Failure))
  ;; Non-BMP character
  (is (= (ids-to-tree "⿰𠆢乚") '([:⿰ "𠆢" "乚"])))
  ;; Nested IDS and codepoints
  (is (= (ids-to-tree "⿰⿱一⿵冂丶⿱一⿵冂丶")
         '([:⿰ [:⿱ "一" [:⿵ "冂" "丶"]] [:⿱ "一" [:⿵ "冂" "丶"]]])))
  (is (= (ids-to-tree "⿰一⿵冂丶")
         '([:⿰ "一" [:⿵ "冂" "丶"]])))
  (is (= (ids-to-tree "⿳&CDP-8C4D;&CDP-8BF1;九")
         '([:⿳ "&CDP-8C4D;" "&CDP-8BF1;" "九"])))
  (is (= (ids-to-tree "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")
         '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]] [:⿱ "&CDP-85F0;" "一"]])))
  (is (= (ids-to-tree "⿰⿱𠆢⿳丶&CDP-8B7C;𠆢⿱丷⿱口⿰丿乚")
         '([:⿰ [:⿱ "&U+201A2;" [:⿳ "&U+4E36;" "&CDP-8B7C;" "&U+201A2;"]]
            [:⿱ "&U+4E37;" [:⿱ "&U+53E3;" [:⿰ "&U+4E3F;" "&U+4E5A;"]]]])))
  ;; Tricky
  (is (= (ids-to-tree "⿳𠆢𠂊⿱口⿰&CDP-8BE8;乚")
         '([:⿳ "𠆢" "𠂊" [:⿱ "口" [:⿰ "&CDP-8BE8;" "乚"]]]))))
