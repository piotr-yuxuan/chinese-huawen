(ns 華文.manipulation.char
  (:use clojure.test)
  (:require [instaparse.core :as insta])
  (:require [華文.consts :refer [re-codepoint unicode-blocks code-body]])
  (:require [華文.utils :refer [hex-to-dec]])
  (:require [華文.manipulation.regeces :refer [to-pattern]])
  (:require [clojure.string :refer [replace-first]]))

(with-test
  (defn token-to-codepoint
  "Expect a string made of one token. Not a character to overcome the Java
  limitation. The token is lousy defined to match &A; then allow easy test."
  [char-string]
  (if (re-matches (re-pattern re-codepoint) char-string)
    (replace char-string #"[&;]" (fn [i] ""))
    (str "U+"
         (upper-case (format "%x"
                             (. char-string codePointAt 0))))))
  (is (= (token-to-codepoint "0") "U+30"))
  (is (= (token-to-codepoint "A") "U+41"))
  (is (= (token-to-codepoint "一") "U+4E00"))
  (is (= (token-to-codepoint "𠆢") "U+201A2"))
  (is (= (token-to-codepoint "&CDP-8B7C;") "CDP-8B7C"))
  (is (= (token-to-codepoint "&U+8B7C;") "U+8B7C"))
  (is (= (token-to-codepoint "&U+A;") "U+A")))

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
  (defn escape-string
    "Beware this will escape each single character. Consider using a token set."
    [string]
    (map #((comp escape-token str) %) string)))

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
                                              unicode-blocks)) "&U+2FF1;"))
  (is (= (select-escape-token "𠆢"
                              (select-regeces #{:IDC :CJKB}
                                              :range
                                              unicode-blocks)) "&U+201A2;"))
  (is (= (select-escape-token "A"
                              (select-regeces (keys unicode-blocks)
                                              :range
                                              unicode-blocks)) "A")))

(with-test
  (defn deref-codepoint
    [codepoint]
    (if (re-matches (re-pattern code-body) codepoint)
      (String. (java.lang.Character/toChars (hex-to-dec (replace-first codepoint #"U\+" ""))))))
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
                (hex-to-dec
                 (reduce #(replace-first %1 %2 "")
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
