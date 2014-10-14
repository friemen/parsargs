(defproject parsargs "1.2.0"
  :description "Parsing concise argument sequences to nice data structures."
  :url "https://github.com/friemen/parsargs"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[codox "0.8.10"]]
  :codox {:defaults {}
          :sources ["src"]
          :exclude []
          :src-dir-uri "https://github.com/friemen/parsargs/blob/master/"
          :src-linenum-anchor-prefix "L"}
  :scm {:name "git"
        :url "https://github.com/friemen/parsargs"}
  :repositories [["clojars" {:url "https://clojars.org/repo"
                             :creds :gpg}]])
