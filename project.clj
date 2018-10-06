(defproject cryogen-admin-ui-md "0.1.0-SNAPSHOT"
  :description "A Clojure library to be used with cryogen static site generator\nThis could be used to create and edit posts or pages with a simple ui instead of directly editing the files"
  :url "https://github.com/oneto018/-cryogen-admin-ui-md"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                  [hiccup "1.0.5"]
                  [cryogen-markdown "0.1.7"]
                  [cryogen-core "0.1.61"]
                  [ring/ring-devel "1.6.3"]
	              [compojure "1.6.0"]
	              [ring-server "0.5.0"]
	              [ring/ring-json "0.4.0"]
               	  [cheshire "5.8.1"]
                  [ring-cors "0.1.12"]
                  ])
