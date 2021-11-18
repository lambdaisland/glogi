(ns lambdaisland.glogi.console
  (:require [lambdaisland.glogi :as glogi]
            [lambdaisland.glogi.print :as print]
            [goog.object :as gobj]
            [goog.debug.Console :as Console]))

;; By default we do CSS colorization on non-IE browsers only. You can change
;; this in your build with :closure-defines. Possible values: "auto" "true" "false"
(goog-define colorize "auto")

(defn log-method [level]
  (condp #(>= %2 %1) (glogi/level-value level)
    (glogi/level-value :severe)  "error"
    (glogi/level-value :warning) "warn"
    (glogi/level-value :info)    "info"
    (glogi/level-value :config)  "log"
    "log"))

(defn format-raw [{:keys [level logger-name message exception]}]
  [(str "[" logger-name "]") message])

(defn format-css [{:keys [level logger-name message exception]}]
  (print/format level logger-name message))

(defn format-plain [{:keys [level logger-name message exception]}]
  [(str "[" logger-name "]") (pr-str message)])

(defn make-console-log [format]
  (fn [{:keys [logger-name level exception] :as record}]
    (let [method-name (log-method level)
          method      (or (gobj/get js/console method-name)
                          js/console.log)]
      (apply method (format record))
      (when exception
        (method (str "[" logger-name "]") (str exception) "\n" (.-stack exception))))))

(defonce console-log-raw (make-console-log format-raw))
(defonce console-log-css (make-console-log format-css))
(defonce console-log-plain (make-console-log format-plain))

;; backward compatibility
(defonce format format-plain)
(defonce console-log console-log-plain)

(defn devtools-installed? []
  (and (exists? js/devtools.core.installed_QMARK_)
       (js/devtools.core.installed_QMARK_)))

(defn- browser? []
  (exists? js/window))

(defn- ie? []
  (and (browser?)
       (exists? js/window.navigator)
       (exists? js/window.navigator.userAgent)
       ;; IE and pre-chromium EDGE don't support %c
       (or (> (.indexOf js/window.navigator.userAgent "MSIE") -1)
           (> (.indexOf js/window.navigator.userAgent "Trident") -1))))

(defn select-handler []
  (case colorize
    "auto"
    (cond
      (devtools-installed?)        console-log-raw
      (and (browser?) (not (ie?))) console-log-css
      :else                        console-log-plain)
    "raw"
    console-log-raw
    "true"
    console-log-css
    "false"
    console-log-plain))

(defn install! []
  ;; Disable goog.debug.Console if it's been enabled (e.g. by Figwheel), we do
  ;; console logging now
  (when-let [instance Console/instance]
    (.setCapturing instance false))

  (glogi/add-handler-once (select-handler)))
