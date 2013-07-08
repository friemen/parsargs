# parsargs

Parse convenient to specify function arguments into Clojure
data structures that are easy to process.

[![Build Status](https://travis-ci.org/friemen/parsargs.png?branch=master)](https://travis-ci.org/friemen/parsargs)

## Motivation

When creating an API there is always a tension between ease of its use
and its implementation. Ease-of-use demands a flexible and concise
notation for those who use the API, including specifying function arguments.

The API function implementation demands data structures that it can easily
work with. Although the notation of Clojure data structures is indeed
very light-weight there are times when you want to offer an even
simpler way for specifying arguments. 

As an example let's assume you're creating an API for mappings between
Clojure data and UI components. Obviously you need to specify
at least two things per mapping: the path within the data structure and
the path of the visual components property. So you could start
with a simple map:

```clojure
(def m {:name    ["Name" :text]
        :street  ["Street" :text]
        :zipcode ["Zipcode" :text]
        :city    ["City" :text]})
```
No visual noise, that's fine. But unfortunately that is not enough. 
You'll need to specify a parser and a formatter function to deal with 
dates and numeric data.

The code that implememts the mapping would ideally work on something like this:
```clojure
(def m [{:data-path :name
         :signal-path ["Name" :text]
	     :formatter str
	     :parser identity},
	    ; ... 
	    ; more mapping specifications
	    ; ...
	])
```

But this is a lot of boilerplate to read and write because

 - in most cases formatter and parser would take default values `str` and `identity`.
 - the keywords like `:data-path` visually create more noise than signal.


Here parsargs offers a way to specify how the concise notation is mapped
to an easy-to-work-with data structure.

```clojure
(require [parsargs.core :as p])

(def mapping-parser 
             (p/some
                (p/sequence :data-path (p/alternative
                                        (p/value vector?)
                                        (p/value keyword?))
							:signal-path (p/alternative
                                          (p/value #(and (vector? %) (string? (last %))))
                                          (p/value string?))			
                            :formatter (p/optval fn? str)
                            :parser (p/optval fn? identity))))

(defn mapping [& args]
  (p/parse mapping-parser args))

```

The mapping function is now your factory to create full blown data structures
from a concise notation:

```clojure
(def m (mapping :name    ["Name" :text]    
                :street  ["Street" :text]
                :zipcode ["Zipcode" :text] :parser to-number 
                :city    ["City" :text])) 
```


## Usage

Will be available on Clojars soon.

## License

Copyright 2013 F.Riemenschneider

Distributed under the Eclipse Public License, the same as Clojure.