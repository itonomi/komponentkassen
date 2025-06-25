(ns com.itonomi.komponentkassen.core
  "A Electric v3 component library built on top of https://designsystemet.no.

  Alias com.itonomi.komponentkassen.core :as kk and ctrl-f
  \"comment :usage\" for a nice list of copy-pasteable examples.

  For Finaliser components, the corresponding state-machine and state-machine tests
  are meant as documentation too.

  Happy hacking!"
  {:clj-kondo/ignore true}
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]
            [lambdaisland.deep-diff2 :as ddiff]
            [clojure.core.match :refer [match]]
            [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests tap %]])
  #?(:cljs (:require-macros [com.itonomi.komponentkassen.core :refer [defmachine]])))

;; Going forwards components will be defined in shell.cljc and be
;; without behaviour. We might build something with behaviour after
;; that, but it should be optional as it's really hard to get the API
;; right

;; ====================================
;;
;;           Infrastructure
;;
;; ====================================

#_(hyperfiddle.rcf/enable!)

(e/defn Rcomp [F G]
  (e/fn [x] (-> (F x) G)))

(e/defn Prn-state-fr [[state intent suggestion]]
  (prn :state state)
  [state intent suggestion])

(e/defn Prn-intent-fr [[state intent suggestion]]
  (prn :intent intent)
  [state intent suggestion])

(e/defn Prn-suggestion-fr [[state intent suggestion]]
  (prn :suggestion suggestion)
  [state intent suggestion])

(e/defn Fn->fr [f]
  (e/fn Fr [[state intent suggestion]]
    [state intent (f suggestion)]))

(defn print-diff [old new]
  (-> (ddiff/diff old new)
    ddiff/minimize
    ddiff/pretty-print ;; Printing directly with
                       ;; ddiff/pretty-print does not look
                       ;; right
    with-out-str
    print))

(e/defn Diff-fr [[state intent suggestion]]
  (print-diff state suggestion)
  [state intent suggestion])

;; Identity and Tee are valid finalisers :^)
(e/defn Identity [x] x)
(e/defn Tee [x] (prn x) x)

(e/defn No-op [& args])
(e/defn Never-called [&args]
  ;;
  )

(e/defn Atom-Reset! [!a]
  (e/fn Reset-atom! [new] (reset! !a new)))

;; TODO: Add a macro `finaliser` or `fr` akin to machine.
;;       We should be able to define Finalisers like so
;;       (fr [state-pattern intent-pattern] suggestion)
;;
;;       Then just doing (fr) results in one which always
;;       falls back to suggestion, which is just what we
;;       want.
;;
;;       For adding special behaviour we just add in new
;;       transitions:
;;
;;       (fr [_ [:insert text]]
;;           (assoc suggestion :visible-options (Options text)))
;;
;;       It's beautiful

(defn fixed-point
  "The fixed-point of f and x is a fancy math phrase for describing the
   value that repeated application of f to x converges to."
  [f x]
  (let [x' (f x)]
    (if (= x x') x (recur f x'))))

(defn merge-config-pair [left right]
  (if (or (map? left) (map? right))
    (merge left right) ; We intenionally fail when only one value is a map
    right))
(def merge-configs (partial merge-with merge-config-pair))

(e/defn Handle-events
  "`events` is a map. The keys are the set of DOM events to listen to,
  and the values are fns that converts such DOM events to the format
  expected by the state-machine. E.g

{\"input\" #(hash-map :type :input :text (-> % .-target .-value))}"
  [state events state-machine Finaliser Reset!]
  (e/for [[event-name event->intent] (e/diff-by key events)]
    (when-some [event (dom/On event-name identity nil)]
      (when-some [[t _err] (e/Token event)]
        (when t
          (when-let [intent (event->intent event)]
            ;; If we touch the event, we must eat it.
            ;;
            ;; NB: if this is removed Text-field :enter event will
            ;; break
            ;;
            ;; Part of me thinks we should propagate the events still. Let's wait until there's a need
            ;; and then do programming by wishful thinking.
            (.preventDefault event)
            (when-some [suggestion (state-machine
                                     ;; For why e/snapshot is needed: https://clojurians.slack.com/archives/C7Q9GSHFV/p1738457960209599
                                     (e/snapshot state)
                                     intent)]
              (let [[_state _intent new-state] (Finaliser [state intent suggestion])]
                (when (not= state new-state) (Reset! new-state)))))
          (t))))))

(defmacro machine-iterate
  [name & pattern-transitions]
  `(fn ~name [~'state ~'intent]
     (match [~'state ~'intent]
       ~@pattern-transitions
       :else ~'state)))

(tests
  (def button-machine (machine-iterate button
                        [{:loading? false} :trigger] {:loading? true}))
  (button-machine {:loading? false} :trigger) := {:loading? true})

(defmacro machine
  "Returns a state-machine, see `defmachine` for details."
  [name & pattern-transitions]
  `(let [~'mitr (machine-iterate ~name ~@pattern-transitions)
         ~'normalize (partial fixed-point (fn [~'state] (~'mitr ~'state :machine/normalize)))]
     (fn [~'state ~'intent]
       (->
         ~'state
         ~'normalize
         (~'mitr ~'intent)
         ~'normalize))))

(tests
  (def button-machine (machine button
                        [_ :machine/normalize] (merge {:loading? false} state)
                        [{:loading? false} :trigger] {:loading? true}))
  (button-machine nil :trigger) := {:loading? true})

(defmacro defmachine
  "Defines a state-machine. A state-machine is a fn
   from a state and an intent to a new state.

   Takes a name and a sequence of
   `[state-pattern intent-pattern] suggestion` pairs.
   `[state-pattern intent-pattern]` is a core.match
   pattern and suggestion is a regular Clojure expression,
   where `state` and `intent` are bound to the corresponding
   values.

   There is one special intent :machine/normalize which is used
   to normalize the input and the output of the state-machine on
   every transition.

   For a state-machine mach, (mach state :machine/normalize)
   results in (fixed-point #(mach % :machine/normalize) state). In
   plain english this means that we will keep normalizing state until
   no more normalization is possible.

   There is nothing wrong with defining state-machines as
   regular fns, if that is more natural for your use case."
  [name & pattern-transitions]
  `(def ~name (machine ~name ~@pattern-transitions)))

(defmacro fr [& pattern-transitions]
  `(fn [[state# intent# suggestion#]]
     (match [state# intent#]
            ~@pattern-transitions
            :else [state# intent# suggestion#])))

;; ====================================
;;
;;         Wrapper components
;;
;; ====================================

(defmacro Field-wrapper [config & field]
  `(let [{:keys [~'dom-id ~'label ~'description ~'error]} ~config]
     ;; IMPORTANT: data- fields impact styling!
     (dom/div (dom/props (merge-configs {:class "ds-field" :data-my-field true} ~config))
       (when ~'label (dom/label (dom/props {:class "ds-label"
                                            :data-weight "medium"
                                            :for   ~'dom-id})
                       (dom/text ~'label)))
       (when ~'description (dom/div (dom/props {:id ~'dom-id
                                                :data-field "description"})
                             (dom/text ~'description)))
       ~@field
       (when ~'error (dom/p (dom/props {:class "ds-validation-message"
                                        :id    ~'dom-id
                                        :data-field "validation"})
                       (dom/text ~'error))))))

;; ====================================
;;
;;       Config-only components
;;
;; ====================================

(e/defn Spinner "https://next.storybook.designsystemet.no/?path=/docs/komponenter-loaders-spinner--docs"
  ([] (Spinner nil))
  ([config]
   (let [ config]
     (svg/svg (dom/props (merge-configs {:class       "ds-spinner"
                                         :role        "img"
                                         :viewBox     "0 0 50 50"
                                         :aria-hidden "true"
                                         :data-size   "sm"}
                           config))

       (svg/circle (dom/props {:class        "ds-spinner__background"
                               :cx           25
                               :cy           25
                               :r            20
                               :fill         "none"
                               :stroke-width 5}))

       (svg/circle (dom/props {:class        "ds-spinner__circle"
                               :cx           25
                               :cy           25
                               :r            20
                               :fill         "none"
                               :stroke-width 5}))))))

(e/defn Paragraph "https://next.storybook.designsystemet.no/?path=/docs/komponenter-typography--docs"
    ([text] (Paragraph nil text))
    ([config text]
     (dom/p (dom/props (merge-configs {:class "ds-paragraph"
                               :data-variant "default"}
                         config))
       (dom/text text))))

(e/defn Heading "https://next.storybook.designsystemet.no/?path=/docs/komponenter-typography--docs"
    ([text] (Heading nil text))
    ([config text]
     (dom/h2 (dom/props (merge {:class "ds-heading"}
                          config))
       (dom/text text))))


(e/defn Divider []
  (dom/hr (dom/props {:class "ds-divider"})))

;; ====================================
;;
;;        Finaliser components
;;
;; ====================================

(defmachine checkbox
  [_ [:toggle]] (update state :checked? not))

(e/defn Checkbox [config state Finaliser Reset!]
  (prn :fdjkalfjksl state)
  (let [{:keys [label description]} config
        {:keys [checked?]} state
        dom-id (gensym)]

    (dom/div (dom/props {:class "ds-field"})
      (dom/input (dom/props {:class "ds-input"
                             :type "checkbox"
                             :value "value"
                             :checked checked?
                             :id dom-id
                             :aria-describedby (str dom-id ":description")}))
      (when label
        (dom/label (dom/props {:class       "ds-label"
                               :data-weight "regular"
                               :for         dom-id})
          (dom/text label)))
      (when description
        (dom/div (dom/props {:data-field "description"
                             :id         (str dom-id ":description")})
          (dom/text description)))

      (Handle-events
        state
        {"change" #(vector :toggle)}
        checkbox
        Finaliser
        Reset!)
      )))

(defmachine tabs-mach
  [_ [:switch n]] (assoc state :active-tab n))

(e/defn Tabs "https://next.storybook.designsystemet.no/?path=/docs/komponenter-tabs--docs"
  ;; TODO: feature to have several active tabs, which show side-by-side
  ;;       probably right-clicking or double clicking will enable this
  [config state Finaliser Reset!]
  (let [{:keys [tabs]}       config
        {:keys [active-tab]} state
        dom-id               (gensym)]
    (dom/div (dom/props {:class "ds-tabs"})
      (dom/div (dom/props {:role     "tablist"
                           :tabindex "0"})
        ;; `:disabled?` is blocked by designsystemet
        (e/for [[n {:keys [text disabled?]}] (e/diff-by first (map-indexed vector tabs))]
          (dom/button (dom/props {:aria-selected             (= n active-tab)
                                  :id                        dom-id
                                  :role                      "tab"
                                  :type                      "button"
                                  :data-roving-tabindex-item "true"
                                  :tabindex                  (if (= n active-tab) "0" "-1")})
            (dom/text text)
            (Handle-events
              state
              {"mousedown" #(vector :switch n)}
              tabs-mach
              Finaliser
              Reset!)))))
    ;; The wrapper is required to prevent overflow
    ;; Temp: had to remove the wrapper
    (let [Tab-panel (:Tab-panel (tabs active-tab))]
      (Tab-panel))
    #_(dom/div (dom/props (merge-configs {#_#_:style    {:height  "100%"
                                 ;; the padding is adhoc styling - not according to
                                 ;; designsystemet.
                                                       :padding "1rem"}
                                        :role     "tabpanel"
                                        :tabindex "0"}
                          (:panel config)))
        ;; Electric fns have to be invoked with a form where the
        ;; first expression is a capitalized symbol resolving to
        ;; the given fns.
        ;;
        ;; For example ((:Tab-panel (tabs active-tab))) does not
        ;; work, but the following code does.
      (prn active-tab)
      (let [Tab-panel (:Tab-panel (tabs active-tab))]
        (Tab-panel)))))

(defmachine autocomplete
  [{:text text :all-options all-opts} :machine/normalize]
  (assoc state :visible-options (filterv #(str/includes? % text) all-opts))

  [_ :machine/normalize]
  (merge {:text "" :all-options []} state)

  [{:disabled? true} _]
  state

  [_ [:toggle]]
  (update state :open? not)

  [{:all-options all-opts} [:insert text]]
  (assoc state :text text)

  [_ [:select option]]
  (assoc state :selection option
    :text option))

(tests
  (def !state (atom nil))
  (def autocomplete! (partial swap! !state autocomplete))

  "Automatic normalization"
  (autocomplete! :whatever)
  @!state := {:text "", :all-options [], :visible-options []}

  "Automatic filtering"
  (swap! !state assoc :all-options ["foo" "fuzz" "bar"])
  (autocomplete! [:insert "f"])
  @!state := {:text "f", :all-options ["foo" "fuzz" "bar"], :visible-options ["foo" "fuzz"]}

  "Toggling"
  (autocomplete! [:toggle])
  (:open? @!state) := true

  "Selecting"
  (autocomplete! [:select "foo"])
  (:selection @!state) := "foo")

;; TODO: tab component, seems really useful https://next.storybook.designsystemet.no/?path=/docs/komponenter-tabs--docs

;; TODO: table (and later datagrid), Benjamin requested https://next.storybook.designsystemet.no/?path=/docs/komponenter-table--docs

;; TODO: base it on the new Suggestion instead of the deprecated combo-box
;;       https://next.storybook.designsystemet.no/?path=/docs/komponenter-suggestion--docs
(e/defn Autocomplete [config state Finaliser Reset!]
  (let [dom-id (gensym)
        {:keys [label]} config
        {:keys [open? text visible-options all-options]} (autocomplete state :machine/normalize)]
    (dom/div
      (dom/props {:class "ds-combobox ds-combobox--md"})

      (dom/label
        (dom/props {:class "ds-label ds-combobox__label"
                    :data-weight "medium"
                    :data-size "md"
                    :for dom-id})
        (dom/text label))

      (dom/div
        (dom/props {:class "ds-paragraph ds-textfield__input ds-combobox__input__wrapper"
                    :data-variant "default"
                    :data-size "md"})

        (dom/div
          (dom/props {:class "ds-combobox__chip-and-input"})

          (dom/input
            (dom/props {:aria-autocomplete "list"
                        :role "combobox"
                        :aria-expanded "false"
                        :autocomplete "off"
                        :aria-busy "false"
                        :id dom-id
                        :class "ds-paragraph ds-combobox__input"
                        :data-variant "default"
                        :data-size "md"
                        :value text})
            (Handle-events state
              {"input" #(vector :insert (-> % .-target .-value))}
              autocomplete
              Finaliser
              Reset!)))

        (dom/div
          (dom/props {:class "ds-combobox__arrow"})
          #_(kk.icon/Arrow {:direction (if open? :down :up)})))

      (dom/div
        (dom/props {:class "ds-combobox__error-message"
                    :id dom-id
                    :aria-live "polite"
                    :aria-relevant "additions removals"}))

      (Handle-events state
        {"click" #(vector :toggle)}
        autocomplete
        Finaliser
        Reset!)

      (when open? (dom/ul
                    (e/for [o (e/diff-by identity visible-options)]
                      (dom/li (dom/text o)
                        (Handle-events state
                          {"click" #(vector :select o)}
                          autocomplete
                          Finaliser
                          Reset!))))))

    #_(dom/div (dom/props {:class "ds-combobox__options-wrapper ds-combobox--md"
                           #_#_:style {:position "absolute"
                                       :left "0px"
                                       :top "0px"
                                       :transform "translate(167px, 4670px)"
                                       :will-change "transform"
                                       :width "448px"
                                       :max-height "200px"}})

        (dom/button (dom/props {:class "ds-label ds-combobox__option"
                                :data-weight "medium"
                                :data-size "md"})
          (dom/span (dom/props {:class "ds-label"
                                :data-weight "medium"
                                :data-size "md"})
            (dom/div (dom/props {})))
          (dom/label (dom/props {:class "ds-label ds-combobox__option__label"
                                 :data-weight "medium"
                                 :data-size "md"})
            (dom/text "Brønnøysund")))

        (dom/button (dom/props {:class "ds-label ds-combobox__option "
                                :data-weight "medium"
                                :data-size "md"})
          (dom/span (dom/props {:class "ds-label"
                                :data-weight "medium"
                                :data-size "md"})
            (dom/div
              (dom/props {:class "ds-combobox__option__icon-wrapper--selected"})
              (svg/svg
                (dom/props {#_#_:xmlns "http://www.w3.org/2000/svg"
                            :width "1em"
                            :height "1em"
                            :fill "none"
                            :viewBox "0 0 24 24"
                            :focusable "false"
                            :class "ds-combobox__option__icon-wrapper__icon"})
                (svg/path
                  (dom/props {:fill "currentColor"
                              :fill-rule "evenodd"
                              :d "M18.998 6.94a.75.75 0 0 1 .063 1.058l-8 9a.75.75 0 0 1-1.091.032l-5-5a.75.75 0 1 1 1.06-1.06l4.438 4.437 7.471-8.405A.75.75 0 0 1 19 6.939"
                              :clip-rule "evenodd"})))))
          (dom/label (dom/props {:class "ds-label ds-combobox__option__label"
                                 :data-weight "medium"
                                 :data-size "md"})
            (dom/text "Brønnøysund")))
        (dom/button
          (dom/props
            {:id dom-id
             :role "option"
             :type "button"
             :tabindex "-1"
             :class "ds-label ds-combobox__option ds-combobox__option--active"
             :data-weight "medium"
             :data-size "md"})
          (dom/span
            (dom/props {:class "ds-label"
                        :data-weight "medium"
                        :data-size "md"})
            (dom/div
              (dom/props {:class "ds-combobox__option__icon-wrapper--selected"})
              (svg/svg
                (dom/props {#_#_:xmlns "http://www.w3.org/2000/svg"
                            :width "1em"
                            :height "1em"
                            :fill "none"
                            :viewBox "0 0 24 24"
                            :focusable "false"
                            :class "ds-combobox__option__icon-wrapper__icon"})
                (svg/path
                  (dom/props {:fill "currentColor"
                              :fill-rule "evenodd"
                              :d "M18.998 6.94a.75.75 0 0 1 .063 1.058l-8 9a.75.75 0 0 1-1.091.032l-5-5a.75.75 0 1 1 1.06-1.06l4.438 4.437 7.471-8.405A.75.75 0 0 1 19 6.939"
                              :clip-rule "evenodd"})))))
          (dom/label
            (dom/props {:class "ds-label ds-combobox__option__label"
                        :data-weight "medium"
                        :data-size "md"
                        :id dom-id})
            (dom/text "Leikanger")))

        (dom/button (dom/props {:class "ds-label ds-combobox__option ds-combobox__option--active"
                                :data-weight "medium"
                                :data-size "md"})
          (dom/span (dom/props {:class "ds-label"
                                :data-weight "medium"
                                :data-size "md"})
            (dom/div (dom/props {})))
          (dom/label (dom/props {:class "ds-label ds-combobox__option__label"
                                 :data-weight "medium"
                                 :data-size "md"})
            (dom/text "Trondheim"))))))

(defmachine text-field
  [{:disabled? true} _] state

  [_ [:insert text]]
  (-> state
    (dissoc :error)
    (assoc :text text))

  [{:error _} [:submit]]
  state

  [_ [:submit]]
  (assoc state :text ""))

(def text-field-defaults {:placeholder ""})

(e/defn Text-field
  "https://next.storybook.designsystemet.no/?path=/docs/komponenter-textfield--docs"
  ([config state]
   (Text-field config state No-op))
  ([config state Finaliser]
   (Text-field config state Finaliser No-op))
  ([config state Finaliser Reset!]
   (let [{:as config :keys [dom-id placeholder]} (-> (merge-configs text-field-defaults config)
                                                   (assoc :dom-id (gensym)))
         {:as state :keys [text disabled?]} state
         state (dissoc state :disabled?)]
     (Field-wrapper
      (merge config state)
      (if (:multiline config)
        (dom/textarea (dom/props {:class "ds-input"
                                  :id dom-id
                                  :value text
                                  :placeholder placeholder
                                  :disabled disabled?
                                  :rows (:rows config)
                                  :cols (:cols config)}))
        (dom/input (dom/props {:class "ds-input"
                               :type "text"
                               :id dom-id
                               :value text
                               :placeholder placeholder
                               :disabled disabled?})))
       (Handle-events state
         {"input" #(vector :insert (-> % .-target .-value))
          "keydown" #(when (and (= (.-key %) "Enter") (seq (:text state))) [:submit])}
         text-field
         Finaliser
         Reset!)))))

(defmacro Dumb-text-field
  "https://next.storybook.designsystemet.no/?path=/docs/komponenter-textfield--docs"
  ([config state & body]
   `(let [{:as ~'config :keys [~'dom-id ~'placeholder]} (-> (merge-configs text-field-defaults ~config)
                                                      (assoc :dom-id (gensym)))
          {:as ~'state :keys [~'text ~'disabled?]} ~state
          ~'state (dissoc ~'state :disabled?)]
     (Field-wrapper
       (merge ~'config ~'state)
       (if (:multiline ~'config)
         (dom/textarea (dom/props {:class "ds-input"
                                   :id ~'dom-id
                                   :value ~'text
                                   :placeholder ~'placeholder
                                   :disabled ~'disabled?
                                   :rows (:rows ~'config)
                                   :cols (:cols ~'config)}))
         (dom/input (dom/props {:class "ds-input"
                                :type "text"
                                :id ~'dom-id
                                :value ~'text
                                :placeholder ~'placeholder
                                :disabled ~'disabled?})))
       ~@body))))

(defmachine button
  [_ :trigger] :loading)

;; TODO: use gensym symbols in macros, e.g `s#

(e/defn Button "https://next.storybook.designsystemet.no/?path=/docs/komponenter-button--docs"
  ([] (Button nil))
  ([config]
    ;; If state is not specified, we manage the state.
    (let [!s (atom nil)]
      (Button config (e/watch !s) Identity (Atom-Reset! !s))))
  ;; I think this might be the right second arity
  ([config !state]
   (Button config (e/watch !state) Identity (Atom-Reset! !state)))
  ([config state Finaliser]
    (Button config state Finaliser nil))
  ([config state Finaliser Reset!]
    (let [{:keys [text]} config]
      (dom/button (dom/props (merge-configs
                               {:class "ds-button"
                                :variant "primary"
                                :type "button"}
                               config))
        (when (= state :loading) (Spinner))
        (dom/text text)
        (Handle-events
          state
          {"click" #(when (not= state :loading) :trigger)}
          button
          Finaliser
          Reset!)))))

(e/defn Dumb-button "https://next.storybook.designsystemet.no/?path=/docs/komponenter-button--docs"
  ([config Body]
    (let [{:keys [text]} config]
      (dom/button (dom/props (merge-configs
                               {:class "ds-button"
                                :variant "primary"
                                :type "button"}
                               (dissoc config :loading)))
        (when (= config :loading) (Spinner))
        (dom/text text)
        (Body)))))




#_(e/fn Send-form-fr [[old new err :as x]]
    (send-form formstate)
    [old old err])

(defmacro List [& children]
    `(dom/ul
       (dom/props {:class "ul-list"})
       ~@(map (fn [c]
                (if (string? c)
                  `(dom/li (dom/text ~c))
                  `(dom/li ~c)))
           children)))

#_(defmacro List2 [& children]
    `(dom/ul
       (dom/props {:class "ul-list"})
       ~@children))
#_(e/defn List-item [s]
    (dom/ul (dom/text s)))

#_(defmacro Ordered-list [& children]
    (dom/ul))

#_(comment :example
    (kk/Card {}
      (kk/Heading "Card Neutral")
      (kk/Paragraph "Most provide as with carried business are much better more the perfected designer. Writing slightly explain desk unable at supposedly about this")
      (kk/Paragraph {:data-size :sm} "Footer text")))

(defmacro Card "https://next.storybook.designsystemet.no/?path=/docs/komponenter-card--docs"
  ;; Should data-$PROP be simply $PROP?
    [props & children]
    `(dom/div (dom/props (merge-with
                           merge
                           {:class "ds-card"
                            :data-color "neutral"
                            :style {:max-width "320px"}}
                           ~props))
       ~@children))

#_(comment
  ;; The arglists are programmatically accessible from the var, could
  ;; be used to make a parameter explorer ala storybook.
    (:arglists (meta (var Text-field)))
    :=> ([{:keys [label disabled read-only multiline? description error counter
                  class-name style prefix suffix aria-label aria-labelledby type
                  size data-color]}]))

#_(comment (and
             (= (text-field {:error "foo"} {:type :input :text "foo"}))
             (text-field {:error "foo"}
               {:type :submit})))

#_(Text-field {:placeholder "Jeg har vondt i tommellen ;("}
    (:search-phrase application-state)
    Suggestion-fr #_(e/fn [state event suggestion] suggestion)
    (e/fn [x] (swap! !application-state assoc :search-phrase (:text x))))

#_(def digit? #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

#_(e/defn F []
    (e/fn [state event suggestion]
      (case (:event type)
        :input (if (every? digit? (:text event))
                 suggestion
                 (assoc state :error "Only digits allowed"))
        suggestion))

    (e/fn [state event suggestion]
      (cond-> suggestion
        (and (= (:event type) :input)
          (some? (complement digit?) (:text event)))
        (assoc :error "Only digits allowed"))))

#_(merge {:variant "primary"} #:state{:loading? false})

(e/defn Link [href text]
    (dom/a (dom/props {:href href
                       :class "ds-link"})
           (dom/text text)))

#_(e/defn Link
  ([] (Link nil))
  ([config]
    ;; If state is not specified, we manage the state.
    (let [!s (atom nil)]
      (Link config (e/watch !s) Identity (Atom-Reset! !s))))
  ([config !state]
   (Link config (e/watch !state) Identity (Atom-Reset! !state)))
  ([config state Finaliser]
    (Link config state Finaliser nil))
  ([config state Finaliser Reset!]
    (let [{:keys [text]} config]
      (dom/a (dom/props (merge-configs
                               {:class "ds-link"}
                               config))
        (when (= state :loading) (Spinner))
        (dom/text text)
        (Handle-events
          state
          {"click" #(when (not= state :loading) :trigger)}
          button ;; Reusing the state-machine :^)
          Finaliser
          Reset!)))))

#_(e/def PseudoLink [])

#_(e/defn Login-page [config state hooks reset!]
  ;; TODO: localise based on a dynamic variable *language*
    (let [Log-in-fr (e/fn [t]
                      t)
          !button-state (atom {:ready? true})
          button-state (e/watch !button-state)]
      (Card {:style {:max-width "420px"}}
        (Heading "Logg inn i Kunnskapsassistenten")
        (Text-field {:label "Navn"})
        (Text-field {:label "Passord"
                     :type "password"})
        (Link "/" "Glemt passord?")
        (Button {:text "Logg inn"}
          button-state
          {"click" {{:ready? true} (Rcomp Log-in-fr)}}))))

#_(e/defn Icon-arrow [{:as config :keys [direction]}]
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

        (case direction
          :up (svg/path (dom/props {:fill "currentColor"
                                    :fill-rule "evenodd"
                                    :d "M5.97 9.47a.75.75 0 0 1 1.06 0L12 14.44l4.97-4.97a.75.75 0 1 1 1.06 1.06l-5.5 5.5a.75.75 0 0 1-1.06 0l-5.5-5.5a.75.75 0 0 1 0-1.06"
                                    :clip-rule "evenodd"}))
          :down  (svg/path
                   (dom/props {:fill "currentColor"
                               :fill-rule "evenodd"
                               :d "M5.97 9.47a.75.75 0 0 1 1.06 0L12 14.44l4.97-4.97a.75.75 0 1 1 1.06 1.06l-5.5 5.5a.75.75 0 0 1-1.06 0l-5.5-5.5a.75.75 0 0 1 0-1.06"
                               :clip-rule "evenodd"}))))))
