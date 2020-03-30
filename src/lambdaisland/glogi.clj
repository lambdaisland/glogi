(ns lambdaisland.glogi)

(defn- log-expr [form level keyvals]
  (let [keyvals-map (apply array-map keyvals)
        formatter (::formatter keyvals-map 'identity)]
    `(log ~(::logger keyvals-map (str *ns*))
          ~level
          (~formatter
           ~(-> keyvals-map
                (dissoc ::logger)
                (assoc :line (:line (meta form)))))
          ~(:exception keyvals-map))))

(defmacro trace [& keyvals]
  (log-expr &form :trace keyvals))

(defmacro info [& keyvals]
  (log-expr &form :info keyvals))

(defmacro debug [& keyvals]
  (log-expr &form :debug keyvals))

(defmacro warn [& keyvals]
  (log-expr &form :warn keyvals))

(defmacro error [& keyvals]
  (log-expr &form :error keyvals))

;; goog.log specific

(defmacro shout [& keyvals]
  (log-expr &form :shout keyvals))

(defmacro severe [& keyvals]
  (log-expr &form :severe keyvals))

(defmacro fine [& keyvals]
  (log-expr &form :fine keyvals))

(defmacro finer [& keyvals]
  (log-expr &form :finer keyvals))

(defmacro finest [& keyvals]
  (log-expr &form :finest keyvals))

(defmacro spy [form]
  (let [res (gensym)]
    `(let [~res ~form]
       ~(log-expr &form :debug [:spy `'~form
                                :=> res])
       ~res)))
