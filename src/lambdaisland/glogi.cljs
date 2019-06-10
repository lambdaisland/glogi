(ns lambdaisland.glogi
  (:require [goog.log :as glog]
            [goog.debug.Logger :as Logger]
            [goog.debug.Logger.Level :as Level]
            [goog.debug.Console :as Console]
            [clojure.string :as str])
  (:import [goog.debug Console FancyWindow DivConsole LogRecord])
  (:require-macros [lambdaisland.glogi]))

(defn logger
  "Get a logger by name, and optionally set its level."
  ([name]
   (glog/getLogger name))
  ([name level]
   (glog/getLogger name level)))

(def root-logger (logger ""))

(def level
  {:off     Level/OFF
   :shout   Level/SHOUT
   :severe  Level/SEVERE
   :warning Level/WARNING
   :info    Level/INFO
   :config  Level/CONFIG
   :fine    Level/FINE
   :finer   Level/FINER
   :finest  Level/FINEST
   :all     Level/ALL

   ;; pedestal style
   :trace Level/FINE
   :debug Level/CONFIG
   :warn  Level/WARNING
   :error Level/SEVERE})

(defn log
  "Output a log message to the given logger, optionally with an exception to be
  logged."
  ([name lvl message]
   (log name lvl message nil))
  ([name lvl message exception]
   (glog/log (logger name) (level lvl) message exception)))

(defn set-level
  "Set the level (a keyword) of the given logger, identified by name."
  [name lvl]
  (assert (contains? level lvl))
  (.setLevel (logger name) (level lvl)))

(defn enable-console-logging!
  "Log to the browser console"
  []
  (.setCapturing (Console.) true))

(defn console-autoinstall!
  "Log to the browser console if the browser location contains Debug=true"
  []
  (Console/autoInstall))

(defn popup-logger-window!
  "Pop up a browser window which will display log messages."
  []
  (.setEnabled (FancyWindow.) true))

(defn log-to-div!
  "Log messages to an element on the page."
  [element]
  (.setCapturing (DivConsole. element) true))

(defn add-handler
  "Add a log handler to the given logger, or to the root logger if no logger is
  specified. The handler is a function which receives a map as its argument."
  ([handler-fn]
   (add-handler "" handler-fn))
  ([name handler-fn]
   (.addHandler (logger name)
                (fn [^LogRecord record]
                  (handler-fn {:sequenceNumber (.-sequenceNumber_ record)
                               :time           (.-time_ record)
                               :level          (keyword (str/lower-case (.-name (.-level_ record))))
                               :message        (.-msg_ record)
                               :logger-name    (.-loggerName_ record)
                               :exception      (.-exception_ record)})))))

(glog/warning (glog/getLogger "lambdaisland.glogi") "oh no!")
