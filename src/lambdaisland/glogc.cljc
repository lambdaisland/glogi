(ns lambdaisland.glogc
  "Provide a logging API that you can use anywhere (clj or cljs), by using
  pedestal-log or glogi.

  See https://lambdaisland.com/blog/2020-09-28-logging-in-practice-glogi-pedestal
  for some usage tips.
  "
  #?(:cljs (:require-macros [lambdaisland.glogc]))
  (:require [lambdaisland.glogi :as glogi]
            #?(:clj [io.pedestal.log :as pedestal])))

#?(:clj
   (do

     (defmacro target [& {:keys [cljs clj]}]
       `(if (:ns ~'&env) ~cljs ~clj))

     (defmacro finest [& keyvals] ;; goog.log
       (target :clj  (#'pedestal/log-expr &form :trace keyvals)
               :cljs (#'glogi/log-expr &form :finest keyvals)))

     (defmacro finer [& keyvals] ;; goog.log
       (target :clj  (#'pedestal/log-expr &form :trace keyvals)
               :cljs (#'glogi/log-expr &form :finer keyvals)))

     (defmacro trace [& keyvals]
       (target :clj  (#'pedestal/log-expr &form :trace keyvals)
               :cljs (#'glogi/log-expr &form :trace keyvals)))

     (defmacro fine [& keyvals] ;; goog.log
       (target :clj  (#'pedestal/log-expr &form :debug keyvals)
               :cljs (#'glogi/log-expr &form :fine keyvals)))

     (defmacro debug [& keyvals]
       (target :clj  (#'pedestal/log-expr &form :debug keyvals)
               :cljs (#'glogi/log-expr &form :debug keyvals)))

     (defmacro config [& keyvals] ;; goog.log
       (target :clj  (#'pedestal/log-expr &form :info keyvals)
               :cljs (#'glogi/log-expr &form :config keyvals)))

     (defmacro info [& keyvals]
       (target :clj  (#'pedestal/log-expr &form :info keyvals)
               :cljs (#'glogi/log-expr &form :info keyvals)))

     (defmacro warn [& keyvals]
       (target :clj  (#'pedestal/log-expr &form :warn keyvals)
               :cljs (#'glogi/log-expr &form :warn keyvals)))

     (defmacro error [& keyvals]
       (target :clj  (#'pedestal/log-expr &form :error keyvals)
               :cljs (#'glogi/log-expr &form :error keyvals)))

     (defmacro spy
       ([expr]
        (target :clj `(pedestal/spy ~expr)
                :cljs `(glogi/spy ~expr)))
       ([expr & exprs]
        (target :clj `(pedestal/spy [ ~expr ~@exprs])
                :cljs `(glogi/spy ~expr ~@exprs))))

     (defmacro with-context [ctx-map & body]
       `(pedestal/with-context ~ctx-map ~@body))

     (def format-name pedestal/format-name)
     (def counter pedestal/counter)
     (def gauge pedestal/gauge)
     (def histogram pedestal/histogram)
     (def meter pedestal/meter)))
