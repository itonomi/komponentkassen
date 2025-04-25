(ns com.itonomi.komponentkassen.icon
  "Icons do seem to sit strictly underneath core."
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]
            [lambdaisland.deep-diff2 :as ddiff]))

(e/defn Arrow [{:as config :keys [direction]}]
  (let [dom-id (gensym)] 
    (svg/svg
      (dom/props {;; NOTE: specifying xmlns breaks things
                  #_#_:xmlns "http://www.w3.org/2000/svg"
                  :width "1em"
                  :height "1em"
                  :fill "none"
                  :viewBox "0 0 24 24"
                  :aria-labelledby dom-id
                  :focusable "false"
                  :role "img"
                  :font-size "1.5em"})
      
      (svg/title 
        (dom/props {:id dom-id})
        (svg/text (case direction
                    :up "arrow up"
                    :down "arrow down")))

      (svg/path 
        (dom/props {:fill "currentColor"
                    :fill-rule "evenodd"
                    :d (case direction
                         :up "M11.47 7.97a.75.75 0 0 1 1.06 0l5.5 5.5a.75.75 0 1 1-1.06 1.06L12 9.56l-4.97 4.97a.75.75 0 0 1-1.06-1.06z"
                         :down "M5.97 9.47a.75.75 0 0 1 1.06 0L12 14.44l4.97-4.97a.75.75 0 1 1 1.06 1.06l-5.5 5.5a.75.75 0 0 1-1.06 0l-5.5-5.5a.75.75 0 0 1 0-1.06")
                    :clip-rule "evenodd"})))))
