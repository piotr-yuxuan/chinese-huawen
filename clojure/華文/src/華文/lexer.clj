(ns 華文.lexer.parser
  (:require [instaparse.core :as insta])
  (:use clojure.test))

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

(defn pow
  "Mathematical function power. LaTeX: $a^b$ where a is number and b is power."
  [number power]
  (reduce * (repeat power number)))

(defn power-extrema
  "Helper function for range-extrema, see its doc."
  [power direction]
  (cond
   (= direction :up) (dec-to-hex (dec (pow 16 (inc power))))
   (= direction :down) (dec-to-hex (if (= power 0) 0 (pow 16 power)))))

(with-test
  (defn range-extrema
  "Initial is an extrema. Return an extrema in the given direction with the same digit number. Direction can be :up or :down."
  [initial direction]
  (power-extrema (dec (count initial)) direction))
  (is (= (range-extrema "1" :up) "F"))
  (is (= (range-extrema "1" :down) "0"))
  (is (= (range-extrema "10" :up) "FF"))
  (is (= (range-extrema "10" :down) "10"))
  (is (= (range-extrema "15" :up) "FF"))
  (is (= (range-extrema "15" :down) "10")))

(with-test
  (defn range-full
  "Take as parameter two (decimal) integers and return hexadecimal ranges spanning over a power of 16. Power of 16 are from min-range (included) to max-range (included)."
  [min-range max-range]
  (if-not (or (< min-range 0) (< max-range 0))
    (loop [final []
           current min-range]
      (if (> current max-range)
        final
        (let [power (pow 10 current)]
          (recur (conj final [(str power) (range-extrema (str power) :up)])
                 (inc current)))))))
  (is (= (range-full 0 0) [["1" "F"]]))
  (is (= (range-full 2 3) [["100" "FFF"] ["1000" "FFFF"]])))

(with-test
  (defn equal-length-ranges
  "Expect two strings of hex. Return sorted vector of ranges."
  [start end]
  (cond
   (< (hex-to-dec end) (hex-to-dec start)) (equal-length-ranges end start)
   (= (count start) (count end)) [[start end]]
   (= 1 (- (count end) (count start))) [[start (range-extrema start :up)]
                                        [(power-extrema (count start) :down) end]]
   :else (into
          [[start (range-extrema start :up)]]
          (conj (range-full (count start) (- (count end) 2))
           [(range-extrema (str end) :down) end]))))
  (is (= (equal-length-ranges "11" "11") [["11" "11"]]))
  (is (= (equal-length-ranges "11" "85") [["11" "85"]]))
  (is (= (equal-length-ranges "11" "850") [["11" "FF"] ["100" "850"]]))
  (is (= (equal-length-ranges "11" "8500")
         [["11" "FF"] ["100" "FFF"] ["1000" "8500"]])))

(with-test
  (defn mask-from-left
  "Number is an hexadecimal number put into a string. Extant is the size of the mask from the left. Extant shoudln't be strictly greater than the `number` number of digits."
  [number extant]
  (str (subs number 0 (- (count number) extant)) (if-not (= extant 0) (power-extrema (dec extant) :up))))
  (is (= (mask-from-left "1A23" 0) "1A23"))
  (is (= (mask-from-left "1A22" 1) "1A2F"))
  (is (= (mask-from-left "1A22" 2) "1AFF"))
  (is (= (mask-from-left "1A22" 3) "1FFF"))
  (is (= (mask-from-left "1A22" 4) "FFFF")))

(with-test
  (defn split-into-simple-ranges
  "Expect a range. A range is made of a vector with two extrema. Each extremum is an hexadecimal number in a string. Return a vector of simpler ranges."
  [range]
  (loop [final []
         previous (first range)
         current 1]
    (let [masked (mask-from-left (first range) current)]
      (if (< (hex-to-dec masked) (hex-to-dec (last range)))
        (recur
         (conj final [previous masked])
         (dec-to-hex (inc (hex-to-dec masked)))
         (inc current))
        (conj final [previous (last range)])))))
  (is (= (split-into-simple-ranges ["1234" "1999"])
         [["1234" "123F"] ["1240" "12FF"] ["1300" "1999"]]))
  (is (= (split-into-simple-ranges ["1234" "2999"])
         [["1234" "123F"] ["1240" "12FF"] ["1300" "1FFF"] ["2000" "2999"]])))

(defn fun-longest-matching-prefix
  "Fun way to perform a reduction although in unpracticable for use. Keep in mind you have to test the result: if the whole string b is contained in a then it outputs the length of the prefix. If not, it outputs the longest prefix."
  [a b]
  (reduce #(if (= %2 (get a %1))
            (inc %1)
            (reduced (subs a 0 %1))) 0 b))

(with-test
  (defn matching-prefix-length
  "Return the length of the longest matching prefix for two strings a and b."
  [a b]
  (loop [length 0])
  (reduce #(if (= %2 (get a %1))
             (inc %1)
             (reduced %1)) 0 b))
  (is (= (matching-prefix-length "" "") 0))
  (is (= (matching-prefix-length "123" "1234") 3))
  (is (= (matching-prefix-length "1234" "12345") 4)))

(with-test
  (defn pattern-from-simple-range
  "Important: range is a vector containing two strings of same length which represent hexadecimal numbers."
  [range]
  (let [left (first range)
        right (second range)]
    (cond
     (> (hex-to-dec left) (hex-to-dec right)) (pattern-from-simple-range (reverse range))
     (= (hex-to-dec left) (hex-to-dec right)) left
     :else
     (let [length (apply matching-prefix-length range)
           a (get left length)
           b (get right length)
           pivot (cond
                  (re-matches #"[0-9]" (str b)) (str "[" a "-" b "]")
                  (re-matches #"[a-fA-F]" (str a)) (str "[" (clojure.string/lower-case a) "-" (clojure.string/lower-case b) (clojure.string/upper-case a) "-" (clojure.string/upper-case b) "]")
                  :else (str "[" a "-9a-" (clojure.string/lower-case b) "A-" (clojure.string/upper-case b) "]"))]
       (str
        (subs left 0 length)
        pivot
        (let [count (- (count left) length 1)]
          (cond
           (= count 1) (str "[0-9a-fA-F]")
           (> count 1) (str "[0-9a-fA-F]{" count "}"))))))))
  (is (= (simple-pattern-from-range ["2000" "2000"])
         "2000"))
  (is (= (simple-pattern-from-range ["2145" "21FF"])
         "21[4-9a-fA-F][0-9a-fA-F]"))
  (is (= (simple-pattern-from-range ["2000" "2FFF"])
         "2[0-9a-fA-F][0-9a-fA-F]{2}"))
  (is (= (simple-pattern-from-range ["21456" "2FFFF"])
         "2[1-9a-fA-F][0-9a-fA-F]{3}")))

(with-test
  (defn range-pattern-from-hex
  "Draft for Clojure implementation of the algorithm published here:
  http://utilitymill.com/utility/Regex_For_Range"
  [hex-start hex-end]
  ;; First, break into equal length ranges
  ;; Second, break into ranges that yield simple regexes
  ;; Turn each range into a regex
  ;; Collapse adjacent powers of 10
  ;; Combining the regexes above yields
  ;; Next we try to factor out common prefixes using a tree
  ;; Turning the parse tree into a regex yields
  ;; We choose the shorter one as our result
  (let [start (str (hex-to-dec hex-start))
        end (str (hex-to-dec hex-end))]
    (#(str "^(" % ")$")
     (reduce
      #(str %1 (if-not (= %1 "") "|") %2)
      ""
      (map pattern-from-simple-range
           (reduce
            #(into %1 (split-into-simple-ranges %2))
            []
            (equal-length-ranges hex-start hex-end)))))))
  (is (= (range-pattern-from-hex "0" "0")
         "^(0)$"))
  (is (= (range-pattern-from-hex "1" "A")
         "^([1-9a-aA-A])$"))
  (is (= (range-pattern-from-hex "10" "21")
         "^(1[0-9a-fA-F]|2[0-1])$"))
  (is (= (range-pattern-from-hex "1200" "129E")
         "^(120[0-9a-fA-F]|12[1-9][0-9a-fA-F])$")))

;; TODO: generate pattern on the fly
(def block-range
  {:CJK {:pattern "^(199(6[89]|[7-9][0-9])|[23][0-9]{4}|40([0-8][0-9]{2}|9[0-5][0-9]))$"
         :hex-range ["4E00" "9FFF"]
         :name ""}
   :CJKA {:pattern "^1(3(3(1[2-9]|[2-9][0-9])|[4-9][0-9]{2})|[4-8][0-9]{3}|9([0-8][0-9]{2}|90[0-3]))$"
          :hex-range ["3400" "4DBF"]
          :name ""}
   :CJKB {:pattern "^1(3(1(0(7[2-9]|[89][0-9])|[1-9][0-9]{2})|[2-9][0-9]{3})|[4-6][0-9]{4}|7([0-2][0-9]{3}|3([0-6][0-9]{2}|7([0-8][0-9]|9[01]))))$"
          :hex-range ["20000" "2A6DF"]
          :name ""}
   :CJKC {:pattern "^17(3(8(2[4-9]|[3-9][0-9])|9[0-9]{2})|[4-6][0-9]{3}|7([0-8][0-9]{2}|9([0-7][0-9]|8[0-3])))$"
          :hex-range ["2A700" "2B73F"]
          :name ""}
   :CJKD {:pattern "^17(79(8[4-9]|9[0-9])|8([01][0-9]{2}|20[0-7]))$"
          :hex-range ["2B740" "2B81F"]
          :name ""}
   :CJKE {:pattern "^1(7(8(2(0[89]|[1-9][0-9])|[3-9][0-9]{2})|9[0-9]{3})|8([0-2][0-9]{3}|3([0-8][0-9]{2}|9([0-7][0-9]|8[0-3]))))$"
          :hex-range ["2B820" "2CEAF"]
          :name ""}
   :CJKRS {:pattern "^1(19(0[4-9]|[1-9][0-9])|20([0-2][0-9]|3[01]))$"
           :hex-range ["2E80" "2EFF"]
           :name ""}
   :KR {:pattern "^12(0(3[2-9]|[4-9][0-9])|1[0-9]{2}|2([0-4][0-9]|5[0-5]))$"
        :hex-range ["2F00" "2FDF"]
	:name ""}
   :IDC {:pattern "^122(7[2-9]|8[0-7])$"
         :hex-range ["2FF0" "2FFF"]
         :name ""}
   :CJKSP {:pattern "^12(2(8[89]|9[0-9])|3([0-4][0-9]|5[01]))$"
           :hex-range ["3000" "303F"]
           :name ""}
   :CJKS {:pattern "^127(3[6-9]|[4-7][0-9]|8[0-3])$"
          :hex-range ["31C0" "31EF"]
          :name ""}
   :ECJKLM {:pattern "^1(2[89][0-9]{2}|30([0-4][0-9]|5[0-5]))$"
            :hex-range ["3200" "32FF"]
            :name ""}
   :CJKCo {:pattern "^13(0(5[6-9]|[6-9][0-9])|[12][0-9]{2}|3(0[0-9]|1[01]))$"
           :hex-range ["3300" "33FF"]
           :name ""}
   :CJKCI {:pattern "^6(3(7(4[4-9]|[5-9][0-9])|[89][0-9]{2})|4([01][0-9]{2}|2([0-4][0-9]|5[0-5])))$"
           :hex-range ["F900" "FAFF"]
           :name ""}
   :CJKCF {:pattern "^65(0(7[2-9]|[89][0-9])|10[0-3])$"
           :hex-range ["FE30" "FE4F"]
           :name ""}
   :CJKCIS {:pattern "^65(0(7[2-9]|[89][0-9])|10[0-3])$"
            :hex-range ["2F800" "2FA1F"]
            :name ""}})

(def grammar-test
  (insta/parser
   (str
    ;; entry point, default rule
    "<S> = " (reduce #(str %1 (if-not (= %1 "") " | ") %2)
                     ""
                     (map name (keys block-range-patterns)))
    ";\n"
    ;; Unicode Sinograph block rules
    (reduce #(str %1 (if-not (= %1 "") "\n") "<" (name (first %2)) "> = #'" (second %2) "';")
            ""
            block-range-patterns))))

(fn []
  (map name (keys block-range-patterns)))


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

  <BeginEscape> = #'&(U\+|CDP\-)'
  <EndEscape> = ';'

  (* Operands *)
  (* Same as Han but with code and without IDC *)
  <Letter> = " (str "Code |" (clojure.string/replace han-block "| IDC" "")) "
  Code =      #'&[A-Z0-9-]+;' (* for code points*)
  (* As should be defined \\p{Han}. Currently unused *)
  Han = " han-block "

  (* Ideographs blocks *)
"
  (reduce #(str %1 (if-not (= %1 "") "\n") "<" (name (first %2)) "> = #'" (second %2) "';") "" block-range-patterns)))

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
