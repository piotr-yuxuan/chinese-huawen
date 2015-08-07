(ns 華文.lexer.parser
  (:require [instaparse.core :as insta]))

;; This is the parser file. This parser has several levels, described by the
;; documentation.

;; Level 0
(def ids "../../data/ids/ids.txt")
(if (.exists (clojure.java.io/as-file ids))
  (def file-reader (line-seq (clojure.java.io/reader ids)))
  (println "Not able to find file"))

(defn level-0
  [i]
  (nth file-reader i))

;; Level 1
(def columns [:codepoint :letter :ids])
(def separator-regular-expression #"\t")
(def line-grammar
  "Formal grammar to parse line."
  (insta/parser
   (str "S = S")))
(defn assoc-one-by-one
  "Associate each key of k to each value of v and skip if current key is nil."
  [k v]
  (loop [final {}
         [key & keys] k
         [value & values] v]
    (if (nil? key)
      (if (nil? keys)
        final
        (recur final keys values))
      (recur (assoc final key value) keys values))))
(defn split-line
  "Return a function which parses the line i-th line, according to the vector
  format and the regular expression separator."
  [format separator]
  #(assoc-one-by-one
    format
    (clojure.string/split (level-0 %) separator)))

(defn level-1
  [i]
  ((split-line columns separator-regular-expression) i))

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

;; þ Actually this is not the design I want: I want more grammars and simpler
;; ones. Then the first grammar parses raw "data-source custom" ids to formal ids. Then the formal ids grammar parse them to tree.

(def ids-grammar
  "Formal grammar to parse ids into trees."
   (insta/parser
   "S = S"))

;; Level 2.
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
  [])
