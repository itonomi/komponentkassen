(ns com.itonomi.komponentkassen.demo
  "Demonstration and staging grounds for Komponentkassen. Not to bed
  used by library consumers!"
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]
            [com.itonomi.komponentkassen.core :as kk]
            [lentes.core :as l]
            [clojure.core.match :refer [match]]))




#_(kk/Button {:text "Logg inn"} ;; configuration – properties which the state-machine is ignorant of
             button-state       ;; state
             {"click" {{:ready? true} kk/Tee-fr}} ;; hooks, e.g finalise all "click" events, when `{:ready true}`, with `kk/Tee-fr`
             (partial reset! !button-state))      ;; resetter – the function called to set the new state, if the finalisation says
                                                  ;; that it should change. 


    
;; #_(kk/Button #?(:lpy)
;;
;;              ;; ;; New concept: configuration - the properties of a
;;              ;; ;;              component, which the state-machine is
;;              ;; ;;              ignorant of.
;;              ;; ;;
;;              ;; ;;              Typical examples of configuration is
;;              ;; ;;              styling, prose, layout.
;;              ;; ;;
;;              ;; ;;              There's nothing in the way of
;;              ;; ;;              configuration being dynamic or
;;              ;; ;;              finalisers altering configuration.
;;              ;; #_configuration: {:disabled false
;;              ;;                   :text "Logg inn"}
;;              ;; #_state: {:ready? false}
;;              ;; ;; Hooks connect finalisers to the state-machine
;;              ;; ;;       #_event-pattern  #_state-pattern  #_finaliser
;;              ;; #_hooks: [["onclick"        {:ready? true}   Log-in-fr]]
;;              ;; ;; New concept: resetter! is called when a finalised
;;              ;; ;;              event-transition results in a `new`
;;              ;; ;;              which is unequal to `old`.
;;              ;; ;;
;;              ;; ;;              The default resetter! simply throws an
;;              ;; ;;              exception. It's completely fine to
;;              ;; ;;              program in such a way that no
;;              ;; ;;              event-transition results in
;;              ;; ;;              `(not= new old)`.
;;              ;; #_resetter! (partial reset! !state)
;;              ;; )
;;              ))) 

#?(:clj (defn authenticate [{:as credentials :keys [username password]}]
          (if (zero? (rand-int 2))
            (assoc credentials :error "Incorrect username/password combo")
            (str "session-token:" (random-uuid)))))

#?(:cljs (defn validate-credentials [{:as credentials :keys [username password]}]
           (cond-> credentials
             (not (zero? (rand-int 4))) (assoc :username/error "Something is wrong...")
             (not (zero? (rand-int 4))) (assoc :password/error "Something is wrong...")))) 

;; TODO: implement login in Crud-app by wishful thinking

(e/defn Application-page [!state]
  (kk/Card {}
           (kk/Heading "Welcome back")
           (kk/Paragraph "Here's a list of todos:")
           (kk/List
             "[x] implement List"
             "[ ] implement Autocomplete"
             (dom/div (dom/text "non-string arguments are handled correctly!")))
           (let [!ac-state (l/derive (l/key :autocomplete) !state)
                 ac-state (e/watch !ac-state)]
             (reset! !ac-state {:all-options ["foo" "fuzz" "buzz"]})
             #_(comment

               
               (kk/Autocomplete
                (merge {:label "Autocomplete"
                        :all-options ["foo" "fuzz" "buzz"]}
                       !ac-state)
                (e/fn [new] (e/client (reset! !ac-state new)))
                [{} {:type :input}] (assoc suggestion
                                           :visible-options
                                           (filterv (partial re-matches) (:all-options state)))))

             
             (kk/Autocomplete
              {:label "Autocomplete (vanilla)"}
              ac-state
              (e/fn Do-nothing-fr [state event suggestion] suggestion)
              (e/fn [new] (e/client (reset! !ac-state new))))
             
             (kk/Autocomplete
              {:label "Autocomplete (regex search)"}
              ac-state
              (e/fn [state event suggestion]
                ;; Here we replace the str/include? filering with
                ;; regex filtering. Note that the component was no
                ;; designed with a "replace str/include?" feature in
                ;; mind.
                (cond-> suggestion
                  (= (:type event) :input)
                  (assoc :visible-options
                         (filterv
                          (partial re-matches (re-pattern (:text suggestion)))
                          (:all-options state)))))
              (e/fn [new] (e/client (reset! !ac-state new))))


             #_(let [!ts-ac-state (atom {})
                   ts-ac-state (e/watch !ts-ac-state)]
               (kk/Autocomplete
                {:label "Autocomplete (typesense backed)"}
                ts-ac-state
                (e/fn [state event suggestion]
                  suggestion)
                (e/fn [new] (e/client (reset! !ac-state new))))))))



#_(Convenient:login-page)

(e/defn Crud-app []
  (let [!state (atom {})]
    (Application-page !state)
    #_(if (:session (e/watch !state))
      (Application-page !state)
      (kk.convenient/Login-page {:application-name "Kunnskapsassistenten"}
                                Authenticate))))


;; The big idea is that event handlers should be guided by the
;; component's state-machine.
