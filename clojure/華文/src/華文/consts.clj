;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.consts
  (:use clojure.test)
  (:require [華文.manipulation.regeces :refer [to-pattern]])
  (:require [華文.utils :refer [codepoint-to-hex]]))

(declare print-rules
         block-names)

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

(def unicode-blocks
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
  <Letter> = " (block-names (keys unicode-blocks) #{:Code} #{:IDC}) "
  <Form> =    ⿰ | ⿱ | ⿴ | ⿵ | ⿶ | ⿷ | ⿸ | ⿹ | ⿺ | ⿻ | ⿲ | ⿳
  <Code> = " (-> re-codepoint re-pattern print-str) " (* for code points*)
  (* As should be defined \\p{Han}. Currently unused *)
  Han = " (block-names (keys unicode-blocks) nil nil) "

  (* Ideographs blocks *)"
  (print-rules (keys unicode-blocks) :range unicode-blocks)))

(with-test
  (defn- print-rules
  "Expect a collection of keywords, a map of maps and a keyword. Inner maps
  must bind the specified keyword to a vector of range. See tests for examples."
  [rules-to-append keyword range-map]
  (let [rule-range #(apply
                     to-pattern
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

(with-test
  (defn- block-names
  "Generate a string of possible forms for a grammar. The default beaviour when
  an item must be both included and removed is to keep it."
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
