(ns electric-starter-app.main
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.oscilloscope :as oscilloscope]
            [com.itonomi.komponentkassen.shell2 :as ks2 :include-macros true]))

;; Example components to showcase in the gallery
(e/defn ButtonExample [params]
  (ks2/button {:data-variant (:variant params "primary")
               :data-size (:size params "md")
               :disabled (:disabled params false)}
    (dom/text (:text params "Click me"))
    (let [[t _] (e/Token (dom/On "click" identity nil))]
      (when t
        (when-not (:disabled params)
          (js/alert (str "Button clicked! Variant: " (:variant params))))
        (t)))))

(e/defn CardExample [params]
  (ks2/card
    (ks2/card-block
      (ks2/heading {:level 3}
        (dom/text "Example Card"))
      (ks2/paragraph
        (dom/text "This is a card component from Komponentkassen.")))))

(e/defn InputExample [params]
  (let [!value (atom "")
        value (e/watch !value)]
    (ks2/field
      (ks2/label (dom/text "Example Input"))
      (dom/input
       (dom/props {:type "text"
                   :placeholder "Type something..."
                   :value value
                   :style {:padding "0.5rem"
                           :border "1px solid #d1d5db"
                           :border-radius "4px"
                           :width "100%"}})
       (dom/On "input" #(reset! !value (.. % -target -value)) nil))
      (when (not (empty? value))
        (ks2/field-description
          (dom/text "You typed: " value))))))

(e/defn AlertExample [params]
  (ks2/alert {:data-color (:color params "info")}
    (dom/text (:message params "This is an alert from Komponentkassen."))))

(e/defn Main [ring-request]
  (e/client
   (binding [dom/node js/document.body
             e/http-request (e/server ring-request)]
     (dom/div
      (dom/props {:style {:display "contents"}})
      
      ;; Use the Oscilloscope component gallery
      (oscilloscope/Oscilloscope
       {:components [{:id "button-1"
                      :title "Komponentkassen Button"
                      :description "A button component from the Komponentkassen design system with customizable variants, sizes, and states"
                      :category "Buttons"
                      :Component ButtonExample
                      :parameters [{:name :variant
                                    :label "Variant"
                                    :type :select
                                    :options ["primary" "secondary" "tertiary" "danger"]
                                    :default "primary"}
                                   {:name :size
                                    :label "Size"
                                    :type :select
                                    :options ["sm" "md" "lg"]
                                    :default "md"}
                                   {:name :disabled
                                    :label "Disabled"
                                    :type :checkbox
                                    :default false}
                                   {:name :text
                                    :label "Button Text"
                                    :type :text
                                    :default "Click me"}]}
                     
                     {:id "card-1"
                      :title "Card Component"
                      :description "A card container with block sections"
                      :category "Layout"
                      :Component CardExample}
                     
                     {:id "input-1"
                      :title "Input Field"
                      :description "An input field with label and description"
                      :category "Forms"
                      :Component InputExample}
                     
                     {:id "alert-1"
                      :title "Alert"
                      :description "An alert component for displaying messages with different colors"
                      :category "Feedback"
                      :Component AlertExample
                      :parameters [{:name :color
                                    :label "Color"
                                    :type :select
                                    :options ["info" "success" "warning" "danger"]
                                    :default "info"}
                                   {:name :message
                                    :label "Alert Message"
                                    :type :text
                                    :default "This is an alert from Komponentkassen."}]}
                     
                     {:id "heading-1"
                      :title "Headings"
                      :description "Different heading levels"
                      :category "Typography"
                      :Component (e/fn [params]
                                  (dom/div
                                   (ks2/heading {:level 1}
                                     (dom/text "Heading Level 1"))
                                   (ks2/heading {:level 2}
                                     (dom/text "Heading Level 2"))
                                   (ks2/heading {:level 3}
                                     (dom/text "Heading Level 3"))))}
                     
                     {:id "link-1"
                      :title "Link"
                      :description "A link component"
                      :category "Navigation"
                      :Component (e/fn [params]
                                  (ks2/link {:href "#"}
                                    (dom/text "This is a link")))}]})))))

(defn electric-boot [ring-request]
  #?(:clj  (e/boot-server {} Main (e/server ring-request))
     :cljs (e/boot-client {} Main (e/server (e/amb)))))