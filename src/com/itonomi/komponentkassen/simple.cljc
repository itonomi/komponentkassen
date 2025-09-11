(ns com.itonomi.komponentkassen.simple ;; [com.itonomi.komponentkassen.simple :as ksi]
  {:clj-kondo/ignore true}
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.komponentkassen.shell2 :as ks2]))

#?(:cljs
   (defn target-value [ev]
     (-> ev .-target .-value)))

;; Options: [{:value "whatever" :content "text or (e/fn [])"} ...]
;; Returns change events
(e/defn Select0 [label selected-option options]
  (ks2/field
   (ks2/label (dom/text label))
   (ks2/select
    (e/for [option (e/diff-by :value options)]
      (ks2/select-option
       {:value (:value option)}
       (dom/text (:content option)))))
   ;; We could do target-value instead of identity, but then the consumer 
   ;; loses the ability for transactional event processing. Better to just
   ;; return the event.
   (dom/On "change" identity nil)))

;; Simple components with behaviour built on top of shell2