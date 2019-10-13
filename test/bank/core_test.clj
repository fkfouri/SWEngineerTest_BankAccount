(ns bank.core-test
  (:require [clojure.test :refer :all]
            [bank.core :refer :all]
            [bank.validate :refer :all]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.format :as f]         
  )
)


(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))


(deftest validate-transaction-limit-test
  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (def l-time nil)
  (restart)

  (testing "Limite 100"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (testing "teste de fronteira: limite 21"
    (is (= (validate-transaction temp 22 l-time)  "ok")
    )
  )

  (testing "teste de fronteira: limite 20"
    (is (= (validate-transaction temp 21 l-time)  "ok")
    )
  )

  (testing "teste de fronteira: limite 20"
    (is (= (validate-transaction temp 20 l-time)  "ok")
    )
  )

  (testing "teste de fronteira: limite 19"
    (is (= (validate-transaction temp 19 l-time)  "insufficient-limit")
    )
  )

  (testing "limite 10"
    (is (= (validate-transaction temp 10 l-time)  "insufficient-limit")
    )
  )

)
   

(deftest validate-transaction-timelimit-test
  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (def l-time (f/parse "2019-02-13T10:00:00.000Z"))
  (restart)


  (testing "Limite 100"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Nike\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limite 22 mesmo horario"
    (is (= (validate-transaction temp 100 l-time)  "doubled-transaction")
    )
  )

  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Cinemark\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (testing "limite 21 mesmo horario"
    (is (= (validate-transaction temp 100 l-time)  "doubled-transaction")
    )
  )


  (testing "teste de fronteira: limite 20"
    (is (= (validate-transaction temp 20 l-time)  "high-frequency-small-interval")
    )
  )


  (testing "teste de fronteira: limite 19"
    (is (= (validate-transaction temp 19 l-time)  "insufficient-limit")
    )
  )

  (testing "limite 10"
    (is (= (validate-transaction temp 10 l-time)  "insufficient-limit")
    )
  )

)


(deftest validate-transaction-double-transaction
  (def temp (json/read-str "{ \"transaction\": { \"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\" } }"))
  (def l-time (f/parse "2019-02-13T10:00:00.000Z"))
  (restart)


  (testing "primeiro ok"
    (is (= (validate-transaction temp 100 l-time)  "ok")
    )
  )

  (testing "segundo double transaction"
    (is (= (validate-transaction temp 100 l-time)  "doubled-transaction")
    )
  )
)