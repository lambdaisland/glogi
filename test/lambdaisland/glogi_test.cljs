(ns lambdaisland.glogi-test
  (:require [lambdaisland.glogi :as log]
            [clojure.test :refer [deftest testing is are use-fixtures run-tests join-fixtures]]))

(enable-console-print!)

(deftest smoke-test
  (let [captured (atom [])
        handler (fn [record] (swap! captured conj record))]
    (log/add-handler handler)
    (log/set-levels '{lambdaisland.glogi-test :info})
    (log/warn :get :these :log :messages)
    (log/fine :not :you)
    (log/info :to :show :up :here)
    (is (= [{:sequenceNumber 0
             :level :warning
             :message {:get :these :log :messages :line 12}
             :logger-name "lambdaisland.glogi-test"
             :exception nil}
            {:sequenceNumber 0
             :level :info
             :message {:to :show :up :here :line 14}
             :logger-name "lambdaisland.glogi-test" :exception nil}]
           (map #(dissoc % :time) @captured)))
    (log/remove-handler handler)

    (is (= (log/level-value :warn) 900))))
