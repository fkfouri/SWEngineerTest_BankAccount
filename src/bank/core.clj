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


(defn process-input [input]

  (def account-out "")
  (def transaction-out "")
  (def unknown-out "")

  (try
    (do
      (def t-json (json/read-str input))

      ;validacao do tipo de json
      (case (val/operationType? t-json)
        "transaction" (def transaction-out (val/validate-transaction t-json val/account-limit val/account-last-time))
        "account" (def account-out (val/validate-account t-json))
        nil (def unknown-out "invalid-input")
      )
    )
    (catch Exception e
      (def unknown-out "invalid-input")
    )
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

  (if (> (count unknown-out) 0)
    (println (json/write-str {:account {:activeCard val/account-active, :availableLimit val/account-limit} :violations [unknown-out]}))
  )

  (if (= input "stop")
    (throw (Exception. "stop"))
  )
)



;teste de entrada de operacoes
(defn test-operations [& args]
  (renew)
  ;equivalente o stdin para receber as referencias json
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]


    (case line
      "0" (def t-json (json/read-str "{ \"account\": { \"activeCard\": true, \"availableLimit\": 200 } }"))
      "1" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
      "2" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
      "3" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Adidas\", \"amount\": 30, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
      "4" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Nike\", \"amount\": 40, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
      "5" (def t-json (json/read-str "{ \"transaction\": { \"merchant\": \"Gibson\", \"amount\": 15, \"time\": \"2019-02-13T11:00:00.000Z\" } }"))
    )

    
    (process-input t-json)
    ;(println t-json)

    
  


  )
)


(defn operations [& args]
  (renew)
  ;equivalente o stdin para receber as referencias json
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]

    ;parse de string para json


    (process-input line)

  )
)