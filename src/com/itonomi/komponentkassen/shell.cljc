;; As it turns out, building a flexible component library with
;; behaviours is not at all easy. This namespace is for behaviour-less
;; components. It must be flexible and stable so that it can be used
;; to build experimental high-level APIs going forwards.
;;
;; - No macros
;; - No clever APIs
;; - Uniformity > convenience
;;
;; ------
;;
;; In other words, we aim to mirror the React API as used in the
;; Storybook.
;;

(ns com.itonomi.komponentkassen.shell
  {:clj-kondo/ignore true}
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]
            [com.itonomi.komponentkassen.icon :as kk.icon]))

(defn- merge-prop-pair [left right]
  (if (or (map? left) (map? right))
    (merge left right) ; We intenionally fail when only one value is a map
    right))
(defn- merge-props [left right]
  (merge-with merge-prop-pair left right))

(e/defn Spinner
  "https://storybook.designsystemet.no/?path=/docs/komponenter-loaders-spinner"
  [props]
  (svg/svg (dom/props (merge-props {:class       "ds-spinner"
                                    :role        "img"
                                    :viewBox     "0 0 50 50"
                                    :aria-hidden "true"
                                    :data-size   "sm"}
                                   props))

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
                                   :stroke-width 5}))))

(e/defn Button
  "https://next.storybook.designsystemet.no/?path=/docs/komponenter-button"
  ([props Body]
   (dom/button
     (dom/props (merge-props
                 {:class "ds-button"
                  :variant "primary"
                  :type "button"}
                 props))
     ;; NB: it's crucial that we return the result of Body!
     (Body))))

(e/defn Link
  "https://next.storybook.designsystemet.no/?path=/docs/komponenter-link"
  [props Body]
  (dom/a (dom/props (merge-props {:class "ds-link"}
                                 props))
         (Body)))

(e/defn Textarea
  "https://storybook.designsystemet.no/?path=/docs/komponenter-textarea"
  [props Body]
  (dom/textarea (dom/props (merge-props
                            {:class "ds-input"}
                            props))
                (Body)))

(e/defn Field
  "https://storybook.designsystemet.no/?path=/docs/komponenter-field"
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class ["ds-field"]}
                       props))
           (Body)))

(e/defn FieldDescription [props Body]
  (dom/div (dom/props (merge-props
                       {:data-field "description"}
                       props))
           (Body))) 

 (e/defn FieldAffixes
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-field-affixes"}
                       props))
           (Body))) 

 (e/defn FieldAffix
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-field-affix"
                         :aria-hidden "true"}
                        props))
            (Body))) 

 (e/defn FieldCounter
  [props Body]
  (dom/p (dom/props (merge-props
                     {:data-field "validation"}
                     props))
         (Body)))

(e/defn Label
  "https://storybook.designsystemet.no/?path=/docs/komponenter-typography--docs#label"
  [props Body]
  (dom/label (dom/props (merge-props
                         {:class ["ds-label"]}
                         props))
             (Body)))

(e/defn Select
  "https://storybook.designsystemet.no/?path=/docs/komponenter-select--docs"
  [props Body]
  (dom/select (dom/props (merge-props
                          {:class ["ds-input"]}
                          props))
              (Body)))

(e/defn SelectOption
  ;; In React it's Select.Option
  [props Body]
  (dom/option (dom/props props)
              (Body)))

(e/defn TabsTab
  [props Body]
  (dom/button (dom/props (merge-props
                          {:role "tab"
                           :type "button"
                           :data-roving-tabindex-item "true"}
                          props))
              (Body)))

(e/defn TabsPanel
  [props Body]
  (dom/div (dom/props (merge-props
                       {:role "tabpanel"}
                       props))
           (Body)))

(e/defn TabsList
  [props Body]
  (dom/div (dom/props (merge-props
                       {:role "tablist"}
                       props))
           (Body)))

(e/defn Tabs
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class ["ds-tabs"]}
                       props))
           (Body)))

(e/defn Heading
  "https://storybook.designsystemet.no/?path=/docs/komponenter-typography--docs#heading"
  [props Body]
  (let [B (e/fn [] 
            (dom/props (merge-props
                        {:class ["ds-heading"]}
                        props))
            (Body))]
   (case (:level props)
     1 (dom/h1 (B))
     2 (dom/h2 (B))
     3 (dom/h3 (B))
     4 (dom/h4 (B))
     5 (dom/h5 (B))
     6 (dom/h6 (B)))))

(e/defn Paragraph
  "https://storybook.designsystemet.no/?path=/docs/komponenter-typography--docs#paragraph"
  [props Body]
  (dom/p (dom/props (merge-props
                     {:class ["ds-paragraph"]}
                     props))
         (Body)))

(e/defn Divider 
  "https://storybook.designsystemet.no/?path=/docs/komponenter-divider"
  ;; I'm being dogmatic about the calling convention because I intend to generate
  ;; process this code programmatically
  [props Body]
  (dom/hr (dom/props (merge-props
                      {:class ["ds-divider"]}
                      props))
         (Body)))

(e/defn ListUnordered
  [props Body]
  (dom/ul (dom/props (merge-props
                      {:class ["ds-list"]}
                      props))
         (Body))) 

 (e/defn ListOrdered
  [props Body]
  (dom/ol (dom/props (merge-props
                      {:class "ds-list"}
                      props))
          (Body)))

(e/defn ListItem
  [props Body]
  (dom/li (dom/props props)
          (Body)))

(e/defn Details
  "https://storybook.designsystemet.no/?path=/docs/komponenter-details"
  [props Body]
  ;; Upstream uses <u-details> which seems to be backwards compatible with regular <details>
  (dom/details (dom/props (merge-props
                          {:class ["ds-details"]}
                          props))
              (Body)))

(e/defn DetailsSummary 
  [props Body]
  (dom/summary (dom/props props)
               (Body)))

(e/defn DetailsContent
  [props Body]
  (dom/div (dom/props props)
               (Body)))

(e/defn Card 
  "https://storybook.designsystemet.no/?path=/docs/komponenter-card"
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class ["ds-card"]}
                       props))
           (Body))) 

 (e/defn CardBlock
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-card__block"}
                       props))
           (Body)))

(e/defn Table
  "https://storybook.designsystemet.no/?path=/docs/komponenter-table"
  [props Body]
  (dom/table (dom/props (merge-props
                         {:class ["ds-table"]}
                         props))
             (Body)))

(e/defn TableHead
  [props Body]
  (dom/thead (dom/props props)
             (Body)))

(defmacro dom-tfoot [& body] (dom/element* :tfoot body))

(e/defn TableBody
  [props Body]
  (dom/tbody (dom/props props)
             (Body)))

(e/defn TableFoot
  [props Body]
  (dom-tfoot (dom/props props)
             (Body)))

(e/defn TableRow
  [props Body]
  (dom/tr (dom/props props)
          (Body)))

(e/defn TableCell
  [props Body]
  (dom/td (dom/props props)
          (Body)))

(e/defn TableHeaderCell
  [props Body]
  (dom/th (dom/props props)
          (Body))) 

 (e/defn Alert
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-alert"
                        :data-color "info"}
                       props))
           (Body))) 

 (e/defn Avatar
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-avatar"
                         :data-variant "circle"
                         :role "img"}
                        props))
            (Body))) 

 (e/defn Badge
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-badge"
                         :data-variant "base"}
                        props))
            (Body))) 

 (e/defn BadgePosition
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-badge--position"
                         :data-overlap "rectangle"
                         :data-placement "top-right"}
                        props))
            (Body))) 

 (e/defn Breadcrumbs
  [props Body]
  (dom/nav (dom/props (merge-props
                       {:class "ds-breadcrumbs"
                        :aria-label "Du er her:"}
                       props))
           (Body))) 

 (e/defn BreadcrumbsList
  [props Body]
  (dom/ol (dom/props props)
          (Body))) 

 (e/defn BreadcrumbsItem
  [props Body]
  (dom/li (dom/props props)
          (Body))) 

 (e/defn BreadcrumbsLink
  [props Body]
  (Link props Body)) 

 (e/defn Checkbox
  [props Body]
  (Field props
         (e/fn []
           (dom/input (dom/props (merge-props
                                  {:type "checkbox"
                                   :class "ds-input"}
                                  props)))
           (Body)))) 

 (e/defn ChipButton
  [props Body]
  (dom/button (dom/props (merge-props
                          {:class "ds-chip"
                           :type "button"}
                          props))
              (Body))) 

 (e/defn ChipRemovable
  [props Body]
  (ChipButton (merge-props {:data-removable true} props) Body)) 

 (e/defn ChipCheckbox
  [props Body]
  (dom/label (dom/props (merge-props
                         {:class "ds-chip"}
                         props))
             (dom/input (dom/props (merge-props
                                    {:type "checkbox"
                                     :class "ds-input"}
                                    props)))
             (Body))) 

 (e/defn ChipRadio
  [props Body]
  (dom/label (dom/props (merge-props
                         {:class "ds-chip"}
                         props))
             (dom/input (dom/props (merge-props
                                    {:type "radio"
                                     :class "ds-input"}
                                    props)))
             (Body))) 

 (e/defn Combobox
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-combobox"}
                       props))
           (Body))) 

 (e/defn ComboboxOption
  [props Body]
  (dom/button (dom/props (merge-props
                          {:class "ds-combobox__option"
                           :role "option"
                           :type "button"
                           :tabIndex -1}
                          props))
              (Body))) 

 (e/defn ComboboxCustom
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-combobox__custom"
                        :role "option"
                        :tabIndex -1}
                       props))
           (Body))) 

 (e/defn ComboboxEmpty
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-combobox__empty"}
                       props))
           (Body))) 

 (e/defn Dialog
  [props Body]
  (dom/dialog (dom/props (merge-props
                          {:class "ds-dialog"
                           :data-modal true}
                          props))
              (Body))) 

 (e/defn DialogBlock
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-dialog__block"}
                       props))
           (Body))) 

 (e/defn DialogTrigger
  [props Body]
  (Button (merge-props {:aria-haspopup "dialog"} props) Body)) 

 (e/defn DialogTriggerContext
  [props Body]
  (dom/div (dom/props props)
           (Body))) 

 (e/defn Dropdown
  [props Body]
  (Popover (merge-props {:class "ds-dropdown"
                         :placement "bottom-end"}
                        props)
           Body)) 

 (e/defn DropdownButton
  [props Body]
  (Button (merge-props {:variant "tertiary"} props) Body)) 

 (e/defn DropdownHeading
  [props Body]
  (Heading props Body)) 

 (e/defn DropdownItem
  [props Body]
  (dom/li (dom/props props)
          (Body))) 

 (e/defn DropdownList
  [props Body]
  (dom/ul (dom/props props)
          (Body))) 

 (e/defn DropdownTrigger
  [props Body]
  (PopoverTrigger props Body)) 

 (e/defn DropdownTriggerContext
  [props Body]
  (PopoverTriggerContext props Body)) 

 (e/defn ErrorSummary
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-error-summary"
                        :tabIndex -1}
                       props))
           (Body))) 

 (e/defn ErrorSummaryHeading
  [props Body]
  (Heading props Body)) 

 (e/defn ErrorSummaryList
  [props Body]
  (ListUnordered props Body)) 

 (e/defn ErrorSummaryItem
  [props Body]
  (ListItem props Body)) 

 (e/defn ErrorSummaryLink
  [props Body]
  (Link (merge-props {:data-color "neutral"} props) Body)) 

 (e/defn Fieldset
  [props Body]
  (dom/fieldset (dom/props (merge-props
                            {:class "ds-fieldset"}
                            props))
                (Body))) 

 (e/defn FieldsetLegend
  [props Body]
  (dom/legend (dom/props props)
              (Body))) 

 (e/defn FieldsetDescription
  [props Body]
  (Paragraph props Body)) 

 (e/defn Input
  [props Body]
  (dom/input (dom/props (merge-props
                         {:class "ds-input"
                          :type "text"}
                         props)))) 

 (e/defn Pagination
  [props Body]
  (dom/nav (dom/props (merge-props
                       {:class "ds-pagination"
                        :aria-label "Sidenavigering"}
                       props))
           (Body))) 

 (e/defn PaginationList
  [props Body]
  (dom/ul (dom/props props)
          (Body))) 

 (e/defn PaginationItem
  [props Body]
  (dom/li (dom/props props)
          (Body))) 

 (e/defn PaginationButton
  [props Body]
  (Button props Body)) 

 (e/defn Radio
  [props Body]
  (Field props
         (e/fn []
           (dom/input (dom/props (merge-props
                                  {:type "radio"
                                   :class "ds-input"}
                                  props)))
           (Body)))) 

 (e/defn Search
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-search"}
                       props))
           (Body))) 

 (e/defn SearchInput
  [props Body]
  (dom/input (dom/props (merge-props
                         {:type "search"
                          :class "ds-input"
                          :placeholder ""}
                         props)))) 

 (e/defn SearchButton
  [props Body]
  (Button (merge-props {:type "submit"} props) 
          (e/fn [] 
            (if (Body)
              (Body)
              (dom/text "Søk"))))) 

 (e/defn SearchClear
  [props Body]
  (Button (merge-props {:variant "tertiary"
                        :type "reset"
                        :aria-label "Tøm"
                        :icon true}
                       props)
          Body)) 

 (e/defn Switch
  [props Body]
  (Field props
         (e/fn []
           (dom/input (dom/props (merge-props
                                  {:type "checkbox"
                                   :role "switch"
                                   :class "ds-input"}
                                  props)))
           (Body)))) 

 (e/defn Tag
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-tag"}
                        props))
            (Body))) 

 (e/defn ToggleGroup
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-togglegroup"
                        :role "radiogroup"}
                       props))
           (Body))) 

 (e/defn ToggleGroupItem
  [props Body]
  (Button (merge-props {:variant "tertiary"} props) Body)) 

 (e/defn Tooltip
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-tooltip"
                         :role "tooltip"
                         :popover "manual"}
                        props))
            (Body))) 

 (e/defn ValidationMessage
  [props Body]
  (dom/p (dom/props (merge-props
                     {:class "ds-validation-message"
                      :data-field "validation"}
                     props))
         (Body))) 

 (e/defn Popover
  [props Body]
  (dom/div (dom/props (merge-props
                       {:class "ds-popover"
                        :popover "manual"
                        :data-variant "default"}
                       props))
           (Body))) 

 (e/defn PopoverTrigger
  [props Body]
  (Button props Body)) 

 (e/defn PopoverTriggerContext
  [props Body]
  (dom/div (dom/props props)
           (Body))) 

 (e/defn Skeleton
  [props Body]
  (dom/span (dom/props (merge-props
                        {:class "ds-skeleton"
                         :aria-hidden "true"
                         :data-variant "rectangle"}
                        props))
            (Body)))
