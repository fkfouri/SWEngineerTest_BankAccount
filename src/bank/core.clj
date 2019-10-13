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


(def available-limit (atom nil))
(def last-transaction (atom nil))


;;verifica se eh uma conta
;;desativado
(defn is-account [json]
  ;verifica se existe no json o atributo 'account'
  (some? (get-in json ["account"])))

;;verifica o tipo de operacao
(defn operationType [json]
  (if (= (some? (get-in json ["account"])) true)
    "account"
    (if (= (some? (get-in json ["transaction"])) true)
      "transaction"
      nil
    )
  )
)


;;verifica se eh uma conta ativa
(defn is-active-account [json]
  ;se o atributo 'activeCard'estiver setado para true, retorna true
  (if (= (get-in json ["account" "activeCard"]) true)
    true
    false))

;;obtem o limite de uma conta ativa
(defn account-limit [json]
  ;somente obtem o limite se for uma conta ativa, do contrario seta como nil
  (if (= (is-active-account json) true)
    (def available-limit  (get-in json ["account" "availableLimit"]))
    (def available-limit nil)))

;;obtem o limite de uma conta ativa
(defn account-limit2 [json]
  ;somente obtem o limite se for uma conta ativa, do contrario seta como nil
  (if (= (is-active-account json) true)
    (get-in json ["account" "availableLimit"])
    nil
  )
)


(defn read1 [& args]
  (renew)
  ;leitura do arquivo json
  (def json (json/read-str (slurp "src/bank/oper1.json"))) 

  ;;(println (get-in json ["account"]))

  ;;verifica se existe account 
  (println " ")
  (println "operation Type:" (operationType json))
  (println "activeCard:" (is-active-account json))

  ;;executa  o limite da conta
  ;(account-limit2 json)
  (def limit (account-limit2 json))
  (def available-limit limit)

  ;;(if (< some? 100) "yes" "no"))

  ;;(def available-limit  (get-in json ["account" "available-limit"]))
  (println "available-limit global:" available-limit)
  (println "available-limit local:" limit)
  (println " ")
  ;;

  )



(defn read2 [& args]
  ;leitura do json 1 + variaveis globais

  (read1)

  ;leitura json 2
  (def json (json/read-str (slurp "src/bank/oper2.json")))

  (println "operation Type:" (operationType json) )

  (case (operationType json)
    "transaction" (validate/validate-transaction json available-limit)
    "account" (println "algo com account")
  )

  ;;
  )



;equivalente o stdin para receber as referencias json
(defn operations [& args]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]

    (println line)
  )
)