(ns 華文.lexer.parser
  (:require [instaparse.core :as insta])
  (:use clojure.test))

(def definitions
  "
  Sep = '\t' | ' '*

  (* Operators *)
  ⿰ = <'⿰'> IDC2;             ⿱ = <'⿱'> IDC2;             ⿴ = <'⿴'> IDC2
  ⿵ = <'⿵'> IDC2;             ⿶ = <'⿶'> IDC2;             ⿷ = <'⿷'> IDC2
  ⿸ = <'⿸'> IDC2;             ⿹ = <'⿹'> IDC2;             ⿺ = <'⿺'> IDC2
  ⿻ = <'⿻'> IDC2;             ⿲ = <'⿲'> IDC3;             ⿳ = <'⿳'> IDC3

  (* Operator arities *)
  <IDC2> =  (Letter|S) (Letter|S);          <IDC3> =  IDC2 (Letter|S)

  <BeginEscape> = #'&(U\+|CDP\-)'
  <EndEscape> = ';'

  (* Operands *)
  (* Same as Han but with code and without IDC *)
  <Letter> =  Code | CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR
  | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS
  Code =      #'&[A-Z0-9-]+;' (* for code points*)
  (* As should be defined \\p{Han}. Currently unused *)
  Han =       CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR | IDC
  | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS

  (* Ideographs blocks *)
  <CJK> =     #'[\u4e00-\u9fff]';           <CJKA> =    #'[\u3400-\u4dbf]'
  <CJKB> =    #'[\u20000-\u2a6df]';         <CJKC> =    #'[\u2a700-\u2b73f]'
  <CJKD> =    #'[\u2b740-\u2B81F]';         <CJKE> =    #'[\u2b820-\u2ceaf]'
  <CJKRS> =   #'[\u2e80-\u2eff]';           <KR> =      #'[\u2f00-\u2fdf]'
  IDC =       #'[\u2ff0-\u2fff]';           <CJKSP> =   #'[\u3000-\u303f]'
  <CJKS> =    #'[\u31c0-\u31ef]';           <ECJKLM> =  #'[\u3200-\u32ff]'
  <CJKCo> =   #'[\u3300-\u33ff]';           <CJKCI> =   #'[\uf900-\ufaff]'
  <CJKCF> =   #'[\ufe30-\ufe4f]';           <CJKCIS> =  #'[\u2f800-\u2fa1f]'")

(defn dec-to-hex
  [number]
  (clojure.string/upper-case (format "%x" number)))

(defn hex-to-dec
  [string]
  (let [conversion-table (zipmap
                          (concat (map char (range 48 58)) (map char (range 65 91)))
                          (range))
        string (clojure.string/upper-case string)]
    (assert (every? #(< (conversion-table %) 16) string))
    (loop [num string
           acc 0]
      (if (seq num)
        (recur (drop 1 num) (+ (* 16 acc) (get conversion-table (first num))))
        acc))))

(with-test
  (defn get-codepoint-from-token
  "Expect a string made of one token. Not a character to overcome the Java
  limitation. The token is lousy defined to match &A; then allow easy test."
  [char-string]
  (if (re-matches #"&[A-Z0-9\+\-]+;" char-string)
    (clojure.string/replace char-string #"[&;]" (fn [i] ""))
    (str "U+" (clojure.string/upper-case (format "%x" (. char-string codePointAt 0))))))
  (is (= (get-codepoint-from-token "0") "U+30"))
  (is (= (get-codepoint-from-token "A") "U+41"))
  (is (= (get-codepoint-from-token "一") "U+4E00"))
  (is (= (get-codepoint-from-token "𠆢") "U+201A2"))
  (is (= (get-codepoint-from-token "&CDP-8B7C;") "CDP-8B7C"))
  (is (= (get-codepoint-from-token "&U+8B7C;") "U+8B7C"))
  (is (= (get-codepoint-from-token "&A;") "A")))

(with-test
  (defn get-escaped-from-token
    [char-string]
    (str "&" (get-codepoint-from-token char-string) ";"))
  (is (= (get-escaped-from-token "0") "&U+30;"))
  (is (= (get-escaped-from-token "A") "&U+41;"))
  (is (= (get-escaped-from-token "一") "&U+4E00;"))
  (is (= (get-escaped-from-token "𠆢") "&U+201A2;")) ;; fucking bitch
  (is (= (get-escaped-from-token "&CDP-8B7C;") "&CDP-8B7C;"))
  (is (= (get-escaped-from-token "&U+8B7C;") "&U+8B7C;"))
  (is (= (get-escaped-from-token "&A;") "&A;")))

(with-test
  (defn get-token-from-codepoint
    [codepoint]
    (if (re-matches #"U\+[A-Z0-9]+" codepoint)
      (String. (java.lang.Character/toChars (hex-to-dec (clojure.string/replace-first codepoint #"U\+" ""))))
      (str "&" codepoint ";")))
  (is (= (get-token-from-codepoint "U+30") "0"))
  (is (= (get-token-from-codepoint "U+41") "A"))
  (is (= (get-token-from-codepoint "U+4E00") "一"))
  (is (= (get-token-from-codepoint "U+201A2") "𠆢"))
  (is (= (get-token-from-codepoint "CDP-8B7C") "&CDP-8B7C;"))
  (is (= (get-token-from-codepoint "U+8B7C") "譼"))
  (is (= (get-token-from-codepoint "A") "&A;")))

(with-test
  (defn get-token-from-escaped
    [escaped]
    (if (re-matches #"&U\+[A-Z0-9]+;" escaped)
      (String. (java.lang.Character/toChars
                (hex-to-dec
                 (reduce #(clojure.string/replace-first %1 %2 "")
                         escaped
                         [#"&U\+" #";"]))))
      escaped))
  (is (= (get-token-from-escaped "&U+30;") "0"))
  (is (= (get-token-from-escaped "&U+41;") "A"))
  (is (= (get-token-from-escaped "&U+4E00;") "一"))
  (is (= (get-token-from-escaped "&U+201A2;") "𠆢"))
  (is (= (get-token-from-escaped "&CDP-8B7C;") "&CDP-8B7C;"))
  (is (= (get-token-from-escaped "&U+8B7C;") "譼"))
  (is (= (get-token-from-escaped "&A;") "&A;")))

(with-test
  (def from-ids-to-tree
  "Grammar for the Ideographic Description Sequence"
  (insta/parser
   (str
    "<S> =      Letter | Form
    <Form> =    ⿰ | ⿱ | ⿴ | ⿵ | ⿶ | ⿷ | ⿸ | ⿹ | ⿺ | ⿻ | ⿲ | ⿳"
    definitions ;; and beneath follow overloading
    "<Code> =   #'&[A-Z0-9-]+;' (* for code points*)")))
  ;; Basic
  (is (= (from-ids-to-tree "兑") '("兑")))
  (is (= (from-ids-to-tree "⿰飠兑") '([:⿰ "飠" "兑"])))
  ;; Only works for single-rooted expression
  (is (= (class (from-ids-to-tree "飠兑")) instaparse.gll.Failure))
  ;; Nested IDS and codepoints
  (is (= (from-ids-to-tree "⿰⿱一⿵冂丶⿱一⿵冂丶")
         '([:⿰ [:⿱ "一" [:⿵ "冂" "丶"]] [:⿱ "一" [:⿵ "冂" "丶"]]])))
  (is (= (from-ids-to-tree "⿰一⿵冂丶")
         '([:⿰ "一" [:⿵ "冂" "丶"]])))
  (is (= (from-ids-to-tree "⿻廿丙")
         '([:⿻ "廿" "丙"])))
  (is (= (from-ids-to-tree "⿳&CDP-8C4D;&CDP-8BF1;九")
         '([:⿳ "&CDP-8C4D;" "&CDP-8BF1;" "九"])))
  (is (= (from-ids-to-tree "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")
         '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]] [:⿱ "&CDP-85F0;" "一"]]))))

(with-test
  (defn from-tree-to-ids
    [field]
    (reduce #(str %1 (cond (keyword? %2) (name %2)
                           (string? %2) %2
                           (vector? %2) (from-tree-to-ids (list %2))
                           :else (str %2)))
            "" (first field)))
  (is (= (from-tree-to-ids '([:⿻ "廿" "丙"])) "⿻廿丙"))
  (is (= (from-tree-to-ids '("兑")) "兑"))
  (is (= (from-tree-to-ids '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]]
                              [:⿱ "&CDP-85F0;" "一"]]))
         "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")))

(with-test
  (defn from-tree-to-token-set
    [ids-tree]
    (reduce #(into %1 (cond (keyword? %2) (list (name %2))
                            (string? %2) (list %2)
                            (char? %2) (list (str %2))
                            (vector? %2) (from-tree-to-token-set (list %2))
                            :else (list %2)))
            #{} (#(if (coll? %) % (list %)) (first ids-tree))))
  (is (= (from-tree-to-token-set '("&CDP-8C42;")) #{"&CDP-8C42;"}))
  (is (= (from-tree-to-token-set '("兄")) #{"兄"}))
  (is (= (from-tree-to-token-set '([:⿻ "廿" "丙"])) #{"⿻" "廿" "丙"}))
  (is (= (from-tree-to-token-set '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]]
                                    [:⿱ "&CDP-85F0;" "一"]]))
         #{"⿳" "⿻" "一" "内" "⿱" "廿" "丙" "&CDP-85F0;"})))

(with-test
  (def from-ids-to-token-set
    (comp from-tree-to-token-set from-ids-to-tree))
  (is (= (from-ids-to-token-set "&CDP-8C42;") #{"&CDP-8C42;"}))
  (is (= (from-ids-to-token-set "兑") #{"兑"}))
  (is (= (from-ids-to-token-set "⿻廿丙") #{"⿻" "廿" "丙"}))
  (is (= (from-ids-to-token-set "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")
         #{"⿳" "⿻" "一" "内" "⿱" "廿" "丙" "&CDP-85F0;"})))

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
                                :expand (from-ids-to-token-set ids)
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
    (from-ids-to-tree
     (ids-toggle direction
                 (from-tree-to-ids tree)
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

;; Well, for now on we device to address the character issue. It comes from the JVM and the way Clojure directly refers to characters. I believe there must be a way for macros to solve it gracefully but anyway, the example-map variable previously defined is somewhat redundant. So let's define struct華文.
;; struct華文 is less redundant and handle multiple versions. More over it circumvents the Unicode issue and isolates it in the side-effect display function.
;; It becomes less easy for the programmer to know which character they manipulate because of the higher abstraction.
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
