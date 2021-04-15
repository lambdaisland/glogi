(ns walkthrough
  (:require [lambdaisland.glogi :as log]))

(def captured (atom []))

(log/add-handler (fn [record] (swap! captured conj record)))

(log/debug :test :ing)

(log/set-levels '{:glogi/root :off
                  walkthrough :debug})

@captured
