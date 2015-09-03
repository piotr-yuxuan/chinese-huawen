;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.utils
  (:use clojure.test))

(with-test
  (defn dec-to-hex
    [number]
    (clojure.string/upper-case (format "%x" number)))
  (is (= (dec-to-hex 10) "A"))
  (is (= (dec-to-hex 256)) "100")
  (is (= (dec-to-hex 1911) "777")))

(with-test
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
  (is (= (hex-to-dec "777") 1911))
  (is (= (hex-to-dec "CAFE") 51966)))

(with-test
  (defn pow
  "Mathematical function power. LaTeX: $a^b$ where a is number and b is power."
  [number power]
  (reduce * (repeat power number)))
  (is (= (pow 2 3) 8))
  (is (= (pow 3 2) 9)))

(with-test
  (defn codepoint-to-hex
    "TODO: this is a non-sense and should be removed"
    [codepoint]
    (clojure.string/replace codepoint #"[U\+]" "")))
