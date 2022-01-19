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

(defmacro shout [& keyvals]
  (log-expr &form :shout keyvals))

(defmacro error [& keyvals]
  (log-expr &form :error keyvals))

(defmacro severe [& keyvals]
  (log-expr &form :severe keyvals))

(defmacro warn [& keyvals]
  (log-expr &form :warn keyvals))

(defmacro info [& keyvals]
  (log-expr &form :info keyvals))

(defmacro debug [& keyvals]
  (log-expr &form :debug keyvals))

(defmacro config [& keyvals]
  (log-expr &form :config keyvals))

(defmacro trace [& keyvals]
  (log-expr &form :trace keyvals))

(defmacro fine [& keyvals]
  (log-expr &form :fine keyvals))

(defmacro finer [& keyvals]
  (log-expr &form :finer keyvals))

(defmacro finest [& keyvals]
  (log-expr &form :finest keyvals))

(defmacro spy
  ([form]
   (let [res (gensym)]
     `(let [~res ~form]
        ~(log-expr &form :debug [:spy `'~form
                                 :=> res])
        ~res)))
  ([form & forms]
   (let [form-res (mapv (fn [f] (let [res (gensym)] (vector f res))) (conj forms form))
         form-res-dict (into [] (mapcat (fn [[k v]] [`'~k v]) form-res))]
     `(let ~(into [] (mapcat (fn [[k v]] [v k]) form-res))
        ~(log-expr &form :debug [:spy form-res-dict])
        ~(last (last form-res))))))
