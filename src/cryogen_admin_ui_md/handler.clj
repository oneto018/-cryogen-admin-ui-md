(ns cryogen-admin-ui-md.handler
  (:require [compojure.core :refer [GET defroutes PUT]]
            [compojure.core :as cc]
            [compojure.route :as route]
            [cryogen-core.compiler :refer [compile-assets-timed read-config find-posts find-pages process-config page-content read-page-meta]]
            [cryogen-core.io :refer [path]]
            [cryogen-core.markup :as m]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [ring.middleware.cors :refer [wrap-cors]]
            [cryogen-admin-ui-md.core :as admin]
            [clojure.string :as st]
            ))


;for handling development via another cljs project
(defn handle-cors [my-routes]
  (wrap-cors my-routes :access-control-allow-origin [#"http://localhost:3449"]
                       :access-control-allow-methods [:get :put :post :delete]
                       :access-control-allow-credentials "true"))

;too lazy to write a middleware
(defn json-res [x]
  {:body (json/generate-string x)
   :content-type "application/json"})

(defn pprint-to-str [x]
  (let [out (java.io.StringWriter.)]
    (clojure.pprint/pprint x out)
    (.toString out)))

(defn get-env-params []
  {:mu (first (m/markups))
   :config (process-config (read-config))})

(defn file-entry [fl]
   {:name (.getName fl) :path (.getPath fl)})

(defn get-location [files]
  (-> (first files)
      (.getParent)))


(defn list-entities[find-fn]
  (let [{:keys [mu config]} (get-env-params)
        items (find-fn config mu)
        location (get-location items)]
   (json-res {:items (map file-entry items) :location location})))


(defn write-item [loc data]
  (println "writing data " data)
  (let [head (update 
               (dissoc data :content)
               :layout keyword)
        content (clojure.string/trim (get data :content))
        head-str (pprint-to-str head)
       
        ]
    (spit  loc (str head-str content))))


(defn page-content-raw
  "Returns a map with the given page's file-name, metadata and content parsed from
  the file with the given markup."
  [^java.io.File page config markup]
  (with-open [rdr (java.io.PushbackReader. (io/reader page))]
    (let [re-root   (re-pattern (str "^.*?(" (:page-root config) "|" (:post-root config) ")/"))
          page-fwd  (clojure.string/replace (str page) "\\" "/")    ;; make it work on Windows
          page-name (if (:collapse-subdirs? config) (.getName page) (clojure.string/replace page-fwd re-root ""))
         
          page-meta (read-page-meta page-name rdr)
          content    (->> (java.io.BufferedReader. rdr)
                          (line-seq)
                          (clojure.string/join "\n"))]
     (merge 
        {:file-name page-name :content content} 
        page-meta))))


(defn content-of [loc]
  (println "for location" loc)
  (let [{:keys [mu config]} (get-env-params)
        v (page-content-raw  (io/file loc) config mu)]
    v))


(defn add-routes [old-handler]
  (cc/routes 
    (GET "/admin" []  (admin/loading-page))
    (GET "/admin/posts" [] (list-entities find-posts))
    (GET "/admin/pages" [] (list-entities find-pages))
    (GET "/admin/item/:loc" [loc] (json-res  (content-of loc)))
    (PUT "/admin/item/:loc" [loc :as req] (let [datax (get-in req [:body :data])]
                                            (write-item loc datax)
                                           (json-res {:status true})))
    old-handler 
    ))



(def test-routes 
  
   (cc/routes 
   
    (GET "/admin" []  (admin/loading-page))
    (GET "/admin/posts" [] (list-entities find-posts))
    (GET "/admin/pages" [] (list-entities find-pages))
    (GET "/admin/item/:loc" [loc] (json-res  (content-of loc)))
    (PUT "/admin/item/:loc" [loc :as req] (let [datax (get-in req [:body :data])]
                                            (write-item loc datax)
                                           (json-res {:status true})))
     (route/resources "/") 
    ))

(defn current-path []
  (.getAbsolutePath (io/file "./")))

;;quick dirty parser for project.clj
(defn get-project []
  (apply hash-map 
      (drop 3 (-> (slurp "./project.clj")
      (read-string)))))

(println "current project ring is -->> " (get-in (get-project) [:ring :handler]))


(defn get-fn-dyn [ns-name f-name]
  (require (symbol ns-name))
  (let [bar (find-ns (symbol ns-name))]
    (ns-resolve bar (symbol f-name))))

(defn get-old-handlers []
  (let [old-handler-str (str (get-in (get-project) [:ring :handler]))
        [nsn fname] (st/split old-handler-str #"\/")
        
        old-handler-var (get-fn-dyn nsn fname)]
    (println "parts" [nsn fname] "old-handler" old-handler-var)
    (var-get old-handler-var)))

(def handler-with-admin  (-> (wrap-json-response (add-routes (get-old-handlers)))
                            (wrap-json-body {:keywords? true :bigdecimals? true})
                            (handle-cors)))