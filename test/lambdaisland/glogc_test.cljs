(ns lambdaisland.glogc-test
  (:require [lambdaisland.glogc :as logc]
            [lambdaisland.glogi :as log]
            [clojure.test :refer [deftest testing is are use-fixtures run-tests join-fixtures]]))

(enable-console-print!)

(deftest smoke-test
  (let [captured (atom [])
        handler (fn [record] (swap! captured conj record))
        example-val 5]
    (log/add-handler handler)
    (log/set-levels '{lambdaisland.glogc-test :info})
    (logc/warn :get :these :log :messages)
    (logc/fine :not :you)
    (logc/info :to :show :up :here)
    (is (= example-val (logc/spy example-val)))
    (is (= example-val (logc/spy :spy1 example-val)))
    (is (= [{:sequenceNumber 0
             :level :warning
             :message {:get :these :log :messages :line 14}
             :logger-name "lambdaisland.glogc-test"
             :exception nil}
            {:sequenceNumber 0
             :level :info
             :message {:to :show :up :here :line 16}
             :logger-name "lambdaisland.glogc-test" :exception nil}]
           (map #(dissoc % :time) @captured)))
    (log/remove-handler handler)))

