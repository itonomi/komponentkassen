(ns com.itonomi.oscilloscope
  "A component gallery and storybook for Electric Clojure development"
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.komponentkassen.shell :as ks]))

(e/defn ParameterControl
  "Control for adjusting a component parameter"
  [{:keys [label type options value on-change]}]
  (dom/div
   (dom/props {:style {:margin-bottom "1rem"}})
   
   (dom/label
    (dom/props {:style {:display "block"
                        :font-size "0.875rem"
                        :font-weight "500"
                        :margin-bottom "0.25rem"
                        :color "#374151"}})
    (dom/text label))
   
   (case type
     :select
     (ks/Select {:value (or value (first options))
                 :style {:width "100%"}}
                (e/fn []
                  (dom/On "change" #(on-change (.. % -target -value)) nil)
                  (e/for [option (e/diff-by identity options)]
                    (ks/SelectOption {:value option
                                      :selected (= option value)}
                                     (e/fn [] (dom/text option))))))
     
     :checkbox
     (ks/Checkbox {:checked value}
                  (e/fn []
                    (dom/On "change" #(on-change (.. % -target -checked)) nil)))
     
     :text
     (dom/div
      (ks/Input {:value value
                 :style {:width "100%"}})
      (dom/On "input" #(on-change (.. % -target -value)) nil)))))

(e/defn ComponentCard
  "Display a component in a card format with title, description, and parameter controls"
  [{:keys [title description parameters]} Component]
  (let [!params (atom (into {} (map (fn [p] [(:name p) (:default p)]) parameters)))
        params (e/watch !params)]
    (dom/div
     (dom/props {:style {:border "1px solid #e5e7eb"
                         :border-radius "8px"
                         :padding "1.5rem"
                         :margin-bottom "1.5rem"
                         :background "white"}})
     
     (when title
       (dom/h3
        (dom/props {:style {:margin-bottom "0.5rem"
                            :font-size "1.125rem"
                            :font-weight "600"}})
        (dom/text title)))
     
     (when description
       (dom/p
        (dom/props {:style {:margin-bottom "1rem"
                            :color "#6b7280"
                            :font-size "0.875rem"}})
        (dom/text description)))
     
     ;; Parameter controls
     (when (seq parameters)
       (dom/div
        (dom/props {:style {:margin-bottom "1rem"
                            :padding "1rem"
                            :background "#f3f4f6"
                            :border-radius "4px"}})
        
        (dom/h4
         (dom/props {:style {:font-size "0.875rem"
                             :font-weight "600"
                             :margin-bottom "0.75rem"}})
         (dom/text "Parameters"))
        
        (e/for [param (e/diff-by :name parameters)]
          (ParameterControl
           {:label (:label param)
            :type (:type param)
            :options (:options param)
            :value (get params (:name param))
            :on-change #(swap! !params assoc (:name param) %)}))))
     
     ;; Component preview
     (dom/div
      (dom/props {:style {:padding "1rem"
                          :background "#f9fafb"
                          :border-radius "4px"}})
      (Component params)))))

(e/defn CategorySection
  "Group components by category"
  [{:keys [category-name]} & components]
  (dom/div
   (dom/props {:style {:margin-bottom "2rem"}})
   
   (dom/h2
    (dom/props {:style {:margin-bottom "1rem"
                        :font-size "1.5rem"
                        :font-weight "700"
                        :border-bottom "2px solid #e5e7eb"
                        :padding-bottom "0.5rem"}})
    (dom/text category-name))
   
   (e/for [Component (e/diff-by identity components)]
     (Component))))

(e/defn SearchBar
  "Search components in the gallery"
  [{:keys [value on-change placeholder]}]
  (dom/input
   (dom/props {:type "text"
               :placeholder (or placeholder "Search components...")
               :value value
               :style {:width "100%"
                       :padding "0.75rem 1rem"
                       :border "1px solid #d1d5db"
                       :border-radius "6px"
                       :font-size "1rem"}})
   (dom/On "input" #(on-change (.. % -target -value)) nil)))

(e/defn Sidebar
  "Navigation sidebar for component categories"
  [{:keys [categories active-category on-select]}]
  (dom/div
   (dom/props {:style {:width "250px"
                       :background "#f9fafb"
                       :padding "1rem"
                       :border-right "1px solid #e5e7eb"
                       :height "100vh"
                       :overflow-y "auto"}})
   
   (dom/h3
    (dom/props {:style {:margin-bottom "1rem"
                        :font-weight "600"}})
    (dom/text "Categories"))
   
   (e/for [category (e/diff-by identity categories)]
     (dom/button
      (dom/props {:style {:display "block"
                          :width "100%"
                          :text-align "left"
                          :padding "0.5rem 1rem"
                          :margin-bottom "0.25rem"
                          :border "none"
                          :border-radius "4px"
                          :background (if (= category active-category)
                                        "#3b82f6"
                                        "transparent")
                          :color (if (= category active-category)
                                   "white"
                                   "#374151")
                          :cursor "pointer"}})
      (dom/On "click" #(on-select category) nil)
      (dom/text category)))))

(e/defn Oscilloscope
  "Main component gallery viewer"
  [{:keys [components]}]
  (let [!search-query (atom "")
        search-query (e/watch !search-query)
        !active-category (atom "All")
        active-category (e/watch !active-category)
        
        ;; Group components by category
        categories (into ["All"] (distinct (map :category components)))
        
        ;; Filter components based on search and category
        filtered-components (filter
                             (fn [component]
                               (and
                                (or (= active-category "All")
                                    (= (:category component) active-category))
                                (or (empty? search-query)
                                    (clojure.string/includes?
                                     (clojure.string/lower-case (or (:title component) ""))
                                     (clojure.string/lower-case search-query)))))
                             components)]
    
    (dom/div
     (dom/props {:style {:display "flex"
                         :height "100vh"
                         :background "#ffffff"}})
     
     ;; Sidebar
     (Sidebar {:categories categories
               :active-category active-category
               :on-select #(reset! !active-category %)})
     
     ;; Main content
     (dom/div
      (dom/props {:style {:flex "1"
                          :padding "2rem"
                          :overflow-y "auto"}})
      
      ;; Header
      (dom/div
       (dom/props {:style {:margin-bottom "2rem"}})
       
       (dom/h1
        (dom/props {:style {:font-size "2rem"
                            :font-weight "700"
                            :margin-bottom "1rem"}})
        (dom/text "Component Gallery"))
       
       (SearchBar {:value search-query
                   :on-change #(reset! !search-query %)}))
      
      ;; Components grid
      (dom/div
       (dom/props {:style {:display "grid"
                           :grid-template-columns "repeat(auto-fill, minmax(400px, 1fr))"
                           :gap "1.5rem"}})
       
       (e/for [component-data (e/diff-by :id filtered-components)]
         (ComponentCard
          {:title (:title component-data)
           :description (:description component-data)
           :parameters (:parameters component-data)}
          (:Component component-data))))))))

;; Example usage - developers would add their components here
(e/defn ExampleGallery []
  (Oscilloscope
   {:components [{:id "button-1"
                  :title "Primary Button"
                  :description "A standard primary button component"
                  :category "Buttons"
                  :Component (e/fn []
                              (dom/button
                               (dom/props {:style {:padding "0.5rem 1rem"
                                                   :background "#3b82f6"
                                                   :color "white"
                                                   :border "none"
                                                   :border-radius "4px"
                                                   :cursor "pointer"}})
                               (dom/text "Click me")))}
                 
                 {:id "input-1"
                  :title "Text Input"
                  :description "A basic text input field"
                  :category "Forms"
                  :Component (e/fn []
                              (dom/input
                               (dom/props {:type "text"
                                           :placeholder "Enter text..."
                                           :style {:padding "0.5rem"
                                                   :border "1px solid #d1d5db"
                                                   :border-radius "4px"}})))}]}))