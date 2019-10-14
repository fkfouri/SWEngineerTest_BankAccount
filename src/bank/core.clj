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
(require '[bank.validate :as val])
(import java.util.Date)
(import java.text.SimpleDateFormat)



(defn renew  [& args]
  (require '[bank.validate :reload :all])
  (require '[bank.core :reload :all])
)


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
  (println "operation Type:" (val/operationType? json))
  (println "activeCard:" (val/is-active-account? json))

  ;;define  o limite da conta
  (def available-limit (val/account-limit? json))


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

  (println "operation Type:" (val/operationType? json) )

  (case (val/operationType? json)
    "transaction" (val/validate-transaction json available-limit last-time)
    "account" (println "algo com account")
  )

  ;;
  )



;equivalente o stdin para receber as referencias json
(defn operations [& args]
  (renew)
  (val/reset)
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]


    (case line
      "0" (def t-json (json/read-str "{ \"account\": { \"activeCard\": true, \"availableLimit\": 200 } }"))
      "1" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
      "2" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
      "3" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Adidas\", \"amount\": 30, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
      "4" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Nike\", \"amount\": 40, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
      "5" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Gibson\", \"amount\": 15, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
    )

    ;parse de string para json
    ;(def t-json (json/read-str temp))

    ;(println t-json)

    (def account-out "")
    (def transaction-out "")

    ;validacao do tipo de json
    (case (val/operationType? t-json)
      "transaction" (def transaction-out (val/validate-transaction t-json val/account-limit val/account-last-time))
      "account" (def account-out (val/validate-account t-json))
    )

    ;formata saida de account
    (if (> (count account-out) 1)
      (do
        (if (= account-out "ok")
          (def account-out "") ;limpa saida para ok
        )
        (println (json/write-str {:account {:activeCard val/account-active, :availableLimit val/account-limit} :violations [account-out]}))
      )      
    )

    ;formata saida de transaction
    (if (> (count transaction-out) 0)
      (do
        ;debita o valor se transacao OK
        (if (= transaction-out "ok")
          (do
            (val/debit val/t-amount) ;debita valor 
            (def transaction-out "") ;limpa saida para ok
          )
        )
        (println (json/write-str {:account {:activeCard val/account-active, :availableLimit val/account-limit} :violations [transaction-out]}))
      )
    )
  


  )
)