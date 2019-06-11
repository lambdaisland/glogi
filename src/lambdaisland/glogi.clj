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

(defmacro debug [& keyvals]
  (log-expr &form :debug keyvals))

(defmacro warn [& keyvals]
  (log-expr &form :warn keyvals))

(defmacro error [& keyvals]
  (log-expr &form :error keyvals))
