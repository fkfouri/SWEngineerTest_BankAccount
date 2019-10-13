(ns bank.validate
    (:require
        [clojure.string :as s]
        [clojure.pprint :refer [pprint]]
        [clojure.data.json :as json]
        [clj-time.core :as t]
        [clj-time.format :as f]
    )
)
(import java.util.Date)
(import java.text.SimpleDateFormat)

(defn validate-transaction [json available-limit]
    (def amount (get-in json ["transaction" "amount"]))
    ;(def t-time 
    ;    (.parse
    ;        (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") (get-in json ["transaction" "time"])
    ;    )
    ;)

    ;(def custom-formatter (f/formatters :basic-date-time))
    (def t-time (f/parse (get-in json ["transaction" "time"])))


    ;(if (= (> available-limit amount) true)
    ;  (println "new available-limit:" (- available-limit amount) "at" last-time)
    ;  (println "transactio not valid" "at" last-time)
    ;)
    
    ;(if (= (>= available-limit amount) true)
    ;  true
    ;  false
    ;)

    ;(let [start-time (t/now)])
    (println t-time    )
    ;(def now (java.util.Date.))
    (def now (t/now))
    (println now )
    (println "minutes:" (t/in-minutes (t/interval t-time now) ))
    (println "days" (t/in-days (t/interval t-time now) ))
    ;(println "days" (t/in-days (t/interval now t-time) ))

 
;(.format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") (java.util.Date.)) ;; result: "08/19/2017")
    ;(println (t/in-millis (t/interval t-time (t/now))))
;(println .toString t-time)
)


      