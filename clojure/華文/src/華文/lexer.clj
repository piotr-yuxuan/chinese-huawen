;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.lexer
  (:require [instaparse.core :as insta]
            [華文.range-pattern :as range]
            [華文.char-manipulation :as mano])
  (:use clojure.test))

;; This is the final datastructure I want. It's realistic but only takes one
;; version. To populate it, one just has to parse then add a line. Before to be
;; queried, it has to be normalised. A structure is normalised iff:
;;  → Every sinographs in IDS have a corresponding key
;;  → Each row, IDS is as collapsed as possible

(def example-map
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
   "乚" {:codepoint "U+4E5A"
         :ids "乚"}})

(with-test
  (defn ids-toggle
  "ids is a string. The map is expected to be normalised but an unknown token
  will fallback to itself. þ todo: pass the map already translated to next
  iteration and only iterate on the new tokens."
  [direction ids map]
  (let [translation (reduce (fn [mapper token]
                                (assoc mapper
                                  (case direction
                                    :expand token
                                    :collapse (:ids (get map
                                                         token)))
                                  (case direction
                                    :expand (:ids (get map
                                                       token
                                                       {:ids token}))
                                    :collapse token)
                                  ))
                              {}
                              (case direction
                                :expand (mano/escape-ids-set ids)
                                :collapse (keys map)))]
    (println translation)
    (let [final (reduce (fn [current token]
                        (clojure.string/replace current
                                                (first token)
                                                (second token)))
                      ids
                      translation)]
      (if-not (= ids final)
        (ids-toggle direction final map)
        final))))
  ;; Basic
  (is (= "&A;"
         (ids-toggle :expand "&A;"
                     {"&A;" {:ids "&A;"}})))
  (is (= "&A;"
         (ids-toggle :collapse "&A;"
                     {"&A;" {:ids "&A;"}})))
  ;; Non normalised
  (is (= "⿳&B;&C;&D;"
         (ids-toggle :expand "&A;"
                     {"&A;" {:ids "⿳&B;&C;&D;"}})))
  (is (= "&A;"
         (ids-toggle :collapse "⿳&B;&C;&D;"
                     {"&A;" {:ids "⿳&B;&C;&D;"}})))
  ;; Non normalised, missing root
  (is (= "&A;"
         (ids-toggle :expand "&A;"
                     {"&B;" {:ids "⿳&C;&D;&E;"}})))
  (is (= "⿳&C;&D;&E;"
         (ids-toggle :collapse "⿳&C;&D;&E;"
                     {"&A;" {:ids "⿳&B;&C;&D;"}})))
  ;; Normalised
  (is (= "⿳⿰&E;&F;⿳&D;&E;&F;&D;"
         (ids-toggle :expand "&A;"
                     {"&A;" {:ids "⿳&B;&C;&D;"}
                      "&B;" {:ids "⿰&E;&F;"}
                      "&C;" {:ids "⿳&D;&E;&F;"}
                      "&D;" {:ids "&D;"}
                      "&E;" {:ids "&E;"}
                      "&F;" {:ids "&F;"}})))
  (is (= "&A;"
         (ids-toggle :collapse "⿳⿰&E;&F;⿳&D;&E;&F;&D;"
                     {"&A;" {:ids "⿳&B;&C;&D;"}
                      "&B;" {:ids "⿰&E;&F;"}
                      "&C;" {:ids "⿳&D;&E;&F;"}
                      "&D;" {:ids "&D;"}
                      "&E;" {:ids "&E;"}
                      "&F;" {:ids "&F;"}})))
  (is (= "⿱口⿰丿乚"
         (ids-toggle :expand "兄"
                     {"䬽" {:codepoint "U+4B3D" :ids "⿰飠兑"}
                      "飠" {:codepoint "U+98E0" :ids "⿱𠆢&CDP-8C42;"}
                      "兑" {:codepoint "U+5151" :ids "⿱丷兄"}
                      "𠆢" {:codepoint "U+201A2" :ids "𠆢"}
                      "&CDP-8C42;" {:codepoint "CDP-8C42" :ids "⿱丶&CDP-8B7C;"}
                      "丷" {:codepoint "U+4E37" :ids "丷"}
                      "兄" {:codepoint "U+5144" :ids "⿱口儿"}
                      "丶" {:codepoint "U+4E36" :ids "丶"}
                      "&CDP-8B7C;" {:codepoint "CDP-8B7C" :ids "&CDP-8B7C;"}
                      "口" {:codepoint "U+53E3" :ids "口"}
                      "儿" {:codepoint "U+513F" :ids "⿰丿乚"}
                      "丿" {:codepoint "U+4E3F" :ids "丿"}
                      "乚" {:codepoint "U+4E5A" :ids "乚"}})))
  (is (= "⿱口⿰丿乚"
         (ids-toggle :expand "䬽"
                     {"䬽" {:codepoint "U+4B3D" :ids "⿰飠兑"}
                      "飠" {:codepoint "U+98E0" :ids "⿱𠆢&CDP-8C42;"}
                      "兑" {:codepoint "U+5151" :ids "⿱丷兄"}
                      "𠆢" {:codepoint "U+201A2" :ids "𠆢"}
                      "&CDP-8C42;" {:codepoint "CDP-8C42" :ids "⿱丶&CDP-8B7C;"}
                      "丷" {:codepoint "U+4E37" :ids "丷"}
                      "兄" {:codepoint "U+5144" :ids "⿱口儿"}
                      "丶" {:codepoint "U+4E36" :ids "丶"}
                      "&CDP-8B7C;" {:codepoint "CDP-8B7C" :ids "&CDP-8B7C;"}
                      "口" {:codepoint "U+53E3" :ids "口"}
                      "儿" {:codepoint "U+513F" :ids "⿰丿乚"}
                      "丿" {:codepoint "U+4E3F" :ids "丿"}
                      "乚" {:codepoint "U+4E5A" :ids "乚"}}))))

(with-test
  (defn tree-toggle
    [direction tree map]
    (mano/ids-to-tree
     (ids-toggle direction
                 (mano/flatten-tree tree)
                 map)))
    ;; Basic
  (is (= '("&A;")
         (tree-toggle :expand '("&A;")
                     {"&A;" {:ids "&A;"}})))
  (is (= '("&A;")
         (tree-toggle :collapse '("&A;")
                     {"&A;" {:ids "&A;"}})))
  ;; Non normalised
  (is (= '([:⿳ "&B;" "&C;" "&D;"])
         (tree-toggle :expand '("&A;")
                     {"&A;" {:ids "⿳&B;&C;&D;"}})))
  (is (= '("&A;")
         (tree-toggle :collapse '([:⿳ "&B;" "&C;" "&D;"])
                     {"&A;" {:ids "⿳&B;&C;&D;"}})))
  ;; Non normalised, missing root
  (is (= '("&A;")
         (tree-toggle :expand '("&A;")
                     {"&B;" {:ids "⿳&C;&D;&E;"}})))
  (is (= '([:⿳ "&C;" "&D;" "&E;"])
         (tree-toggle :collapse '([:⿳ "&C;" "&D;" "&E;"])
                     {"&A;" {:ids "⿳&B;&C;&D;"}})))
  ;; Normalised
  (is (= '([:⿳ [:⿰ "&E;" "&F;"] [:⿳ "&D;" "&E;" "&F;"] "&D;"])
         (tree-toggle :expand '("&A;")
                     {"&A;" {:ids "⿳&B;&C;&D;"}
                      "&B;" {:ids "⿰&E;&F;"}
                      "&C;" {:ids "⿳&D;&E;&F;"}
                      "&D;" {:ids "&D;"}
                      "&E;" {:ids "&E;"}
                      "&F;" {:ids "&F;"}})))
  (is (= '("&A;")
         (tree-toggle :collapse
                      '([:⿳ [:⿰ "&E;" "&F;"] [:⿳ "&D;" "&E;" "&F;"] "&D;"])
                     {"&A;" {:ids "⿳&B;&C;&D;"}
                      "&B;" {:ids "⿰&E;&F;"}
                      "&C;" {:ids "⿳&D;&E;&F;"}
                      "&D;" {:ids "&D;"}
                      "&E;" {:ids "&E;"}
                      "&F;" {:ids "&F;"}}))))

;; Well, for now on we device to address the character issue. It comes from the
;; JVM and the way Clojure directly refers to characters. I believe there must
;; be a way for macros to solve it gracefully but anyway, the example-map
;; variable previously defined is somewhat redundant. So let's define
;; struct華文.
;; struct華文 is less redundant and handle multiple versions. More over it
;; circumvents the Unicode issue and isolates it in the side-effect display
;; function. It becomes less easy for the programmer to know which character
;; they manipulate because of the higher abstraction.
(def struct華文
  {"䬽" {:version {}
         :ids "⿰飠兑"}
   "飠" {:version {}
         :ids "⿱𠆢&CDP-8C42;"}
   "兑" {:version {}
         :ids "⿱丷兄"}
   "𠆢" {:version {}
         :ids "𠆢"}
   "&CDP-8C42;" {:version {}
                 :ids "⿱丶&CDP-8B7C;"}
   "丷" {:version {}
         :ids "丷"}
   "兄" {:version {}
         :ids "⿱口儿"}
   "丶" {:version {}
         :ids "丶"}
   "&CDP-8B7C;" {:version {}
                 :ids "&CDP-8B7C;"}
   "口" {:version {}
         :ids "口"}
   "儿" {:version {}
         :ids "⿰丿乚"}
   "丿" {:version {}
         :ids "丿"}
   "乚" {:version {}
         :ids "乚"}})

(def escaped-grammar
  (insta/parser
   (str
    "S = IDC CJK CJK"
    "\nIDC = #'[\u2ff0-\u2fff]'\n"
    (mano/print-rules #{:CJK} :range mano/block-ranges)
    ;(print-str #"&(U\+|CDP\-)4E00;")
    )))

(escaped-grammar (apply str (map mano/escape-token (mano/tokens-from-string "⿰丿乚" mano/re-codepoint))))

;; Now we can escape characters. We're willing to escape all sinographs but no
;; IDC, as in the standard definitions for it allows us to get a tree. We can
;; use te following syntax to achieve this goal.
(def selected-grammar
  (insta/parser
   (str
    "<S> = Letter | Form"
    mano/definitions)))

(with-test
  (defn escaped-lexer
    [ids-string]
    (selected-grammar
     (apply str
            (map #(mano/select-escape-token
                   %
                   mano/common-rules-set)
                 (mano/tokens-from-string ids-string mano/re-codepoint)))))
  (is (= (escaped-lexer "⿰丿乚") '([:⿰ "&U+4E3F;" "&U+4E5A;"])))
  (is (= (escaped-lexer "⿰𠆢乚") '([:⿰ "&U+201A2;" "&U+4E5A;"])))
  (is (= (escaped-lexer "⿱口⿰丿乚") '([:⿰ "&U+201A2;" "&U+4E5A;"])))
  (is (= (escaped-lexer "⿰⿱𠆢⿳丶&CDP-8B7C;𠆢⿱丷⿱口⿰丿乚")
         '([:⿰ [:⿱ "&U+201A2;" [:⿳ "&U+4E36;" "&CDP-8B7C;" "&U+201A2;"]]
            [:⿱ "&U+4E37;" [:⿱ "&U+53E3;" [:⿰ "&U+4E3F;" "&U+4E5A;"]]]]))))

;; We consider sinographs and escaped tokens to be equivalent although the later representation is easier to be dealt with. We can easily get dereferenced tree:

(with-test
  (def lexer
    #((comp mano/deref-tree escaped-lexer) %))
  (is (= (lexer "⿰丿乚") '([:⿰ "丿" "乚"])))
  (is (= (lexer "⿰𠆢乚") '([:⿰ "𠆢" "乚"])))
  (is (= (lexer "⿱口⿰丿乚") '([:⿱ "口" [:⿰ "丿" "乚"]])))
  (is (= (lexer "⿰⿱𠆢⿳丶&CDP-8B7C;𠆢⿱丷⿱口⿰丿乚")
         '([:⿰ [:⿱ "&U+201A2;" [:⿳ "&U+4E36;" "&CDP-8B7C;" "&U+201A2;"]]
            [:⿱ "&U+4E37;" [:⿱ "&U+53E3;" [:⿰ "&U+4E3F;" "&U+4E5A;"]]]]))))

;; and finally, we'll be done for this part once we'll have moved ids toggling
;; facilities to the escaped standard. By the way, toggling functions are way
;; two long and too similar not to be merged into a single generic one.
