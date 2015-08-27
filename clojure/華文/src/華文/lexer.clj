;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns 華文.lexer
  (:require [instaparse.core :as insta]
            [華文.char-manipulation :as cm]
            [華文.ids-manipulation :as im])
  (:use clojure.test))

;; This is the final datastructure I want. It's realistic but only takes one
;; version. To populate it, one just has to parse then add a line. Before to be
;; queried, it has to be normalised. A structure is normalised iff:
;;  → Every sinographs in IDS have a corresponding key
;;  → Each row, IDS is as collapsed as possible

;(def example-map)
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
}

;; Well, for now on we device to address the character issue. It comes from the
;; JVM and the way Clojure directly refers to characters. I believe there must
;; be a way for macros to solve it gracefully but anyway, the example-map
;; variable previously defined is somewhat redundant.

;; So let's define a container less redundant which handle multiple versions and
;; circumvents the Unicode issue by isolating it in the side-effect display
;; function (moving from "does it belong to the BMP?" to "do I have a proper
;; font to render it?"). It becomes less easy for the programmer to know which
;; character they manipulate because of the higher abstraction.

;; nil refers to the default IDS. IDS content must be escaped (except IDC). I'v
;; found some of possible versions: {G, H, J, K, M, T, U, V, X}. :variants is
;; mapped either to nil (then this entry neither has variants nor is variant of
;; another entry) either to a map referencing variants and their axis.
;(def container)
{"&U+4B3D;" {nil "default IDS."
             :G "?"
             :H "Hangul?"
             :J "Japanese?"
             :K "Korean?"
             :M "Mandarin?"
             :T "Taiwan?"
             :U "?"
             :V "Vietnam?"
             :X "Unknown?"
             :variants {:z ["&CDP+53E31;"]}}}
