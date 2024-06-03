(defproject parsargs "1.2.1-SNAPSHOT"
  :description
  "Parsing concise argument sequences to nice data structures."

  :url
  "https://github.com/friemen/parsargs"

  :license
  {:name "Eclipse Public License"
   :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojure "1.10.3"]]

  :plugins
  [[lein-codox "0.10.8"]]

  :codox
  {:defaults {}
   :sources ["src"]
   :exclude []
   :src-dir-uri "https://github.com/friemen/parsargs/blob/master/"
   :src-linenum-anchor-prefix "L"}

  :scm
  {:name "git"
   :url "https://github.com/friemen/parsargs"}

  :repositories
  [["clojars" {:url "https://clojars.org/repo"
               :creds :gpg}]])
