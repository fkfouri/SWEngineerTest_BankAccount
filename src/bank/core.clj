(ns bank.core
  (:gen-class
  )
)

(require '[net.cgrand.enlive-html :as enlive])
(require '[clojure.string :as s])
;; let's require ourselves a pretty printing function - pprint
;; we'll be looking at a lot of data, might get messy
(require '[clojure.pprint :refer [pprint]])
(require '[clojure.data.json :as json])
(require '[clojure.java.io :as io])
(require '[clj-time.core :as t])
(require '[clj-time.format :as f])
(require '[bank.validate :as validate])
(import java.util.Date)
(import java.text.SimpleDateFormat)



(defn renew  [& args]
  (require '[bank.core :reload :all])
  (require '[bank.validate :reload :all])
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (renew)
  (println "Hello, World again! (require '[bank.core :reload :all])"))



;;verifica se eh uma conta
;;desativado
(defn is-account [json]
  ;verifica se existe no json o atributo 'account'
  (some? (get-in json ["account"])))










(defn read1 [& args]
  (renew)
  ;leitura do arquivo json
  (def json (json/read-str (slurp "src/bank/oper1.json"))) 

  ;;(println (get-in json ["account"]))

  ;;verifica se existe account 
  (println " ")
  (println "operation Type:" (validate/operationType? json))
  (println "activeCard:" (validate/is-active-account? json))

  ;;define  o limite da conta
  (def available-limit (validate/account-limit? json))


  ;;(if (< some? 100) "yes" "no"))

  ;;(def available-limit  (get-in json ["account" "available-limit"]))
  (println "available-limit:" available-limit)
  (println " ")
  ;;

  )


(def available-limit (atom nil))


(defn read2 [& args]
  ;leitura do json 1 + variaveis globais

  (read1)

  (def last-time (t/now))
  ;leitura json 2
  (def json (json/read-str (slurp "src/bank/oper2.json")))

  (println "operation Type:" (validate/operationType? json) )

  (case (validate/operationType? json)
    "transaction" (validate/validate-transaction json available-limit last-time)
    "account" (println "algo com account")
  )

  ;;
  )



;equivalente o stdin para receber as referencias json
(defn operations [& args]
  (renew)
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]


    (case line
      "1" (def t-json (json/read-str "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }"))
      "2" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
      "3" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
    )

    ;parse de string para json
    ;(def t-json (json/read-str temp))

    ;validacao do tipo de json
    (case (validate/operationType? t-json)
      "transaction" (validate/validate-transaction json available-limit last-time)
      "account" (validate/validate-account t-json)
    )
      

    (println t-json)


  )
)