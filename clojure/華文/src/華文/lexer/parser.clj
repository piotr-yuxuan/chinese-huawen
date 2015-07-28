(ns 華文.lexer.parser)

(def ids "../../data/ids/ids.txt")

(.exists (clojure.java.io/as-file ids))

(def src (line-seq (clojure.java.io/reader ids)))
