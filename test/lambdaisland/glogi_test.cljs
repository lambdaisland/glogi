(ns lambdaisland.glogi-test
  (:require [lambdaisland.glogi :as log]
            [clojure.test :refer [deftest testing is are use-fixtures run-tests join-fixtures]]))

(enable-console-print!)

(deftest smoke-test
  (let [captured (atom [])
        handler (fn [record] (swap! captured conj record))
        example-val 5]
    (log/add-handler handler)
    (log/set-levels '{lambdaisland.glogi-test :info})
    (log/warn :get :these :log :messages)
    (log/fine :not :you)
    (log/info :to :show :up :here)
    (log/info :over :ride :line -1)
    (is (= [{:sequenceNumber 0
             :level :warning
             :message {:get :these :log :messages :line 13}
             :logger-name "lambdaisland.glogi-test"
             :exception nil}
            {:sequenceNumber 0
             :level :info
             :message {:to :show :up :here :line 15}
             :logger-name "lambdaisland.glogi-test" :exception nil}
            {:sequenceNumber 0
             :level :info
             :message {:over :ride :line -1}
             :logger-name "lambdaisland.glogi-test" :exception nil}]
           (map #(dissoc % :time) @captured)))
    (log/set-levels '{lambdaisland.glogi-test :debug}) ;Debug covers spy
    (log/spy example-val)
    (log/spy :spy1 example-val)
    (log/remove-handler handler)
    (is (= (log/level-value :warn) 900))))

