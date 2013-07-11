(ns parsargs.core
  (:refer-clojure :exclude [sequence some map]))


;; Parse sequence of Clojure data to maps and vectors.

;; A parser is a function that takes a (possibly nested) sequence of Clojure data as input.
;; It returns a pair of the resulting data structure and the input that it didn't consume.
;; If the parser fails it throws an IllegalArgumentException.



(defn value
  "Returns a parser function that returns a pair [first-element remaining-input]
   if first-element fulfills the predicate pred. Otherwise an IllegalArgumentException
   is thrown."
  [pred]
  (fn [[first-element & remaining-input]]
    (if (pred first-element)
      [first-element remaining-input]
      (throw (IllegalArgumentException. (str "Expected '" first-element "' to match " pred))))))


(defn optval
  "Returns a parser function that returns a pair [first-element remaining-input]
  if first element fulfills predicate pred, otherwise the pair
  [default input]. If default is omitted the pair [nil input] is returned."
  ([pred]
     (optval pred nil))
  ([pred default]
  (fn [input]
    (let [first-element (first input)]
      (if (pred first-element) [first-element (rest input)] [default input])))))


(defn omit
  "Returns a parser function that returns a pair [nil remaining-input]
   if the first element fulfills predicate pred. Otherwise an IllegalArgumentException
   is thrown."
  [pred]
  (comp (fn [[parsed-value remaining-input]]
          [nil remaining-input])
        (value pred)))


(defn some
  "Returns a parser function that parses each element of input using the given
   parser function. Stops when parse-fn fails on an input.
   The result is a pair [parsed-results-vector remaining-input]."
  ([parse-fn]
     (some 0 0 parse-fn))
  ([min-count max-count parse-fn]
  (fn [input]
    (loop [result [], xs input]
      (if (or (<= max-count 0) (< (count result) max-count))
        (let [[parsed-result remaining-input] (when-not (empty? xs)
                                                (try (parse-fn xs)
                                                     (catch IllegalArgumentException ex [nil xs])))]
          (if parsed-result
            (recur (conj result parsed-result)
                   remaining-input)
            (if (< (count result) min-count)
              (throw (IllegalArgumentException. (str "Unable to parse at least " min-count " items with " parse-fn)))
              [result xs])))
        [result xs])))))


(defn sequence
  "Returns a parser function that parses each element according to the given
   parser function and associates the parsed value with the key (preceding the
   specified parser function). The result is a pair [parsed-results-map remaining-input].
   Optional tokens that have no default value will be omitted completely."
  [& keys-parse-fn-pairs]
  (fn [input]
    (->> keys-parse-fn-pairs
         (partition 2)
         (reduce (fn [[m xs] [k parse-fn]]
                   (let [[parsed-result remaining-input] (parse-fn xs)]
                     [(if parsed-result (assoc m k parsed-result) m)
                      remaining-input]))
                 [{} input]))))


(defn alternative
  "Returns a parser function that tries each of the given parser-fns on the input.
   It returns when the first parser function succeeds with the result
   [parsed-result remaining-input].
   If no parser function succeeds an IllegalArgumentException is thrown."
  [& parse-fns]
  (fn [input]
    (loop [[parse-fn & fns] parse-fns]
      (if parse-fn
        (if-let [x (try (parse-fn input)
                        (catch IllegalArgumentException ex nil))]
          x
          (recur fns))
        (throw (IllegalArgumentException. (str "Input " input " didn't match any alternative " parse-fns)))))))


(defn map
  "Returns a parser function that parses input using the given parser function
   and applies f to the parsed result. A pair [(f parsed-result) remaining-input]
   is returned. f is a one-arg function."
  [f parser-fn]
  (fn [input]
    (let [[parsed-result remaining-input] (parser-fn input)]
      [(f parsed-result) remaining-input])))


(defn optional
  "Returns a parser that applies the given parser and either returns its result
   or the pair [nil input] if parser-fn fails."
  [parser-fn]
  (fn [input]
    (try (parser-fn input)
         (catch IllegalArgumentException ex [nil input]))))


(defn parse
  "Applies the parser function to the given input. Returns either the
   parsed result or throws an IllegalArgumentException if parsing fails
   or remaining input is not empty."
  [parser-fn input]
  (let [[parsed-result remaining-input] (parser-fn input)]
    (if (empty? remaining-input)
      parsed-result
      (throw (IllegalArgumentException. (str "Input " remaining-input " was not parsed"))))))


(defn descent-with
  "Returns a parser function that applies the given parser-fn to the
   first element of the given input and returns a pair
   [parsed-result remaining-input]."
  [parser-fn]
  (fn [[first-element & remaining-input]]
    [(parse parser-fn first-element) remaining-input]))

