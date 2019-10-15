(ns bank.core-test
  (:require [clojure.test :refer :all]
            [bank.core :refer :all]
            [bank.validate :refer :all]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.format :as f]         
  )
)


(deftest validate-transaction-limit-test
  (def l-time nil)
  (start-test)

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limit-test: Limite 100 - 1"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 19, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limit-test: limite 22 - 2"
    (is (= (validate-transaction temp 22 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 18, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limit-test: teste de fronteira 21 - 3 "
    (is (= (validate-transaction temp 21 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 17, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limit-test: teste de fronteira 20"
    (is (= (validate-transaction temp 20 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 21, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limit-test: teste de fronteira 19"
    (is (= (validate-transaction temp 19 l-time)  "insufficient-limit")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 22, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limit-test: limite 10"
    (is (= (validate-transaction temp 10 l-time)  "insufficient-limit")
    )
  )

)
   

(deftest validate-transaction-frequency-test
  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (def l-time (f/parse "2019-02-13T10:00:00.000Z"))
  (start-test)

  (testing "frequency-test: Limite 100 - 1"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Nike\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "frequency-test: limite 22 mesmo horario - 2"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Cinemark\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "frequency-test: limite 21 mesmo horario - 3"
    (is (= (validate-transaction temp 100 l-time)  "high-frequency-small-interval")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Fender\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "frequency-test: limite 20 mesmo horario - 4"
    (is (= (validate-transaction temp 20 l-time)  "high-frequency-small-interval")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Gibson\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "lfrequency-test: limite 20 mesmo horario - 5"
    (is (= (validate-transaction temp 20 l-time)  "high-frequency-small-interval")
    )
  )


  (testing "frequency-test: teste de fronteira: limite 19 - 6"
    (is (= (validate-transaction temp 19 l-time)  "insufficient-limit")
    )
  )

  (testing "frequency-test: limite 10 - 7"
    (is (= (validate-transaction temp 10 l-time)  "insufficient-limit")
    )
  )

)


(deftest validate-transaction-double-transaction
  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (def l-time (f/parse "2019-02-13T10:00:00.000Z"))
  (start-test)


  (testing "double-transaction: primeiro ok"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (testing "double-transaction: segundo double transaction"
    (is (= (validate-transaction temp 100 l-time)  "doubled-transaction")
    )
  )
)