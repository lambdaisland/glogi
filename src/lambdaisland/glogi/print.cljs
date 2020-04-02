(ns lambdaisland.glogi.print
  (:require [lambdaisland.glogi :as glogi]
            [goog.object :as gobj]))

;; https://github.com/chriskempson/base16-tomorrow-scheme/blob/master/tomorrow.yaml

(def colors
  {:white    "#ffffff"
   :gray1    "#e0e0e0"
   :gray2    "#d6d6d6"
   :gray3    "#8e908c"
   :gray4    "#969896"
   :gray5    "#4d4d4c"
   :gray6    "#282a2e"
   :black    "#1d1f21"
   :red      "#c82829"
   :orange   "#f5871f"
   :yellow   "#eab700"
   :green    "#718c00"
   :turqoise "#3e999f"
   :blue     "#4271ae"
   :purple   "#8959a8"
   :brown    "#a3685a"})

(defn level-color [level]
  (condp <= (glogi/level-value level)
    (glogi/level-value :severe)  :red
    (glogi/level-value :warning) :orange
    (glogi/level-value :info)    :blue
    (glogi/level-value :config)  :green
    (glogi/level-value :fine)    :yellow
    (glogi/level-value :finer)   :gray3
    (glogi/level-value :finest)  :gray4
    :gray2))

(defn add
  ([[res res-css] s]
   [(str res s) res-css])
  ([[res res-css] s color]
   [(str res "%c" (str s) "%c") (conj res-css (str "color:" (get colors color)) "color:black")])
  ([[res res-css] s fg bg]
   [(str res "%c" (str s) "%c") (conj res-css
                                      (str "color:" (get colors fg)
                                           ";background-color:" (get colors bg))
                                      "color:black;background-color:inherit")]))

(defn print-console-log-css [res value]
  (cond
    (= ::comma value)
    (add res ", " :gray2)

    (= ::space value)
    (add res " ")

    (keyword? value)
    (add res value :blue)

    (symbol? value)
    (add res value :green)

    (string? value)
    (add res (pr-str value) :turqoise)

    (map-entry? value)
    (-> res
        (print-console-log-css (key value))
        (add " ")
        (print-console-log-css (val value)))

    (or (instance? cljs.core/PersistentArrayMap value)
        (instance? cljs.core/PersistentHashMap value))
    (as-> res %
      (add % "{" :purple)
      (reduce print-console-log-css % (interpose ::comma value))
      (add % "}" :purple))

    (map? value) ;; non-standard map implementation
    (as-> res %
      (add % (str "#" (let [t (type value)
                            n (.-name t)]
                        (if (empty? n)
                          (pr-str t)
                          n)) " ") :brown)
      (add % "{" :purple)
      (reduce print-console-log-css % (interpose ::comma value))
      (add % "}" :purple))

    (set? value)
    (as-> res %
      (add % "#{" :purple)
      (reduce print-console-log-css % (interpose ::space value))
      (add % "}" :purple))

    (vector? value)
    (as-> res %
      (add % "[" :purple)
      (reduce print-console-log-css % (interpose ::space value))
      (add % "]" :purple))

    (instance? cljs.core.PersistentQueue value)
    (-> res
        (add "#queue " :brown)
        (recur (into [] value)))

    (seq? value)
    (as-> res %
      (add % "(" :brown)
      (reduce print-console-log-css % (interpose ::space value))
      (add % ")" :brown))

    (satisfies? IAtom value)
    (-> res
        (add "#atom " :brown)
        (recur @value))

    (uuid? value)
    (-> res
        (add "#uuid " :brown)
        (recur (str value)))

    (object? value)
    (-> res
        (add "#js " :brown)
        (recur (reduce #(assoc %1 (keyword %2) (gobj/get value %2)) {} (js/Object.keys value))))

    (array? value)
    (-> res
        (add "#js " :brown)
        (recur (into [] value)))

    :else
    (add res (pr-str value) :gray5)))

(defn format [level logger-name value]
  (let [color (level-color level)
        [res res-css] (-> ["" []]
                          (add "[" :white color)
                          (add logger-name :white color)
                          (add "]" :white color)
                          (add " ")
                          (print-console-log-css value))]
    (cons res res-css)))
