(ns electric-starter-app.main
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.oscilloscope :as oscilloscope]
            [com.itonomi.komponentkassen.shell :as ks]))

;; Example components to showcase in the gallery
(e/defn ButtonExample []
  (ks/Button {}
             (e/fn []
               (dom/text "Komponentkassen Button")
               (let [[t _] (e/Token (dom/On "click" identity nil))]
                 (when t
                   (js/alert "Button clicked!")
                   (t))))))

(e/defn CardExample []
  (ks/Card {}
           (e/fn []
             (ks/CardBlock {}
                           (e/fn []
                             (ks/Heading {:level 3}
                                         (e/fn [] (dom/text "Example Card")))
                             (ks/Paragraph {}
                                           (e/fn [] (dom/text "This is a card component from Komponentkassen."))))))))

(e/defn InputExample []
  (let [!value (atom "")
        value (e/watch !value)]
    (ks/Field {}
              (e/fn []
                (ks/Label {}
                          (e/fn [] (dom/text "Example Input")))
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
                  (ks/FieldDescription {}
                                       (e/fn [] (dom/text "You typed: " value))))))))

(e/defn AlertExample []
  (ks/Alert {:data-color "info"}
            (e/fn []
              (dom/text "This is an informational alert from Komponentkassen."))))

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
                      :description "A button component from the Komponentkassen design system"
                      :category "Buttons"
                      :Component ButtonExample}
                     
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
                      :description "An alert component for displaying messages"
                      :category "Feedback"
                      :Component AlertExample}
                     
                     {:id "heading-1"
                      :title "Headings"
                      :description "Different heading levels"
                      :category "Typography"
                      :Component (e/fn []
                                  (dom/div
                                   (ks/Heading {:level 1}
                                               (e/fn [] (dom/text "Heading Level 1")))
                                   (ks/Heading {:level 2}
                                               (e/fn [] (dom/text "Heading Level 2")))
                                   (ks/Heading {:level 3}
                                               (e/fn [] (dom/text "Heading Level 3")))))}
                     
                     {:id "link-1"
                      :title "Link"
                      :description "A link component"
                      :category "Navigation"
                      :Component (e/fn []
                                  (ks/Link {:href "#"}
                                           (e/fn [] (dom/text "This is a link"))))}]})))))

(defn electric-boot [ring-request]
  #?(:clj  (e/boot-server {} Main (e/server ring-request))
     :cljs (e/boot-client {} Main (e/server (e/amb)))))