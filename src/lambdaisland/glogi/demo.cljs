(ns lambdaisland.glogi.demo
  (:require [lambdaisland.glogi :as glogi]
            [lambdaisland.glogi.console :as console]))

(console/install!)
;; (glogi/enable-console-logging!)

(glogi/warn :msg "oh no!" )

(try
  (throw (js/Error. "oh no!"))
  (catch js/Error e
    (glogi/warn :msg "so far so good"
                :exception e)))
