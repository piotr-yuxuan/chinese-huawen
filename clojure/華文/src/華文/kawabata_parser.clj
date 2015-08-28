;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.parser.kawabata
  (:require [instaparse.core :as insta]
            [華文.ids-manipulation :as im]
            [華文.char-manipulation :as cm])
  (:use clojure.test))

;; This is the parser file. As I don't know yet how to structure the logic,
;; let's say the parser has several levels.

;; Level 0.
;; Example of args
(def path "../../data/ids/ids.txt")

(with-test
  (defn level-0
    [path]
    (if (.exists (clojure.java.io/as-file path))
      (fn [i] (nth (line-seq (clojure.java.io/reader path)) i))
      (do (println "Not able to find file")
          (fn [i] nil))))
  ;; I acknowledge this test is absolutely not pure. Just take it to give an
  ;; idea how to use it.
  (is (= ((level-0 path) 4)
         "U+2E80\t⺀\t⿱丶丶")))

;; Level 1.
(with-test
  (defn version-from-ids
  "Return nil if no versions are specified, otherwise a list of versions.
  It can be factorised to a more elegant single regular expression but I'm not
  that skilled yet :/"
  [ids-string]
  (if-let [chunk (re-find #"\[.+\]"
                          ids-string)]
    (re-seq #"[a-zA-Z]"
            chunk)))
  (is (= (version-from-ids "⿳&CDP-8C4D;土九[GK]") '("G" "K")))
  (is (= (version-from-ids "⿳&CDP-8C4D;土九") nil)))

(with-test
  (defn map-ids-to-version
    [versioned-ids]
    (reduce #(assoc %
               (keyword %2)
               (clojure.string/replace versioned-ids #"\[.+\]" ""))
            {}
            (apply list (version-from-ids versioned-ids))))
  (is (= (map-ids-to-version "⿳&CDP-8C4D;土九[GK]") {:G "⿳&CDP-8C4D;土九", :K "⿳&CDP-8C4D;土九"})))

(with-test
  (defn level-1
  "Container is the container to fill, level-0-output it the row returned by the
  eponymous function"
  [container level-0-function]
  (fn [row-number]
    (let [[codepoint glyph & ids] (clojure.string/split
                                   (level-0-function row-number)
                                   #"\t")]
      (assoc container
        (cm/escape-token glyph)
        (if (< 1 (count ids))
          (reduce #(into % (map-ids-to-version %2))
                  {nil (clojure.string/replace (first ids) #"\[.+\]" "")}
                  ids)
          {nil (clojure.string/replace (first ids) #"\[.+\]" "")})))))
  ;; With versions
  (is (= ((level-1 {} (level-0 path)) 203)
         {"&U+4EB4;" {nil "⿳&CDP-8C4D;土九"
                      :G "⿳&CDP-8C4D;土九"
                      :K "⿳&CDP-8C4D;土九"
                      :T "⿳&CDP-8C4D;&CDP-8BF1;九"}}))
  ;; Without versions
  (is (= ((level-1 {} (level-0 path)) 250)
         {"&U+4EE3;" {nil "⿰亻弋"}}))
  (is (= ((level-1 {} (level-0 path)) 5177)
         {"&U+6222;" {nil "⿰咠戈"}})))

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
  (def file-grammar
  "Must return three columns, whatever is in the third one."
  (insta/parser
   (str
    "<S> = Codepoint  <('\t' | ' ')+> Letter  <('\t' | ' ')+> #'.+'
     <Codepoint> = #'[A-Z0-9\\-\\+]+'"
    definitions)))
  (is (= (file-grammar "U+2E86\t⺆\t⺆") '("U+2E86" "⺆" "⺆")))
  (is (= (file-grammar "U+4EB4	亴\t⿳&CDP-8C4D;土九[GK]\t⿳&CDP-8C4D;&CDP-8BF1;九[T]")
         '("U+4EB4" "亴" "⿳&CDP-8C4D;土九[GK]\t⿳&CDP-8C4D;&CDP-8BF1;九[T]"))))

(with-test
  (def split-ids-grammar
  "Must return three columns, whatever is in the third one."
  (insta/parser
   (str
    "<S> = (Version <Sep*>)+
     Version = Ids Id*
     <Id>  = <'['> #'[A-Z]'+ <']'> (* Assuming one letter per version *)
     Sep = '\t' | ' '
     <Ids> = #'((&[A-Z0-9-]+;)|[\u4e00-\u9fff\u3400-\u4dbf\u20000-\u2a6df\u2a700-\u2b73f\u2b740-\u2B81F\u2b820-\u2ceaf\u2e80-\u2eff\u2f00-\u2fdf\u2ff0-\u2fff\u3000-\u303f\u31c0-\u31ef\u3200-\u32ff\u3300-\u33ff\uf900-\ufaff\ufe30-\ufe4f\u2f800-\u2fa1f])+'")))
  ;; Without version, should not be altered.
  (is (= (split-ids-grammar "竹⿰氵") '("竹⿰氵")))
  ;; With versions, versions should be split apart.
  (is (= (split-ids-grammar "竹⿰氵[T]\t⿺寸丶[JKG]")
         '("竹⿰氵[T]" "⿺寸丶[JKG]"))))

(with-test
  (def step-by-step-grammar
  "Must return three columns, whatever is in the third one."
  (insta/parser
   (str
    "<S> = IDC2 | IDC3
     Version = Ids Id*
     <Id>  = <'['> #'[A-Z]'+ <']'> (* Assuming one letter per version *)
     Sep = '\t' | ' '
     <Ids> = #'((&[A-Z0-9-]+;)|[\u4e00-\u9fff\u3400-\u4dbf\u20000-\u2a6df\u2a700-\u2b73f\u2b740-\u2B81F\u2b820-\u2ceaf\u2e80-\u2eff\u2f00-\u2fdf\u2ff0-\u2fff\u3000-\u303f\u31c0-\u31ef\u3200-\u32ff\u3300-\u33ff\uf900-\ufaff\ufe30-\ufe4f\u2f800-\u2fa1f])+'"
    definitions)))
  ;; Without version, should not be altered.
  (is (= (split-ids-grammar "竹⿰氵") '("竹⿰氵")))
  ;; With versions, versions should be split apart.
  (is (= (split-ids-grammar "竹⿰氵[T]\t⿺寸丶[JKG]")
         '("竹⿰氵[T]" "⿺寸丶[JKG]"))))

;; "<S> = (Version <Sep*>)+
;;     Version = Ids Id*
;;     Id  = <'['> #'[A-Z]'+ <']'> (* Assuming one letter per version *)
;;     Sep = '\t' | ' '
;; Ids = #'((&[A-Z0-9-]+;)|[\u4e00-\u9fff\u3400-\u4dbf\u20000-\u2a6df\u2a700-\u2b73f\u2b740-\u2B81F\u2b820-\u2ceaf\u2e80-\u2eff\u2f00-\u2fdf\u2ff0-\u2fff\u3000-\u303f\u31c0-\u31ef\u3200-\u32ff\u3300-\u33ff\uf900-\ufaff\ufe30-\ufe4f\u2f800-\u2fa1f])+'"

(defn harden-grammar
  "Stop every upon single grammatical error"
  [i j grammar]
  (loop [current i
         look-up (level-0 path)]
    (if (< current j)
      (let [result (grammar (look-up current))]
        (if (= instaparse.gll.Failure (class result))
          (do (println (look-up current))
              current)
          (recur (inc current)
                 look-up))))))

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
