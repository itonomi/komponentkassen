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



;; TODO: have a macro here which reads in the ns and generates convenience macros for all the components.
;;       BAD: (TabsTab {} (e/fn [] (dom/text "Documents")))
;;       ->
;;       LESSBAD: (tabsTab (dom/text "Documents"))

(defmacro dom-u-tags [& body] (dom/element* :u-tags body))
(defmacro dom-u-datalist [& body] (dom/element* :u-datalist body))
(defmacro dom-u-option [& body] (dom/element* :u-option body))
