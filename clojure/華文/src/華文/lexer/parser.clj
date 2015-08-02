;; Here development is incremental so jump to the end to skip incremental changes.

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
    IDC = #'\\p{InIdeographic_Description_Characters}'
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
    L = #'\\P{InIdeographic_Description_Characters}' (* anything else *)"))
;; (as-and-bs "⿰⿱ab⿱cd")
;; => [:S [:F [:IDC2 "⿰"] [:F [:IDC2 "⿱"] [:L "a"] [:L "b"]] [:F [:IDC2 "⿱"] [:L "c"] [:L "d"]]]]

;; The following lines show something like tdd, but the repl prevented me from properly writing the unitary tests. Þ They should be added.

(def grammar
  (insta/parser
   "S = ((IDC3 (L | S)) | IDC2) (L | S) (L | S)
    IDC2 = '⿰' | '⿱' | '⿴' | '⿵' | '⿶' | '⿷' | '⿸' | '⿹' | '⿺' | '⿻'
    IDC3 = '⿲' | '⿳'
    IDC = #'\\p{InIdeographic_Description_Characters}'
    L = #'\\P{InIdeographic_Description_Characters}'"))
;; Finally we can use this grammar:
(defn parse-ids
  [i]
  (let [ids (nth (split i) 2)]
    (println ids)
    (grammar ids)))

(def grammar
  (insta/parser
   "S = S"))

;; However, this doesn't work on radical thus we need to enlarge the starting rule.
(def grammar
  (insta/parser
   "S = Form | (Form Id <(''|Sep)>)+
    Form =    Letter
              | (((IDC3 (Letter|S)) | IDC2) (Letter|S) (Letter|S))
    Id =      <'['> #'[A-Z]' <']'>
    Sep =     '\t'
    IDC2 =    '⿰' | '⿱' | '⿴' | '⿵' | '⿶' | '⿷' | '⿸' | '⿹' | '⿺' | '⿻'
    IDC3 =    '⿲' | '⿳'
    Code =    #'&[A-Z0-9-]+;' (* for not-yet-implemented code points*)

    (* Same as Han but with code and without IDC *)
    Letter =  Code | CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR
              | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS
    (* As should be defined \\p{Han} *)
    Han =     CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR | IDC | CJKSP
              | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS

    (* Ideographs blocks follow *)
    CJK =     #'[\u4e00-\u9fff]'
    CJKA =    #'[\u3400-\u4dbf]'
    CJKB =    #'[\u20000-\u2a6df]'
    CJKC =    #'[\u2a700-\u2b73f]'
    CJKD =    #'[\u2b740-\u2B81F]'
    CJKE =    #'[\u2b820-\u2ceaf]'
    CJKRS =   #'[\u2e80-\u2eff]'
    KR =      #'[\u2f00-\u2fdf]'
    IDC =     #'[\u2ff0-\u2fff]'
    CJKSP =   #'[\u3000-\u303f]'
    CJKS =    #'[\u31c0-\u31ef]'
    ECJKLM =  #'[\u3200-\u32ff]'
    CJKCo =   #'[\u3300-\u33ff]'
    CJKCI =   #'[\uf900-\ufaff]'
    CJKCF =   #'[\ufe30-\ufe4f]'
    CJKCIS =  #'[\u2f800-\u2fa1f]'"
   :output-format :hiccup ; or :enlive
   ))

;; The previous one is pretty advanced but we would like to deal better with Id
;; and get the result somewhat lighter.
(def grammar
  (insta/parser
   "S =       Letter | Form+
    Form =    (((IDC3 (Letter|S)) | IDC2) (Letter|S) (Letter|S) Id* <Sep*>)
    Id =      <'['> #'[A-Z]' <']'>
    Sep =     '\t'
    IDC2 =    '⿰' | '⿱' | '⿴' | '⿵' | '⿶' | '⿷' | '⿸' | '⿹' | '⿺' | '⿻'
    IDC3 =    '⿲' | '⿳'
    Code =    #'&[A-Z0-9-]+;' (* for not-yet-implemented code points*)

    (* Same as Han but with code and without IDC *)
    Letter =  Code | CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR
              | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS
    (* As should be defined \\p{Han} *)
    Han =     CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR | IDC | CJKSP
              | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS

    (* Ideographs blocks follow *)
    <CJK> =     #'[\u4e00-\u9fff]'
    <CJKA> =    #'[\u3400-\u4dbf]'
    <CJKB> =    #'[\u20000-\u2a6df]'
    <CJKC> =    #'[\u2a700-\u2b73f]'
    <CJKD> =    #'[\u2b740-\u2B81F]'
    <CJKE> =    #'[\u2b820-\u2ceaf]'
    <CJKRS> =   #'[\u2e80-\u2eff]'
    <KR> =      #'[\u2f00-\u2fdf]'
    IDC =     #'[\u2ff0-\u2fff]'
    <CJKSP> =   #'[\u3000-\u303f]'
    <CJKS> =    #'[\u31c0-\u31ef]'
    <ECJKLM> =  #'[\u3200-\u32ff]'
    <CJKCo> =   #'[\u3300-\u33ff]'
    <CJKCI> =   #'[\uf900-\ufaff]'
    <CJKCF> =   #'[\ufe30-\ufe4f]'
    <CJKCIS> =  #'[\u2f800-\u2fa1f]'"
   :output-format :hiccup ; or :enlive
   ))

;; And finally we would like to really get a tree structure and not something flattened. Moreover we solve the last problem: Id, which was actually not previously solved :/
;; Actually the Id problem accepts more than one solution. We choose to change the data structure (IDC vs. :Variant) but remember we must address it properly. Indeed it won't be trivial to deal with nested different variants. Another solution is to keep data homogeneous: the parsed collection returned contains one or 2*i elements. If one then no problem; if 2*i, then 0 mod 2 are variants and 1 mod 2 are variant ids. This data structure is more appealing for a Clojure mindset but from my umble current viewpoint, :Variant will warn very well the user the latter has to deal with the former.
;; In addition to this, ⿰&CDP-88F0;&CDP-8C4E;	⿰&CDP-8B5E;&CDP-8C4E; is a good counter-example for the flat solution. So basically you just have to count: if the output is counted more than 1, then we'll deal with variants. They sometimes are anonymous.
;; Same explanation for :Code.
(def grammar
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
    <IDC3> =  (Letter|S) (Letter|S) (Letter|S)

    (* Operands *)
    (* Same as Han but with code and without IDC *)
    <Letter> =  Code | CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR
                | CJKSP | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS
    Code =      #'&[A-Z0-9-]+;' (* for code points*)
    (* As should be defined \\p{Han}. Currently unused *)
    Han =       CJK | CJKA | CJKB | CJKC | CJKD | CJKE | CJKRS | KR | IDC | CJKSP
                | CJKS | ECJKLM | CJKCo | CJKCI | CJKCF | CJKCIS

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
