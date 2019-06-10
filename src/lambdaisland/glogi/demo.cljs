(ns lambdaisland.glogi.demo
  (:require [lambdaisland.glogi :as glogi]))

(glogi/enable-console-logging!)
(glogi/add-handler (comp js/console.log pr-str))

(glogi/warn :msg "oh no!")

(try
  (throw (js/Error. "oh no!"))
  (catch js/Error e
    (glogi/warn :msg "so far so good"
                :exception e
                ::glogi/formatter (comp pr-str seq)
                ::glogi/logger "foo.bar")
    ))
