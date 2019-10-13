(ns bank.core
  (:gen-class))

(require '[net.cgrand.enlive-html :as enlive])
(require '[clojure.string :as s])
;; let's require ourselves a pretty printing function - pprint
;; we'll be looking at a lot of data, might get messy
(require '[clojure.pprint :refer [pprint]])
(require '[clojure.data.json :as json])
(require '[clojure.java.io :as io])
(def availableLimit (atom nil))

(defn renew  [& args]
  (require '[bank.core :reload :all]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (renew)
  (println "Hello, World again! (require '[bank.core :reload :all])"))


;;verifica se eh uma conta
(defn is-account [json]
  (some? (get-in json ["account"])))


;;verifica se eh uma conta ativa
(defn is-account-active [json]
  (if (= (get-in json ["account" "activeCard"]) true)
    true
    false))

;;obtem o limite de uma conta ativa
(defn account-limit [json]
  (if (= (is-account-active json) true)
    (def availableLimit  (get-in json ["account" "availableLimit"]))
    (def availableLimit nil)))

(defn read1 [& args]
  (renew)
  ;leitura do arquivo json
  (def json (json/read-str (slurp "src/bank/oper1.json"))) 

  ;;(println (get-in json ["account"]))

  ;;verifica se existe account 
  (println "is-account:" (is-account json))

  ;;exibe o limite da conta
  (println "account-limit:" (account-limit json))


  ;;(if (< some? 100) "yes" "no"))

  ;;(def availableLimit  (get-in json ["account" "availableLimit"]))
  (println "availableLimit:" availableLimit)
  ;;
  )




;;obtem o limite de uma conta ativa
(defn account-limit [json]
  (if (= (is-account-active json) true)
    (def availableLimit  (get-in json ["account" "availableLimit"]))
    (def availableLimit nil)))

(defn read2 [& args]
  (read1)
  (def json (json/read-str (slurp "src/bank/oper2.json")))
  (println (get-in json ["transaction"]))
  (def amount (get-in json ["transaction" "amount"]))
  (println "new availableLimit:" (- availableLimit amount))

  ;;
  )



;equivalente o stdin para receber as referencias json
(defn operations [& args]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]

    (println line)
  )
)