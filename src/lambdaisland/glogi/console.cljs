(ns lambdaisland.glogi.console
  (:require [lambdaisland.glogi :as glogi]
            [goog.object :as gobj]
            [goog.debug.LogBuffer :as LogBuffer]
            [goog.debug.Console :as Console]))

(defn log-method [{:keys [value] :as level}]
  (condp #(>= %2 %1) value
    (glogi/level-value :severe)  "error"
    (glogi/level-value :warning) "warn"
    (glogi/level-value :info)    "info"
    (glogi/level-value :config)  "log"
    "log"))

(defn devtools-installed? []
  (and (exists? js/devtools.core.installed_QMARK_)
       (js/devtools.core.installed_QMARK_)))

(defn format [{:keys [logger-name message exception]}]
  [(str "[" logger-name "]")
   (if (devtools-installed?)
     message
     (pr-str message))])

(defonce console-log
  (fn [{:keys [logger-name level exception] :as record}]
    (let [method-name (log-method level)
          method      (or (gobj/get js/console method-name)
                          js/console.log)]
      (apply method (format record))
      (when exception
        (method (str "[" logger-name "]") (str exception) "\n" (.-stack exception))))))

(defn install! []
  ;; Disable goog.debug.Console if it's been enabled (e.g. by Figwheel), we do
  ;; console logging now
  (when-let [instance Console/instance]
    (.setCapturing instance false))

  ;; hax0r! goog.debug.Logger normally calls String on the log message, but it
  ;; doesn't do this if a LogBuffer is active. This causes a single LogRecord
  ;; instance to constantly be reused, which is fine though since we convert it
  ;; to an immutable value in add-handler.
  (when-not (LogBuffer/isBufferingEnabled)
    (set! LogBuffer/CAPACITY 2))

  (glogi/add-handler-once console-log))
