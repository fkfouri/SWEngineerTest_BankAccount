(ns bank.core-test
  (:require [clojure.test :refer :all]
            [bank.core :refer :all]
            [bank.validate :refer :all]
            [clojure.data.json :as json]
  )
)


(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))


(deftest validate-transaction-test
  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))

  (testing "Limite 100"
    (is (= (validate-transaction temp 100)  true)
    )
  )

  (testing "teste de fronteira: limite 21"
    (is (= (validate-transaction temp 21)  true)
    )
  )

  (testing "teste de fronteira: limite 20"
    (is (= (validate-transaction temp 20)  true)
    )
  )

  (testing "teste de fronteira: limite 19"
    (is (= (validate-transaction temp 19)  false)
    )
  )

  (testing "limite 10"
    (is (= (validate-transaction temp 10)  false)
    )
  )

)
    
