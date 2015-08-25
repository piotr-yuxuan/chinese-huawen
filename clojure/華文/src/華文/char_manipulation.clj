(ns 華文.char-manipulation
  (:require [instaparse.core :as insta]
            [華文.range-pattern :as range])
  (:use clojure.test))

(declare definitions)

(def code-escape
  "&")
(def prefix-u
  "U+")
(def prefix-cdp
  "CDP-")
(def prefix
  "(U\\+|CDP\\-)")
(def code-body
  (re-pattern (str prefix "[0-9a-fA-F]+")))
(def code-end-escape
  ";")
(def re-codepoint
  (re-pattern (str code-escape code-body code-end-escape)))

(def block-ranges
  {:CJK {:range ["U+4E00" "U+9FFF"]
         :name "CJK Unified Ideographs"}
   :CJKA {:range ["U+3400" "U+4DBF"]
          :name "CJK Unified Ideographs Extension A"}
   :CJKB {:range ["U+20000" "U+2A6DF"]
          :name "CJK Unified Ideographs Extension B"}
   :CJKC {:range ["U+2A700" "U+2B73F"]
          :name "CJK Unified Ideographs Extension C"}
   :CJKD {:range ["U+2B740" "U+2B81F"]
          :name "CJK Unified Ideographs Extension D"}
   :CJKE {:range ["U+2B820" "U+2CEAF"]
          :name "CJK Unified Ideographs Extension E"}
   :CJKRS {:range ["U+2E80" "U+2EFF"]
           :name "CJK Radicals Supplement"}
   :KR {:range ["U+2F00" "U+2FDF"]
        :name "Kangxi Radicals"}
   :IDC {:range ["U+2FF0" "U+2FFF"]
         :name "Ideographic Description Characters"}
   :CJKSP {:range ["U+3000" "U+303F"]
           :name "CJK Symbols and Punctuation"}
   :CJKS {:range ["U+31C0" "U+31EF"]
          :name "CJK Strokes"}
   :ECJKLM {:range ["U+3200" "U+32FF"]
            :name "Enclosed CJK Letters and Months"}
   :CJKCo {:range ["U+3300" "U+33FF"]
           :name "CJK Compatibility"}
   :CJKCI {:range ["U+F900" "U+FAFF"]
           :name "CJK Compatibility Ideographs"}
   :CJKCF {:range ["U+FE30" "U+FE4F"]
           :name "CJK Compatibility Forms"}
   :CJKCIS {:range ["U+2F800" "U+2FA1F"]
            :name "CJK Compatibility Ideographs Supplement"}})

(with-test
  (defn block-names
  "Generate a string of possible forms for a grammar. The default beaviour when an item must be both included and removed is to keep it."
  [keys add except]
  (reduce #(str %1 (if-not (= "" %1) " | ") (name %2)) ""
          (clojure.set/union
           (clojure.set/difference
            (set keys)
            (set except))
           (set add))))
  (is (= (block-names #{:a :b :c} nil nil) "c | b | a"))
  (is (= (block-names #{:a :b :c} #{:r} nil) "r | c | b | a"))
  (is (= (block-names #{:a :b :c} #{:a} nil) "c | b | a"))
  (is (= (block-names #{:a :b :c} #{:d} #{:c}) "b | d | a"))
  (is (= (block-names #{:a :b :c} #{:d} #{:d}) "c | b | d | a"))
  (is (= (block-names #{:a :b :c} #{:b} #{:c}) "b | a"))
  (is (= (block-names #{:a :b :c} #{:d} #{:e}) "c | b | d | a")))

;; Kept as example.
(def negative-grammar
  (insta/parser
   (let [kernel "&(U\\+|CDP\\-)[0-9a-zA-Z]+;"]
     (str "<S> = (Letter | Else)*"
          "Letter = " (print-str (re-pattern (str "^(?!" kernel "$).")))
          "Else = " (print-str (re-pattern (str #"" kernel "")))))))

(with-test
  (defn token-to-codepoint
  "Expect a string made of one token. Not a character to overcome the Java
  limitation. The token is lousy defined to match &A; then allow easy test."
  [char-string]
  (if (re-matches (re-pattern re-codepoint) char-string)
    (clojure.string/replace char-string #"[&;]" (fn [i] ""))
    (str "U+" (clojure.string/upper-case (format "%x" (. char-string codePointAt 0))))))
  (is (= (token-to-codepoint "0") "U+30"))
  (is (= (token-to-codepoint "A") "U+41"))
  (is (= (token-to-codepoint "一") "U+4E00"))
  (is (= (token-to-codepoint "𠆢") "U+201A2"))
  (is (= (token-to-codepoint "&CDP-8B7C;") "CDP-8B7C"))
  (is (= (token-to-codepoint "&U+8B7C;") "U+8B7C"))
  (is (= (token-to-codepoint "&U+A;") "U+A")))

(with-test
  (defn codepoint-to-hex
    [codepoint]
    (clojure.string/replace codepoint #"[U\+]" "")))

(with-test
  (defn escape-token
    [char-string]
    (str "&" (token-to-codepoint char-string) ";"))
  (is (= (escape-token "0") "&U+30;"))
  (is (= (escape-token "A") "&U+41;"))
  (is (= (escape-token "一") "&U+4E00;"))
  (is (= (escape-token "𠆢") "&U+201A2;")) ;; fucking bitch
  (is (= (escape-token "&CDP-8B7C;") "&CDP-8B7C;"))
  (is (= (escape-token "&U+8B7C;") "&U+8B7C;"))
  (is (= (escape-token "&U+A;") "&U+A;")))

(with-test
  (defn select-regeces
  "Expect a collection of keywords, a map of maps and a keyword. Inner maps must bind the specified keyword to a vector of range. See tests for examples."
  [rules-to-append keyword range-map]
  (let [rule-range #(apply
                     range/range-pattern-from-hex
                     (map codepoint-to-hex (get (last %) keyword)))
        rule-left #(str "<" (name (first %) ">"))]
    (reduce #(conj %1
                   (->  (str code-escape
                             prefix "(" (rule-range %2) ")"
                             code-end-escape)
                        re-pattern))
            #{}
            (filter
             #(contains? (set rules-to-append) (first %))
             range-map))))
  (is (= (select-regeces [:A :B]
                         :kw
                         {:A {:kw ["U+10" "U+20"]}
                          :B {:kw ["U+30" "U+40"]}})
         #{#"&(U\+|CDP\-)(3[0-9a-fA-F]|40);"
           #"&(U\+|CDP\-)(1[0-9a-fA-F]|20);"}))
  (is (= (select-regeces [:B]
                         :kw
                         {:A {:kw ["U+10" "U+20"]}
                          :B {:kw ["U+30" "U+40"]}})
         #{#"&(U\+|CDP\-)(1[0-9a-fA-F]|20);"})))

(def common-rules-set
  (select-regeces (disj (set (keys block-ranges)) :IDC)
                                    :range
                                    block-ranges))

(with-test
  (defn select-escape-token
    [char-string selected-regeces]
    (let [codepoint (escape-token char-string)
          match? (some #(re-matches % codepoint)
                       selected-regeces)]
      (if match?
        (escape-token char-string)
        char-string)))
  (is (= (select-escape-token "⿱"
                              (select-regeces #{:IDC}
                                              :range
                                              block-ranges)) "&U+2FF1;"))
  (is (= (select-escape-token "𠆢"
                              (select-regeces #{:IDC :CJKB}
                                              :range
                                              block-ranges)) "&U+201A2;"))
  (is (= (select-escape-token "A"
                              (select-regeces (keys block-ranges)
                                              :range
                                              block-ranges)) "A")))

(with-test
  (defn deref-codepoint
    [codepoint]
    (if (re-matches (re-pattern code-body) codepoint)
      (String. (java.lang.Character/toChars (range/hex-to-dec (clojure.string/replace-first codepoint #"U\+" ""))))))
  (is (= (deref-codepoint "U+30") "0"))
  (is (= (deref-codepoint "U+41") "A"))
  (is (= (deref-codepoint "U+4E00") "一"))
  (is (= (deref-codepoint "U+201A2") "𠆢"))
  (is (= (deref-codepoint "CDP-8B7C") "&CDP-8B7C;"))
  (is (= (deref-codepoint "U+8B7C") "譼"))
  (is (= (deref-codepoint "U+A") "&A;")))

(with-test
  (defn deref-escaped
    [escaped]
    (if (re-matches #"&U\+[A-Z0-9]+;" escaped)
      (String. (java.lang.Character/toChars
                (range/hex-to-dec
                 (reduce #(clojure.string/replace-first %1 %2 "")
                         escaped
                         [#"&U\+" #";"]))))
      escaped))
  (is (= (deref-escaped "&U+30;") "0"))
  (is (= (deref-escaped "&U+41;") "A"))
  (is (= (deref-escaped "&U+4E00;") "一"))
  (is (= (deref-escaped "&U+201A2;") "𠆢"))
  (is (= (deref-escaped "&CDP-8B7C;") "&CDP-8B7C;"))
  (is (= (deref-escaped "&U+8B7C;") "譼"))
  (is (= (deref-escaped "&A;") "&A;")))

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
  (defn flatten-tree
    [field]
    (reduce #(str %1 (cond (keyword? %2) (name %2)
                           (string? %2) %2
                           (vector? %2) (flatten-tree (list %2))
                           :else (str %2)))
            "" (first field)))
  (is (= (flatten-tree '([:⿻ "廿" "丙"])) "⿻廿丙"))
  (is (= (flatten-tree '("兑")) "兑"))
  (is (= (flatten-tree '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]]
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
  (defn print-rules
  "Expect a collection of keywords, a map of maps and a keyword. Inner maps
  must bind the specified keyword to a vector of range. See tests for examples."
  [rules-to-append keyword range-map]
  (let [rule-range #(apply
                     range/range-pattern-from-hex
                     (map codepoint-to-hex (get (last %) keyword)))
        rule-left #(str "<" (name (first %) ">"))]
    (reduce #(str %1
                  "<" (name (first %2)) "> = "
                  (->  (str code-escape
                            prefix "(" (rule-range %2) ")"
                            code-end-escape)
                       re-pattern
                       print-str)
                  ";\n")
            "\n"
            (filter
             #(contains? (set rules-to-append) (first %))
             range-map))))
  (is (= (print-rules [:A :B]
                       :kw
                       {:A {:kw ["U+10" "U+20"]}
                        :B {:kw ["U+30" "U+40"]}})
         (str "\n<A> = #\"&(U\\+|CDP\\-)(1[0-9a-fA-F]|20);\";"
              "\n<B> = #\"&(U\\+|CDP\\-)(3[0-9a-fA-F]|40);\";\n"))))

(def definitions
  (str "
  Sep = '\t' | ' '*

  (* Operators *)
  ⿰ = <'⿰'> IDC2;             ⿱ = <'⿱'> IDC2;             ⿴ = <'⿴'> IDC2
  ⿵ = <'⿵'> IDC2;             ⿶ = <'⿶'> IDC2;             ⿷ = <'⿷'> IDC2
  ⿸ = <'⿸'> IDC2;             ⿹ = <'⿹'> IDC2;             ⿺ = <'⿺'> IDC2
  ⿻ = <'⿻'> IDC2;             ⿲ = <'⿲'> IDC3;             ⿳ = <'⿳'> IDC3

  (* Operator arities *)
  <IDC2> =  (Letter|S) (Letter|S);          <IDC3> =  IDC2 (Letter|S)

  (* Operands *)
  (* Same as Han but with code and without IDC *)
  <Letter> = " (block-names (keys block-ranges) #{:Code} #{:IDC}) "
  <Form> =    ⿰ | ⿱ | ⿴ | ⿵ | ⿶ | ⿷ | ⿸ | ⿹ | ⿺ | ⿻ | ⿲ | ⿳
  <Code> = " (-> re-codepoint re-pattern print-str) " (* for code points*)
  (* As should be defined \\p{Han}. Currently unused *)
  Han = " (block-names (keys block-ranges) nil nil) "

  (* Ideographs blocks *)"
  (print-rules (keys block-ranges) :range block-ranges)))

(with-test
  (def ids-to-tree
    "Grammar for the Ideographic Description Sequence"
    (insta/parser
     (str
      "<S> = Letter | Form"
      definitions)))
  ;; Basic
  (is (= (ids-to-tree "兑") '("兑")))
  (is (= (ids-to-tree "⿰飠兑") '([:⿰ "飠" "兑"])))
  ;; Only works for single-rooted expression
  (is (= (class (ids-to-tree "飠兑")) instaparse.gll.Failure))
  ;; Nested IDS and codepoints
  (is (= (ids-to-tree "⿰⿱一⿵冂丶⿱一⿵冂丶")
         '([:⿰ [:⿱ "一" [:⿵ "冂" "丶"]] [:⿱ "一" [:⿵ "冂" "丶"]]])))
  (is (= (ids-to-tree "⿰一⿵冂丶")
         '([:⿰ "一" [:⿵ "冂" "丶"]])))
  (is (= (ids-to-tree "⿻廿丙")
         '([:⿻ "廿" "丙"])))
  (is (= (ids-to-tree "⿳&CDP-8C4D;&CDP-8BF1;九")
         '([:⿳ "&CDP-8C4D;" "&CDP-8BF1;" "九"])))
  (is (= (ids-to-tree "⿳廿⿻丙⿱一内⿱&CDP-85F0;一")
         '([:⿳ "廿" [:⿻ "丙" [:⿱ "一" "内"]] [:⿱ "&CDP-85F0;" "一"]]))))

(map deref-escaped
     (map escape-token
          (tokens-from-string "⿳&CDP-8C4D;𠆢内" #"&(U\+|CDP\-)[0-9a-zA-Z]+;")))
