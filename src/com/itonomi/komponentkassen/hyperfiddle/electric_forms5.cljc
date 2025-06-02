(ns com.itonomi.komponentkassen.hyperfiddle.electric-forms5
  "Drop in replacement for hyperfiddle.electric-forms5, with
  Designsystemet styling"
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-forms5 :as forms]))

;; TODO: reuse the docstring metadata
(e/defn Input [v & props]
  (e/Apply forms/Input v (conj (conj props "ds-input") :class )))

