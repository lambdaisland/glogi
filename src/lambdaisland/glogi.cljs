(ns lambdaisland.glogi
  (:require [goog.log :as glog]
            [goog.debug.Console :as Console]
            [goog.array :as array]
            [clojure.string :as str]
            [goog.object :as gobj])
  (:import [goog.debug Console FancyWindow DivConsole])
  (:require-macros [lambdaisland.glogi]))

(def Level
  (if (exists? glog/Level)
    glog/Level
    goog.debug.logger.Level))

;; Wrappers around goog.log methods which changed in Closure v20210302, so we
;; can retain backward compatibility. The static method call is the newer
;; version.

(defn- goog-setLevel [logger level]
  (if (exists? glog/setLevel)
    (^:cljs.analyzer/no-resolve glog/setLevel logger level)
    (.setLevel logger level)))

(defn- goog-logRecord [logger record]
  (if (exists? glog/publishLogRecord)
    (^:cljs.analyzer/no-resolve glog/publishLogRecord logger record)
    (.logRecord logger record)))

(defn- goog-addHandler [logger handler]
  (if (exists? glog/addHandler)
    (glog/addHandler logger handler)
    (.addHandler logger handler)))

(defn- goog-removeHandler [logger handler]
  (if (exists? glog/removeHandler)
    (glog/removeHandler logger handler)
    (.removeHandler logger handler)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^private logger-handlers-prop "__glogi_handlers__")

(defn name-str [x]
  (cond
    (= :glogi/root x)
    ""

    (string? x)
    x

    (simple-ident? x)
    (name x)

    (qualified-ident? x)
    (str (namespace x) "/" (name x))

    :else
    (str x)))

(defn logger
  "Get a logger by name, and optionally set its level. Name can be a string
  keyword, or symbol. The special keyword :glogi/root returns the root logger."
  ([n]
   (glog/getLogger (name-str n) js/undefined))
  ([n level]
   (glog/getLogger (name-str n) level)))

(def root-logger (logger ""))

(defn- predefined-level [name]
  (when (exists? glog/Level)
    (glog/Level.getPredefinedLevel name)))

(def levels
  ;; Hacky way to make this work across versions. (.-OFF Level) does not work in
  ;; the new version after advanced compilation, but they have added a lookup
  ;; function.
  {:off     (or (.-OFF Level) (predefined-level "OFF"))
   :shout   (or (.-SHOUT Level) (predefined-level "SHOUT"))
   :severe  (or (.-SEVERE Level) (predefined-level "SEVERE"))
   :warning (or (.-WARNING Level) (predefined-level "WARNING"))
   :info    (or (.-INFO Level) (predefined-level "INFO"))
   :config  (or (.-CONFIG Level) (predefined-level "CONFIG"))
   :fine    (or (.-FINE Level) (predefined-level "FINE"))
   :finer   (or (.-FINER Level) (predefined-level "FINER"))
   :finest  (or (.-FINEST Level) (predefined-level "FINEST"))
   :all     (or (.-ALL Level) (predefined-level "ALL"))

   ;; pedestal style
   :trace (or (.-FINER Level) (predefined-level "FINER"))
   :debug (or (.-FINE Level) (predefined-level "FINE"))
   :warn  (or (.-WARNING Level) (predefined-level "WARNING"))
   :error (or (.-SEVERE Level) (predefined-level "SEVERE"))})

(defn level [lvl]
  (get levels lvl))

(defn level-value
  "Get the numeric value of a log level (keyword)."
  [lvl]
  (.-value (level lvl)))

(defn make-log-record [level message name exception]
  (let [LogRecord (if (exists? goog.debug.LogRecord)
                    goog.debug.LogRecord
                    goog.log.LogRecord)
        record (new LogRecord level message name)]
    (when exception
      (.setException record exception))
    record))

(defn log
  "Output a log message to the given logger, optionally with an exception to be
  logged."
  ([name lvl message]
   (log name lvl message nil))
  ([name lvl message exception]
   (when glog/ENABLED
     (when-let [l (logger name)]
       (goog-logRecord l (make-log-record (level lvl) message name exception))))))

(defn set-level
  "Set the level (a keyword) of the given logger, identified by name."
  [name lvl]
  (assert (contains? levels lvl))
  (some-> (logger name) (goog-setLevel (level lvl))))

(defn ^:export set-levels
  "Convenience function for setting several levels at one.

  Takes a map of logger name => level keyword. The logger name can be a string,
  keyword, or symbol. The keyword :glogi/root refers to the root logger and is
  equivalent to using an empty string.

  This function is exported so it is still available in optimized builds to set
  levels from the javascript console. In this case use nested arrays and
  strings. Use an empty string for the root logger.

  ``` javascript
  lambdaisland.glogi.set_levels([[\"\" \"debug\"] [\"lambdaisland\" \"trace\"]])
  ```
  "
  [lvls]
  (doseq [[logger level] lvls
          :let [level (if (string? level) (keyword level) level)]]
    (set-level logger level)))

(defn enable-console-logging!
  "Log to the browser console. This uses goog.debug.Console directly,
  use [lambdaisland.glogi.console/install!] for a version that plays nicely with
  cljs-devtools."
  []
  (when-let [instance Console/instance]
    (.setCapturing instance true)
    (let [instance (Console.)]
      (set! Console/instance instance)
      (.setCapturing instance)))
  nil)

(defn console-autoinstall!
  "Log to the browser console if the browser location contains Debug=true."
  []
  (Console/autoInstall)
  nil)

(defn popup-logger-window!
  "Pop up a browser window which will display log messages. Returns the FancyWindow instance."
  []
  (doto (FancyWindow.)
    (.setEnabled true)))

(defn log-to-div!
  "Log messages to an element on the page. Returns the DivConsole instance."
  [element]
  (doto (DivConsole. element)
    (.setCapturing  true)))

(defn- logger-glogi-handlers [logger]
  (gobj/get logger logger-handlers-prop))

(defn- swap-handlers! [logger f & args]
  (gobj/set
   logger logger-handlers-prop
   (apply f (logger-glogi-handlers logger) args)))

(defn add-handler
  "Add a log handler to the given logger, or to the root logger if no logger is
  specified. The handler is a function which receives a map as its argument.

  A given handler-fn is only added to a given logger once, even when called
  repeatedly."
  ([handler-fn]
   (add-handler "" handler-fn))
  ([name handler-fn]
   (let [logger (logger name)
         log-record-handler
         (fn [^goog.log/LogRecord record]
           (handler-fn {:sequenceNumber (.-sequenceNumber_ record)
                        :time (.-time_ record)
                        :level (keyword (str/lower-case (.-name (.-level_ record))))
                        :message (.-msg_ record)
                        :logger-name (.-loggerName_ record)
                        :exception (.-exception_ record)}))]
     (when logger
       (when-let [handler (get (logger-glogi-handlers logger) handler-fn)]
         (goog-removeHandler logger handler))
       (swap-handlers! logger assoc handler-fn log-record-handler)
       (some-> logger (goog-addHandler log-record-handler))))))

(defn remove-handler
  ([handler-fn]
   (remove-handler "" handler-fn))
  ([name handler-fn]
   (let [logger (logger name)]
     (when logger
       (when-let [handler (get (logger-glogi-handlers logger) handler-fn)]
         (goog-removeHandler logger handler))
       (swap-handlers! logger dissoc handler-fn)))))

;; Retained for backward compatibility, but we don't add the same handler twice
;; to the same logger.
(def add-handler-once add-handler)
