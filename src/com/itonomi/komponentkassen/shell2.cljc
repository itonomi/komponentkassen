;; Macro-based component library that mirrors shell.cljc
;; Components are lowercase macros working similarly to dom/ elements
;; (component-name props-map? & children)

(ns com.itonomi.komponentkassen.shell2
  {:clj-kondo/ignore true}
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]))

;; IDEA
;; We are currently checked whether the first arg is a map at macroexpand time.
;; We ought to check it at runtime, so that we can do something like
;;
;; (ks2/whatever (expression (that) (computes-props)))
;;
;; I'll see with further use

(defn merge-prop-pair [left right]
  (if (or (map? left) (map? right))
    (merge left right)
    right))

(def data-attributes
  #{"active" "border" "chromatic" "color" "color-scheme" "colors" "command"
    "count" "creatable" "featured" "field" "hover" "icon" "index" "initials"
    "is-main" "level" "mobile" "modal" "multiple" "open" "overlap" "placement"
    "popover" "position" "pseudo-state" "size" "sr-plural" "sr-singular"
    "sticky-header" "testid" "text" "theme" "typography" "value" "variant"
    "weight" "width" "zebra"})

(defn translate-data-props [props]
  (reduce (fn [acc attr]
            (let [kw-key (keyword attr)
                  str-key attr
                  data-key (keyword (str "data-" attr))]
              (cond
                (contains? acc kw-key)
                (-> acc
                    (dissoc kw-key)
                    (assoc data-key (get acc kw-key)))

                (contains? acc str-key)
                (-> acc
                    (dissoc str-key)
                    (assoc data-key (get acc str-key)))

                :else acc)))
          props
          data-attributes))

(defn merge-props [left right]
  (-> (merge-with merge-prop-pair left right)
      translate-data-props))

(defmacro button [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/button
       (dom/props (merge-props
                   {:class ["ds-button"]
                    :data-variant "primary"
                    :type "button"}
                   ~props))
       ~@children)))

(defmacro link [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/a (dom/props (merge-props {:class ["ds-link"]}
                                    ~props))
            ~@children)))

(defmacro textarea [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/textarea (dom/props (merge-props
                               {:class ["ds-input"]}
                               ~props))
                   ~@children)))

(defmacro field [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-field"]}
                          ~props))
              ~@children)))

(defmacro field-description [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:data-field "description"}
                          ~props))
              ~@children)))

(defmacro field-affixes [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-field-affixes"]}
                          ~props))
              ~@children)))

(defmacro field-affix [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-field-affix"]
                            :aria-hidden "true"}
                           ~props))
               ~@children)))

(defmacro field-counter [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/p (dom/props (merge-props
                        {:data-field "validation"}
                        ~props))
            ~@children)))

(defmacro label [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/label (dom/props (merge-props
                            {:class ["ds-label"]}
                            ~props))
                ~@children)))

(defmacro select [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/select (dom/props (merge-props
                             {:class ["ds-input"]}
                             ~props))
                 ~@children)))

(defmacro select-option [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/option (dom/props ~props)
                 ~@children)))

(defmacro tabs-tab [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/button (dom/props (merge-props
                             {:role "tab"
                              :type "button"
                              :data-roving-tabindex-item "true"}
                             ~props))
                 ~@children)))

(defmacro tabs-panel [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:role "tabpanel"}
                          ~props))
              ~@children)))

(defmacro tabs-list [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:role "tablist"}
                          ~props))
              ~@children)))

(defmacro tabs [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-tabs"]}
                          ~props))
              ~@children)))

(defmacro heading [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(let [props# ~props
           level# (:level props#)]
       (case level#
         1 (dom/h1 (dom/props (merge-props
                               {:class ["ds-heading"]}
                               props#))
                   ~@children)
         2 (dom/h2 (dom/props (merge-props
                               {:class ["ds-heading"]}
                               props#))
                   ~@children)
         3 (dom/h3 (dom/props (merge-props
                               {:class ["ds-heading"]}
                               props#))
                   ~@children)
         4 (dom/h4 (dom/props (merge-props
                               {:class ["ds-heading"]}
                               props#))
                   ~@children)
         5 (dom/h5 (dom/props (merge-props
                               {:class ["ds-heading"]}
                               props#))
                   ~@children)
         6 (dom/h6 (dom/props (merge-props
                               {:class ["ds-heading"]}
                               props#))
                   ~@children)
         (dom/h2 (dom/props (merge-props
                            {:class ["ds-heading"]}
                            props#))
                 ~@children)))))

(defmacro paragraph [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/p (dom/props (merge-props
                        {:class ["ds-paragraph"]}
                        ~props))
            ~@children)))

(defmacro divider [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/hr (dom/props (merge-props
                         {:class ["ds-divider"]}
                         ~props))
             ~@children)))

(defmacro list-unordered [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/ul (dom/props (merge-props
                         {:class ["ds-list"]}
                         ~props))
             ~@children)))

(defmacro list-ordered [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/ol (dom/props (merge-props
                         {:class ["ds-list"]}
                         ~props))
             ~@children)))

(defmacro list-item [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/li (dom/props ~props)
             ~@children)))

(defmacro details [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/details (dom/props (merge-props
                             {:class ["ds-details"]}
                             ~props))
                  ~@children)))

(defmacro details-summary [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/summary (dom/props ~props)
                  ~@children)))

(defmacro details-content [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props ~props)
              ~@children)))

(defmacro card [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-card"]}
                          ~props))
              ~@children)))

(defmacro card-block [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-card__block"]}
                          ~props))
              ~@children)))

(defmacro table [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/table (dom/props (merge-props
                            {:class ["ds-table"]}
                            ~props))
                ~@children)))

(defmacro table-head [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/thead (dom/props ~props)
                ~@children)))

(defmacro dom-tfoot [& body] 
  `(dom/element* :tfoot ~body))

(defmacro table-body [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/tbody (dom/props ~props)
                ~@children)))

(defmacro table-foot [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom-tfoot (dom/props ~props)
                ~@children)))

(defmacro table-row [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/tr (dom/props ~props)
             ~@children)))

(defmacro table-cell [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/td (dom/props ~props)
             ~@children)))

(defmacro table-header-cell [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/th (dom/props ~props)
             ~@children)))

(defmacro alert [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-alert"]
                           :data-color "info"}
                          ~props))
              ~@children)))

(defmacro avatar [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-avatar"]
                            :data-variant "circle"
                            :role "img"}
                           ~props))
               ~@children)))

(defmacro badge [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-badge"]
                            :data-variant "base"}
                           ~props))
               ~@children)))

(defmacro badge-position [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-badge--position"]
                            :data-overlap "rectangle"
                            :data-placement "top-right"}
                           ~props))
               ~@children)))

(defmacro breadcrumbs [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/nav (dom/props (merge-props
                          {:class ["ds-breadcrumbs"]
                           :aria-label "Du er her:"}
                          ~props))
              ~@children)))

(defmacro breadcrumbs-list [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/ol (dom/props ~props)
             ~@children)))

(defmacro breadcrumbs-item [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/li (dom/props ~props)
             ~@children)))

(defmacro breadcrumbs-link [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(link ~props ~@children)))

(defmacro checkbox [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(field ~props
            (dom/input (dom/props (merge-props
                                   {:type "checkbox"
                                    :class ["ds-input"]}
                                   ~props)))
            ~@children)))

(defmacro chip-button [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/button (dom/props (merge-props
                             {:class ["ds-chip"]
                              :type "button"}
                             ~props))
                 ~@children)))

(defmacro chip-removable [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(chip-button (merge-props {:data-removable true} ~props) ~@children)))

(defmacro chip-checkbox [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/label (dom/props (merge-props
                            {:class ["ds-chip"]}
                            ~props))
                (dom/input (dom/props (merge-props
                                       {:type "checkbox"
                                        :class ["ds-input"]}
                                       ~props)))
                ~@children)))

(defmacro chip-radio [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/label (dom/props (merge-props
                            {:class ["ds-chip"]}
                            ~props))
                (dom/input (dom/props (merge-props
                                       {:type "radio"
                                        :class ["ds-input"]}
                                       ~props)))
                ~@children)))

(defmacro combobox [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-combobox"]}
                          ~props))
              ~@children)))

(defmacro combobox-option [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/button (dom/props (merge-props
                             {:class ["ds-combobox__option"]
                              :role "option"
                              :type "button"
                              :tabindex -1}
                             ~props))
                 ~@children)))

(defmacro combobox-custom [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-combobox__custom"]
                           :role "option"
                           :tabindex -1}
                          ~props))
              ~@children)))

(defmacro combobox-empty [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-combobox__empty"]}
                          ~props))
              ~@children)))

(defmacro dialog [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/dialog (dom/props (merge-props
                             {:class ["ds-dialog"]
                              :data-modal true}
                             ~props))
                 ~@children)))

(defmacro dialog-block [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-dialog__block"]}
                          ~props))
              ~@children)))

(defmacro dialog-trigger [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button (merge-props {:aria-haspopup "dialog"} ~props) ~@children)))

(defmacro dialog-trigger-context [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props ~props)
              ~@children)))

(declare popover popover-trigger popover-trigger-context)

(defmacro dropdown [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(popover (merge-props {:class ["ds-dropdown"]
                            :data-placement "bottom-end"}
                           ~props)
              ~@children)))

(defmacro dropdown-button [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button (merge-props {:data-variant "tertiary"} ~props) ~@children)))

(defmacro dropdown-heading [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(heading ~props ~@children)))

(defmacro dropdown-item [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/li (dom/props ~props)
             ~@children)))

(defmacro dropdown-list [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/ul (dom/props ~props)
             ~@children)))

(defmacro dropdown-trigger [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(popover-trigger ~props ~@children)))

(defmacro dropdown-trigger-context [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(popover-trigger-context ~props ~@children)))

(defmacro error-summary [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-error-summary"]
                           :tabindex -1}
                          ~props))
              ~@children)))

(defmacro error-summary-heading [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(heading ~props ~@children)))

(defmacro error-summary-list [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(list-unordered ~props ~@children)))

(defmacro error-summary-item [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(list-item ~props ~@children)))

(defmacro error-summary-link [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(link (merge-props {:data-color "neutral"} ~props) ~@children)))

(defmacro fieldset [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/fieldset (dom/props (merge-props
                               {:class "ds-fieldset"}
                               ~props))
                   ~@children)))

(defmacro fieldset-legend [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/legend (dom/props ~props)
                 ~@children)))

(defmacro fieldset-description [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(paragraph ~props ~@children)))

(defmacro input [& args]
  (let [props (if (map? (first args))
                (first args)
                {})]
    `(dom/input (dom/props (merge-props
                            {:class ["ds-input"]
                             :type "text"}
                            ~props)))))

(defmacro pagination [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/nav (dom/props (merge-props
                          {:class ["ds-pagination"]
                           :aria-label "Sidenavigering"}
                          ~props))
              ~@children)))

(defmacro pagination-list [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/ul (dom/props ~props)
             ~@children)))

(defmacro pagination-item [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/li (dom/props ~props)
             ~@children)))

(defmacro pagination-button [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button ~props ~@children)))

(defmacro radio [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(field ~props
            (dom/input (dom/props (merge-props
                                   {:type "radio"
                                    :class ["ds-input"]}
                                   ~props)))
            ~@children)))

(defmacro search [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-search"]}
                          ~props))
              ~@children)))

(defmacro search-input [& args]
  (let [props (if (map? (first args))
                (first args)
                {})]
    `(dom/input (dom/props (merge-props
                            {:type "search"
                             :class ["ds-input"]
                             :placeholder ""}
                            ~props)))))

(defmacro search-button [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button (merge-props {:type "submit"} ~props)
             (or ~@children (dom/text "Søk")))))

(defmacro search-clear [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button (merge-props {:data-variant "tertiary"
                           :type "reset"
                           :aria-label "Tøm"
                           :icon true}
                          ~props)
             ~@children)))

(defmacro switch [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(field ~props
            (dom/input (dom/props (merge-props
                                   {:type "checkbox"
                                    :role "switch"
                                    :class ["ds-input"]}
                                   ~props)))
            ~@children)))

(defmacro tag [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-tag"]}
                           ~props))
               ~@children)))

(defmacro toggle-group [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-togglegroup"]
                           :role "radiogroup"}
                          ~props))
              ~@children)))

(defmacro toggle-group-item [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button (merge-props {:data-variant "tertiary"} ~props) ~@children)))

(defmacro tooltip [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-tooltip"]
                            :role "tooltip"
                            :popover "manual"}
                           ~props))
               ~@children)))

(defmacro validation-message [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/p (dom/props (merge-props
                        {:class ["ds-validation-message"]
                         :data-field "validation"}
                        ~props))
            ~@children)))

(defmacro popover [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props (merge-props
                          {:class ["ds-popover"]
                           :popover "manual"
                           :data-variant "default"}
                          ~props))
              ~@children)))

(defmacro popover-trigger [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(button ~props ~@children)))

(defmacro popover-trigger-context [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/div (dom/props ~props)
              ~@children)))

(defmacro skeleton [& args]
  (let [[props & children] (if (map? (first args))
                              args
                              (cons {} args))]
    `(dom/span (dom/props (merge-props
                           {:class ["ds-skeleton"]
                            :aria-hidden "true"
                            :data-variant "rectangle"}
                           ~props))
               ~@children)))




;; Not sure if this belongs here?? I think it does, just not sure why it's not here in the first place
;;
;; We're at the point where the library is seeded, it's fine to maintain things by need now.
(defmacro spinner [& args]
  (let [[props & children] (if (map? (first args))
                             args
                             (cons {} args))]
    `(svg/svg (dom/props (merge-props {:class       "ds-spinner"
                                       :role        "img"
                                       :viewBox     "0 0 50 50"
                                       :aria-hidden "true"
                                       :data-size   "sm"}
                                      ~props))
     
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
                                      :stroke-width 5})))))
