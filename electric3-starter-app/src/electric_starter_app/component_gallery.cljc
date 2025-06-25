(ns electric-starter-app.component-gallery
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.komponentkassen.shell :as ks]))

(e/defn ComponentGallery []
  (e/client
    (dom/div
      (dom/props {:style {:padding "2rem"}})
      (ks/Heading {:level 1 :data-size "large"} 
        (e/fn [] (dom/text "Komponentkassen Gallery")))
      
      ;; Button Components
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Button Components")))
        (dom/div
          (dom/props {:style {:display "flex" :gap "1rem" :margin-top "1rem" :flex-wrap "wrap"}})
          (ks/Button {} 
            (e/fn [] (dom/text "Primary Button")))
          (ks/Button {:data-variant "secondary"} 
            (e/fn [] (dom/text "Secondary Button")))
          (ks/Button {:data-variant "tertiary"} 
            (e/fn [] (dom/text "Tertiary Button")))
          (ks/Button {:data-variant "danger"} 
            (e/fn [] (dom/text "Danger Button")))
          (ks/Button {:disabled true} 
            (e/fn [] (dom/text "Disabled Button")))))
      
      ;; Chip Components
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Chip Components")))
        (dom/div
          (dom/props {:style {:display "flex" :gap "1rem" :margin-top "1rem" :flex-wrap "wrap"}})
          (ks/ChipButton {} 
            (e/fn [] (dom/text "Chip Button")))
          (ks/ChipButton {:data-selected "true"} 
            (e/fn [] (dom/text "Selected Chip")))))
      
      ;; Typography
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Typography")))
        (ks/Heading {:level 1} 
          (e/fn [] (dom/text "Heading Level 1")))
        (ks/Heading {:level 2} 
          (e/fn [] (dom/text "Heading Level 2")))
        (ks/Heading {:level 3} 
          (e/fn [] (dom/text "Heading Level 3")))
        (ks/Paragraph {} 
          (e/fn [] (dom/text "This is a paragraph component with some example text."))))
      
      ;; Form Components
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Form Components")))
        
        ;; Input
        (dom/div
          (dom/props {:style {:margin-top "1rem" :max-width "400px"}})
          (ks/Label {:for "input-example"} 
            (e/fn [] (dom/text "Input Field")))
          (ks/Input {:id "input-example" :placeholder "Enter text here..."} 
            (e/fn [])))
        
        ;; Textarea
        (dom/div
          (dom/props {:style {:margin-top "1rem" :max-width "400px"}})
          (ks/Label {:for "textarea-example"} 
            (e/fn [] (dom/text "Textarea")))
          (ks/Textarea {:id "textarea-example" :placeholder "Enter longer text here..." :rows 4} 
            (e/fn [])))
        
        ;; Checkbox
        (dom/div
          (dom/props {:style {:margin-top "1rem"}})
          (ks/Checkbox {:id "checkbox-example"} 
            (e/fn [] 
              (ks/Label {:for "checkbox-example"} 
                (e/fn [] (dom/text "Checkbox option"))))))
        
        ;; Radio
        (dom/div
          (dom/props {:style {:margin-top "1rem"}})
          (ks/Fieldset {} 
            (e/fn []
              (ks/FieldsetLegend {} 
                (e/fn [] (dom/text "Radio Group")))
              (dom/div
                (ks/Radio {:id "radio-1" :name "radio-group"} 
                  (e/fn [] 
                    (ks/Label {:for "radio-1"} 
                      (e/fn [] (dom/text "Option 1"))))))
              (dom/div
                (ks/Radio {:id "radio-2" :name "radio-group"} 
                  (e/fn [] 
                    (ks/Label {:for "radio-2"} 
                      (e/fn [] (dom/text "Option 2"))))))))))
      
      ;; Card Component
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Card Component")))
        (dom/div
          (dom/props {:style {:margin-top "1rem" :max-width "400px"}})
          (ks/Card {} 
            (e/fn []
              (ks/CardBlock {} 
                (e/fn []
                  (ks/Heading {:level 3} 
                    (e/fn [] (dom/text "Card Title")))
                  (ks/Paragraph {} 
                    (e/fn [] (dom/text "This is a card component with some content inside.")))))))))
      
      ;; Badge Component
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Badge Component")))
        (dom/div
          (dom/props {:style {:display "flex" :gap "1rem" :margin-top "1rem"}})
          (ks/Badge {} 
            (e/fn [] (dom/text "Default")))
          (ks/Badge {:data-color "info"} 
            (e/fn [] (dom/text "Info")))
          (ks/Badge {:data-color "success"} 
            (e/fn [] (dom/text "Success")))
          (ks/Badge {:data-color "warning"} 
            (e/fn [] (dom/text "Warning")))
          (ks/Badge {:data-color "danger"} 
            (e/fn [] (dom/text "Danger")))))
      
      ;; Alert Component
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Alert Component")))
        (dom/div
          (dom/props {:style {:margin-top "1rem" :display "flex" :flex-direction "column" :gap "1rem"}})
          (ks/Alert {} 
            (e/fn [] (dom/text "Default alert message")))
          (ks/Alert {:data-variant "info"} 
            (e/fn [] (dom/text "Info alert message")))
          (ks/Alert {:data-variant "success"} 
            (e/fn [] (dom/text "Success alert message")))
          (ks/Alert {:data-variant "warning"} 
            (e/fn [] (dom/text "Warning alert message")))
          (ks/Alert {:data-variant "danger"} 
            (e/fn [] (dom/text "Danger alert message")))))
      
      ;; Divider
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Divider Component")))
        (ks/Divider {} 
          (e/fn [])))
      
      ;; Tag Component
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Tag Component")))
        (dom/div
          (dom/props {:style {:display "flex" :gap "1rem" :margin-top "1rem"}})
          (ks/Tag {} 
            (e/fn [] (dom/text "Tag 1")))
          (ks/Tag {:data-color "info"} 
            (e/fn [] (dom/text "Tag 2")))
          (ks/Tag {:data-color "success"} 
            (e/fn [] (dom/text "Tag 3")))))
      
      ;; Link Component
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Link Component")))
        (dom/div
          (dom/props {:style {:margin-top "1rem"}})
          (ks/Link {:href "#"} 
            (e/fn [] (dom/text "This is a link component")))))
      
      ;; Avatar Component
      (dom/div
        (dom/props {:style {:margin-top "3rem"}})
        (ks/Heading {:level 2 :data-size "medium"} 
          (e/fn [] (dom/text "Avatar Component")))
        (dom/div
          (dom/props {:style {:display "flex" :gap "1rem" :margin-top "1rem"}})
          (ks/Avatar {} 
            (e/fn [] (dom/text "JD")))
          (ks/Avatar {:data-size "small"} 
            (e/fn [] (dom/text "SM")))
          (ks/Avatar {:data-size "large"} 
            (e/fn [] (dom/text "LG"))))))))
    