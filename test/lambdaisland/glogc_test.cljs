
(ns lambdaisland.glogc-test
  (:require [lambdaisland.glogc :as logc]
            [lambdaisland.glogi :as log]
            [clojure.test :refer [deftest testing is are use-fixtures run-tests join-fixtures]]))

(enable-console-print!)

(deftest smoke-test
  (let [example-val 5]
    (logc/warn :get :these :log :messages)
    (logc/fine :not :you)
    (logc/info :to :show :up :here)
    (is (= example-val (logc/spy example-val)))
    (is (= :spy1 (logc/spy :spy1 example-val)))))

