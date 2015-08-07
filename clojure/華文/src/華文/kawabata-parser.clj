(ns 華文.parser.kawabata
  (:require [instaparse.core :as insta])
  (:use clojure.test))

;; This is the parser file. As I don't know yet how to structure the logic,
;; let's say the parser has several levels.

;; Level 0.
;; Example of args
(def path "../../data/ids/ids.txt")

(defn level-0
  [path]
  (if (.exists (clojure.java.io/as-file path))
    (fn [i] (nth (line-seq (clojure.java.io/reader path)) i))
    (do (println "Not able to find file")
        (fn [i] nil))))

;; Presenter or splitter is level 1. This could be seen as a scanner.
(defn line-presenter
  "Split and return the i-th line"
  [i]
  (clojure.string/split (nth file-reader i) #"\t"))
(deftest line-presenter-test
  (let [expected "OK";; "U+4E0E\t与\t⿹&CDP-8BBF;一[GTKV]\t⿻&CDP-8BBF;一[J]"
        nth (fn [a b] (str expected "O"))]
    (is (= 4 (+ 2 2)))
    (is (= expected (line-presenter 15)))
    (is (= 7 (+ 3 4)))))

(with-test
  (defn line-splitter
    [string]
    ))
(def definitions
  "    (* Operators *)
  ⿰ = <'⿰'> IDC2;             ⿱ = <'⿱'> IDC2;             ⿴ = <'⿴'> IDC2
  ⿵ = <'⿵'> IDC2;             ⿶ = <'⿶'> IDC2;             ⿷ = <'⿷'> IDC2
  ⿸ = <'⿸'> IDC2;             ⿹ = <'⿹'> IDC2;             ⿺ = <'⿺'> IDC2
  ⿻ = <'⿻'> IDC2;             ⿲ = <'⿲'> IDC3;             ⿳ = <'⿳'> IDC3

  (* Operator arities *)
  <IDC2> =  (Letter|S) (Letter|S);          <IDC3> =  IDC2 (Letter|S)

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

(with-test
  (def grammar
  "Formal context-free grammar for the file ids.txt."
  (insta/parser
   (str
    "<S> =       C <Sep> Letter <Sep> (Letter | (Variant <Sep*>)+ | Form)
     <C> = #'[A-Z0-9\\-\\+]+'
     (* <S> =       Letter | (Variant <Sep*>)+ | Form *)
     Sep =       ('\t' | ' ')+
     Variant =   Form Id* (* needed because <S> *)
     <Form> =    ⿰ | ⿱ | ⿴ | ⿵ | ⿶ | ⿷ | ⿸ | ⿹ | ⿺ | ⿻ | ⿲ | ⿳
     Id =        <'['> #'[A-Z]'+ <']'> (* Assuming one letter per version *)"
    definitions)))
  (is (= (grammar "U+2E84\t⺄\t⺄") '("U+2E84" "⺄" "⺄")))
  (is (= (grammar "U+70EB\t烫\t⿱汤火") '("U+70EB" "烫" [:⿱ "汤" "火"]))))

(with-test
  (defn assoc-one-by-one
    "Map each i-th key with i-th value in a map."
    ([k v]
     (assoc-one-by-one {} k v))
    ([initial k v]
     (loop [final initial
           [key & keys] k
           [value & values] v]
       (if (nil? key)
         (if (nil? keys)
           final
           (recur final keys values))
         (recur (assoc final key value)
                keys
                values)))))
  (is (= (assoc-one-by-one [] []) {}))
  (is (= (assoc-one-by-one [:a :b] [1 2]) {:a 1 :b 2}))
  (is (= (assoc-one-by-one [:a :b] [1]) {:a 1 :b nil}))
  (is (= (assoc-one-by-one [:a :b] [1 nil]) {:a 1 :b nil}))
  (is (= (assoc-one-by-one [:a :b :c] [1 nil 3]) {:a 1 :b nil :c 3}))
  (is (= (assoc-one-by-one [:a :b] [1 2 3]) {:a 1 :b 2}))
  (is (= (assoc-one-by-one [:a nil] [1 2]) {:a 1}))
  (is (= (assoc-one-by-one [:a nil :c] [1 2 3]) {:a 1 :c 3}))
  (is (= (assoc-one-by-one [:a :b nil] [1 2 3]) {:a 1 :b 2})))

;; The grammar itself is used by level 3

;; And finally we would like to really get a tree structure and not something
;; flattened. Moreover we solve the last problem: Id, which was actually not
;; previously solved ><

;; Actually the Id problem accepts more than one solution. We choose to change
;; the data structure (IDC vs. :Variant) but remember we must address it
;; properly. Indeed it won't be trivial to deal with nested different variants.

;; Another solution is to keep data homogeneous: the parsed collection returned
;; contains one or 2*i elements. If one then no problem; if 2*i, then 0 mod 2
;; are variants and 1 mod 2 are variant ids. This data structure is more
;; appealing for a Clojure mindset but from my umble current viewpoint, :Variant
;; will warn very well the user the latter has to deal with the former.

;; In addition to this, ⿰&CDP-88F0;&CDP-8C4E;	⿰&CDP-8B5E;&CDP-8C4E; is a good
;; counter-example for the flat solution. So basically you just have to count:
;; if the output is counted more than 1, then we'll deal with variants. They
;; sometimes are anonymous.
;; Same explanation for :Code.
(def grammar
  "Formal context-free grammar for the file ids.txt."
   (insta/parser
   "<S> =       Letter | (Variant <Sep*>)+ | Form
    Sep =       '\t' | ' '*
    Variant =   Form Id* (* needed because <S> *)
    <Form> =    ⿰ | ⿱ | ⿴ | ⿵ | ⿶ | ⿷ | ⿸ | ⿹ | ⿺ | ⿻ | ⿲ | ⿳
    Id =        <'['> #'[A-Z]'+ <']'> (* Assuming one letter per version *)

    (* Operators *)
    ⿰ = <'⿰'> IDC2
    ⿱ = <'⿱'> IDC2
    ⿴ = <'⿴'> IDC2
    ⿵ = <'⿵'> IDC2
    ⿶ = <'⿶'> IDC2
    ⿷ = <'⿷'> IDC2
    ⿸ = <'⿸'> IDC2
    ⿹ = <'⿹'> IDC2
    ⿺ = <'⿺'> IDC2
    ⿻ = <'⿻'> IDC2
    ⿲ = <'⿲'> IDC3
    ⿳ = <'⿳'> IDC3

    (* Operator arities *)
    <IDC2> =  (Letter|S) (Letter|S)
    <IDC3> =  IDC2 (Letter|S)

    (* Operands *)
    (* Same as Han but with code and without IDC *)
    <Letter> =  Code | CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR
                | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS
    Code =      #'&[A-Z0-9-]+;' (* for code points*)
    (* As should be defined \\p{Han}. Currently unused *)
    Han =       CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR | IDC
                | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS

    (* Ideographs blocks *)
    <CJK> =     #'[\u4e00-\u9fff]'
    <CJKA> =    #'[\u3400-\u4dbf]'
    <CJKB> =    #'[\u20000-\u2a6df]'
    <CJKC> =    #'[\u2a700-\u2b73f]'
    <CJKD> =    #'[\u2b740-\u2B81F]'
    <CJKE> =    #'[\u2b820-\u2ceaf]'
    <CJKRS> =   #'[\u2e80-\u2eff]'
    <KR> =      #'[\u2f00-\u2fdf]'
    IDC =       #'[\u2ff0-\u2fff]'
    <CJKSP> =   #'[\u3000-\u303f]'
    <CJKS> =    #'[\u31c0-\u31ef]'
    <ECJKLM> =  #'[\u3200-\u32ff]'
    <CJKCo> =   #'[\u3300-\u33ff]'
    <CJKCI> =   #'[\uf900-\ufaff]'
    <CJKCF> =   #'[\ufe30-\ufe4f]'
    <CJKCIS> =  #'[\u2f800-\u2fa1f]'"))

;; This is level three, where you say the line you want to be parsed and its
;; syntactic tree is returned if possible; return nil otherwise
(defn ids-parser
  "If the line selected contains an ids then it parses it. If not, it ouputs a
  warning message."
  [i]
  (let [splitted-line (line-presenter i)]
    (if (and (coll? splitted-line)
             (= 3 (count splitted-line)))
      (first (grammar (nth splitted-line 2)))
      (println (str "can't be parsed: " (first splitted-line))))))

(defn file-crawler
  "Crawl the file"
  []
  "lol")
