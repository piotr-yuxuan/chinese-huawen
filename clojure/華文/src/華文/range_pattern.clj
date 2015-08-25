(ns 華文.range-pattern
  (:use clojure.test))

(defn dec-to-hex
  [number]
  (clojure.string/upper-case (format "%x" number)))

(defn hex-to-dec
  [string]
  (let [conversion-table (zipmap (concat (map char (range 48 58))
                                         (map char (range 65 71)))
                                 (range))
        string (clojure.string/upper-case string)]
    (assert (every? #(< (conversion-table %) 16) string))
    (loop [num string
           acc 0]
      (if (seq num)
        (recur (drop 1 num)
               (+ (* 16 acc)
                  (get conversion-table (first num))))
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
  "Initial is an extrema. Return an extrema in the given direction with the same
  digit number. Direction can be :up or :down."
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
  "Take as parameter two (decimal) integers and return hexadecimal ranges
  spanning over a power of 16. Power of 16 are from min-range (included) to
  max-range (included)."
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
                                        [(power-extrema (count start) :down)
                                         end]]
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
  "Number is an hexadecimal number put into a string. Extant is the size of the
  mask from the left. Extant shoudln't be strictly greater than the `number`
  number of digits."
  [number extant]
  (str (subs number 0 (- (count number) extant)) (if-not (= extant 0) (power-extrema (dec extant) :up))))
  (is (= (mask-from-left "1A23" 0) "1A23"))
  (is (= (mask-from-left "1A22" 1) "1A2F"))
  (is (= (mask-from-left "1A22" 2) "1AFF"))
  (is (= (mask-from-left "1A22" 3) "1FFF"))
  (is (= (mask-from-left "1A22" 4) "FFFF")))

(with-test
  (defn split-into-simple-ranges
  "Expect a range. A range is made of a vector with two extrema. Each extremum
  is an hexadecimal number in a string. Return a vector of simpler ranges."
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
  "Fun way to perform a reduction although in unpracticable for use. Keep in
  mind you have to test the result: if the whole string b is contained in a
  then it outputs the length of the prefix. If not, it outputs the longest
  prefix."
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
  "Important: range is a vector containing two strings of same length which
  represent hexadecimal numbers."
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
  (is (= (pattern-from-simple-range ["2000" "2000"])
         "2000"))
  (is (= (pattern-from-simple-range ["2145" "21FF"])
         "21[4-9a-fA-F][0-9a-fA-F]"))
  (is (= (pattern-from-simple-range ["2000" "2FFF"])
         "2[0-9a-fA-F][0-9a-fA-F]{2}"))
  (is (= (pattern-from-simple-range ["21456" "2FFFF"])
         "2[1-9a-fA-F][0-9a-fA-F]{3}")))

(with-test
  (defn range-pattern-from-hex
  "Draft for Clojure implementation of the algorithm published here:
  http://utilitymill.com/utility/Regex_For_Range.
  However, you add a keyword :word to this function signature if you're willing to
  get a full word."
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
    (reduce
     #(str %1 (if-not (= %1 "") "|") %2)
     ""
     (map pattern-from-simple-range
          (reduce
           #(into %1 (split-into-simple-ranges %2))
           []
           (equal-length-ranges hex-start hex-end))))))
  (is (= (range-pattern-from-hex "0" "0")
         "0"))
  (is (= (range-pattern-from-hex "1" "A")
         "[1-9a-aA-A]"))
  (is (= (range-pattern-from-hex "10" "21")
         "1[0-9a-fA-F]|2[0-1]"))
  (is (= (range-pattern-from-hex "1200" "129E")
         "120[0-9a-fA-F]|12[1-9][0-9a-fA-F]")))
