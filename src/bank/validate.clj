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


;;verifica o tipo de operacao
(defn operationType? [json]
    ;some? retorna true se x nao for nil
    (if (= (some? (get-in json ["account"])) true)
        "account"
        (if (= (some? (get-in json ["transaction"])) true)
            "transaction"
            nil
        )
    )
  )

;;verifica se eh uma conta ativa
(defn is-active-account? [json]
    ;se o atributo 'activeCard'estiver setado para true, retorna true
    (if (= (get-in json ["account" "activeCard"]) true)
      true
      false)
)


;;obtem o limite de uma conta ativa
(defn account-limit? [json]
    ;somente obtem o limite se for uma conta ativa, do contrario seta como nil
    (if (= (is-active-account? json) true)
        ;leitura do atributo availableLimit em account
        (get-in json ["account" "availableLimit"])
        nil
    )
)

;;;obtem o limite de uma conta ativa
;(defn account-limit? [json]
;  ;somente obtem o limite se for uma conta ativa, do contrario seta como nil
;  (if (= (is-active-account? json) true)
;    (def available-limit  (get-in json ["account" "availableLimit"]))
;    (def available-limit nil)))


(def freq 0)
(def last-merchant nil)
(def last-amount nil)
(def account-limit 0)
(def account-active false)
(def account-last-time nil)

;;set contexto para teste
(defn start-test []
    (def freq 0)
    (def last-merchant nil)
    (def last-amount nil)
    (def account-limit 0)
    (def account-last-time nil)
    (def account-active true)
)

;; calcula o delta entre duas datas
(defn delta-time [d1 d2]
    (if (t/before?  d1 d2)
        (t/in-minutes (t/interval d1 d2))
        (t/in-minutes (t/interval d2 d1))
    )
)


(defn validate-transaction [json available-limit last-time]
    ;define se a conta esta ativa
    (def account-active-accepted account-active)

    ;leitura e validacao do valor da transacao
    (def t-amount (get-in json ["transaction" "amount"]))
    (def limit-accepted (>= available-limit t-amount))
    ;(println "limit-accepted: " limit-accepted)
    ;(println " ")

    ;leitura e validacao do horario da transacao
    (def t-time (f/parse (get-in json ["transaction" "time"]))) ;transaction-time


    ;(println "freq==================>" freq )
    ;(println "nil?" (nil? last-time) )
    ;(println "last-time" last-time ) 
    ;(println "t-time" t-time ) 

    ;(if (nil? last-time)
    ;    (def time-accepted true)
    ;    (if (and (> (t/in-minutes (t/interval last-time t-time)) 2)  (< freq 3))
    ;        (def time-accepted true)
    ;        (def time-accepted false)
    ;    )
    ;)

    ;define o valor de account-last-time para ser usado pela aplicacao
    (if (or (nil? last-time) (t/after? t-time last-time ))
        (def account-last-time t-time)
    )

    ;(if (t/after? t-time last-time )
    ;    (def freq 0) ;zera frequencia
    ;)


    ;reinicia a contagem na janela de 2 minutos
    (if (nil? last-time)
        (def freq 0) ;incremento da frequencia
        (if (< (t/in-minutes (t/interval last-time t-time)) 2)
            (def freq (+ freq 1)) ;incremento da frequencia
            (def freq 0) ;zera frequencia
        )
    )

    (if (< freq 3)
        (def time-accepted true)
        (def time-accepted false)
    )

    ;(println "newfreq==================>" freq )
    ;(println "newfreq==================>" (t/in-minutes (t/interval account-last-time t-time)) )
    ;(if (and (some? last-time) (> (t/in-minutes (t/interval last-time t-time)) 2) )
    ;    (def freq 0) ;zera frequencia
    ;    (def freq (+ freq 1)) ;incremento da frequencia
    ;)

    ;leitura e validacao de transacao similar
    (def t-merchant (get-in json ["transaction" "merchant"]))
    (if (nil? last-merchant)
        (def transaction-accepted true)
        (if (and (= t-merchant last-merchant) (= t-amount last-amount))
            (def transaction-accepted false)
            (def transaction-accepted true)
        )
    )
    
    ;(println "t-merchant" t-merchant "  last-merchant" last-merchant transaction-accepted)
    ;(println "t-amount" t-amount "  last-amount" last-amount) 

    ;atualiza a ultima compra
    (def last-merchant t-merchant)
    (def last-amount t-amount)

    (cond
        (= account-active-accepted false) "card-not-active"    
        (= limit-accepted false) "insufficient-limit"
        (= transaction-accepted false) "doubled-transaction"        
        (= time-accepted false) "high-frequency-small-interval"
        :else "ok"
    )

 

)


;;valida se um input json eh uma transacao valida
(defn validate-account [json]
    (println " ")
    (println "operation Type:" (operationType? json))
    
    ;define se a conta esta ativa
    (def account-active (is-active-account? json))
    (println "account-active: AQUI" account-active)

    ;;define  o limite da conta
    (def account-limit (account-limit? json))
  
  
    ;;(if (< some? 100) "yes" "no"))
  
    ;;(def account-limit  (get-in json ["account" "account-limit"]))
    (println "account-limit:" account-limit)
    (println " ")

) 
