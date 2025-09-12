(ns com.itonomi.komponentkassen.simple ;; [com.itonomi.komponentkassen.simple :as ksi]
  {:clj-kondo/ignore true}
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.komponentkassen.shell2 :as ks2]
            ))

;; Simple components with behaviour built on top of shell2

#?(:cljs
   (defn target-value [ev]
     (-> ev .-target .-value)))

;; Options: [{:value "whatever" :content "text or (e/fn [])"} ...]
;; Returns change events
(e/defn Select0 
  ;; The enabled/disabled arity is NOT thought out
  ([label selected-option options disabled?]
   (ks2/field
    (ks2/label (dom/text label))
    (ks2/select 
     (when disabled? (dom/props {:disabled disabled?}))
     (e/for [option (e/diff-by :value options)]
       (ks2/select-option
        {:selected (#{selected-option} (:value option))
         :value (:value option)}
        (dom/text (:content option)))))
    ;; We could do target-value instead of identity, but then the consumer 
    ;; loses the ability for transactional event processing. Better to just
    ;; return the event.
    (when-not disabled? (dom/On "change" identity nil))))
  ([label selected-option options]
   (Select0 label selected-option options false)))


(e/defn SpinnerButton0 
  "A button which renders a spinner loading animation until the OnClick callback yields"
  [label OnClick]
  (ks2/button
   (let [!loading (atom false)]
     (when (e/watch !loading) (ks2/spinner))
     (dom/text label)
     (let [e (dom/On "click" identity nil)
           [t err] (e/Token e)]
       (when t
         (case (reset! !loading true)
           (case (OnClick)
             (case (reset! !loading false)
               (t)))))))))