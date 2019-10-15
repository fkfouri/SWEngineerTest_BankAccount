(ns bank.core-integration-test
    (:require [clojure.test :refer :all]
              [bank.core :refer :all]
              [bank.validate :refer :all]
              [clojure.data.json :as json]
              [clj-time.core :as t]
              [clj-time.format :as f]         
    )
)


(def my-test-cases [
    "{ \"account\": { \"activeCard\": true, \"availableLimit\": 200 } }"
    "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Adidas\", \"amount\": 100, \"time\": \"2019-02-13T11:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Nike\", \"amount\": 40, \"time\": \"2019-02-13T11:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Gibson\", \"amount\": 15, \"time\": \"2019-02-13T11:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 30, \"time\": \"2019-02-13T10:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Cinemark\", \"amount\": 10, \"time\": \"2019-02-13T11:00:00.000Z\" } }"
    "{ \"transaction\": { \"merchant\": \"Cinemark\", \"amount\": 10, \"time\": \"2019-02-13T11:03:00.000Z\" } }"]
)


;inicia um conta e faz 3 ou mais compra em espaco de tempo maior que dois minutos
(deftest test-high-frequency-small-interval-great-2-minutes
    (testing "Test high-frequency-small-interval" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (json/read-str (process-input (nth my-test-cases 2))) ;compra de 90 no Habbib's as 11hs
        (json/read-str (process-input (nth my-test-cases 4))) ;compra de 40 na Nike as 11hs
        (json/read-str (process-input (nth my-test-cases 5))) ;compra de 15 na Gibson as 11hs
        (def result (json/read-str (process-input (nth my-test-cases 8)))) ;compra de 10 no Cinemark as 11:03hs
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 45 (get-in result ["account" "availableLimit"])))
        (is (= [""] (get-in result ["violations"]))) 
    )
)


;inicia um conta e faz 3 ou mais compra em espaco de tempo menor que dois minutos
(deftest test-high-frequency-small-interval
    (testing "Test high-frequency-small-interval" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (json/read-str (process-input (nth my-test-cases 2))) ;compra de 90 no Habbib's as 11hs
        (json/read-str (process-input (nth my-test-cases 4))) ;compra de 40 na Nike as 11hs
        (json/read-str (process-input (nth my-test-cases 5))) ;compra de 15 na Gibson as 11hs
        (def result (json/read-str (process-input (nth my-test-cases 7)))) ;compra de 10 no Cinemark as 11hs
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 55 (get-in result ["account" "availableLimit"])))
        (is (= ["high-frequency-small-interval"] (get-in result ["violations"]))) 
    )
)



 ;teste de inclusao de uma transacao sem uma conta ativa  
(deftest test-card-not-active
    (testing "Test card-not-active" 
        (reset)
        (def result (json/read-str (process-input (nth my-test-cases 1))))
        (is (= false (get-in result ["account" "activeCard"]))) 
        (is (= 0 (get-in result ["account" "availableLimit"])))
        (is (= ["card-not-active"] (get-in result ["violations"]))) 
    )
)

;teste de inclusao de uma de deixar uma uma conta ativa  
(deftest test-active-card
    (testing "Test test-active-card" 
        (reset)
        (def result (json/read-str (process-input (nth my-test-cases 0))))
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 200 (get-in result ["account" "availableLimit"])))
        (is (= [""] (get-in result ["violations"]))) 
    )
)

 ;inicia um conta e faz uma compra deixando um limite de 180 na conta
 (deftest test-create-account
    (testing "Test test-create-account" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (def result (json/read-str (process-input (nth my-test-cases 1))))
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 180 (get-in result ["account" "availableLimit"])))
        (is (= [""] (get-in result ["violations"]))) 
    )
)

 ;inicia um conta e faz uma as 11hs e em seguida uma as 10h do mesmo dia, gerando erro de cronologia
 (deftest test-chronology-error
    (testing "Test test-chronology-error" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (json/read-str (process-input (nth my-test-cases 2))) ;uma compra as 11hs
        (def result (json/read-str (process-input (nth my-test-cases 1)))) ;uma compra as 10hs
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 110 (get-in result ["account" "availableLimit"])))
        (is (= ["chronology-error"] (get-in result ["violations"]))) 
    )
)

 ;inicia um conta e faz duas vezes a mesma compra no mesmo merchant, no mesmo horario e com o mesmo valor
 (deftest test-doubled-transaction
    (testing "Test test-doubled-transaction" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (json/read-str (process-input (nth my-test-cases 1))) ;compra de 20 no Burger King as 10hs
        (def result (json/read-str (process-input (nth my-test-cases 1)))) ;a mesma compra de 20 no Burger King as 10hs
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 180 (get-in result ["account" "availableLimit"])))
        (is (= ["doubled-transaction"] (get-in result ["violations"]))) 
    )
)

;inicia um conta e faz duas compras no mesmo merchant, no mesmo horario e com valores diferentes
(deftest test-two-purchase-same-merchant
    (testing "Test test-two-purchase-same-merchant" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (json/read-str (process-input (nth my-test-cases 6))) ;compra de 20 no Burger King as 10hs
        (def result (json/read-str (process-input (nth my-test-cases 1)))) ;a mesma compra de 30 no Burger King as 10hs
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 150 (get-in result ["account" "availableLimit"])))
        (is (= [""] (get-in result ["violations"]))) 
    )
)

;inicia um conta e faz duas compras consumindo o limite, e tendo a 3 compra negada por limite insuficiente
(deftest test-insufficient-limit
    (testing "Test test-insufficient-limit" 
        (reset)
        (json/read-str (process-input (nth my-test-cases 0))) ;inicia a conta
        (json/read-str (process-input (nth my-test-cases 1))) ;compra de 20 no Burger King as 10hs
        (json/read-str (process-input (nth my-test-cases 2))) ;compra de 90 no Habbib as 11hs
        (def result (json/read-str (process-input (nth my-test-cases 3)))) ;compra de 100 na Adidas as 11hs
        (is (= true (get-in result ["account" "activeCard"]))) 
        (is (= 90 (get-in result ["account" "availableLimit"])))
        (is (= ["insufficient-limit"] (get-in result ["violations"]))) 
    )
)



