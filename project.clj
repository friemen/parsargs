(defproject parsargs "1.2.1-SNAPSHOT"
  :description
  "Parsing concise argument sequences to nice data structures."

  :url
  "https://github.com/friemen/parsargs"

  :license
  {:name "Eclipse Public License"
   :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojure "1.12.1"]]

  :plugins
  [[lein-codox "0.10.8"]]

  :codox
  {:language     :clojure
   :source-paths ["src"]
   :namespaces   [#"^parsargs"]
   :source-uri   "https://github.com/friemen/parsargs/blob/master/{filepath}#L{line}"}

  :scm
  {:name "git"
   :url "https://github.com/friemen/parsargs"}

  :repositories
  [["clojars" {:url "https://clojars.org/repo"
               :creds :gpg}]])
