(ns cryogen-admin-ui-md.core
  (:require [hiccup.page :refer [include-js include-css html5]]
            ))

(def mount-target
  [:div#app
      [:p "please enable javascript"]])


(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.1/css/bulma.min.css")
   (include-js "https://use.fontawesome.com/releases/v5.1.0/js/all.js")
   (include-css "/css/site.css" )
   ])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))





(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
