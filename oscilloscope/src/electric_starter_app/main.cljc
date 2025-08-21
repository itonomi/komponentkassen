(ns electric-starter-app.main
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [com.itonomi.oscilloscope :as oscilloscope]
            [com.itonomi.komponentkassen.shell2 :as ks2 :include-macros true]
            [com.itonomi.komponentkassen.aksel-icons :as icons]))

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
                                    (dom/text "This is a link")))}
                     
                     ;; Icon examples
                     {:id "icon-airplane"
                      :title "Airplane Icon"
                      :description "Airplane icon from Aksel icons"
                      :category "Icons"
                      :parameters [{:name :size
                                    :label "Size"
                                    :type :select
                                    :options ["1em" "1.5em" "2em" "3em" "4em"]
                                    :default "2em"}
                                   {:name :color
                                    :label "Color"
                                    :type :select
                                    :options ["currentColor" "#3b82f6" "#ef4444" "#10b981" "#f59e0b"]
                                    :default "currentColor"}]
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:font-size (:size params)
                                                       :color (:color params)}})
                                   (icons/Airplane)))}
                     
                     {:id "icon-heart"
                      :title "Heart Icon"
                      :description "Heart icon (regular and filled)"
                      :category "Icons"
                      :parameters [{:name :filled
                                    :label "Filled"
                                    :type :checkbox
                                    :default false}
                                   {:name :size
                                    :label "Size"
                                    :type :select
                                    :options ["1em" "1.5em" "2em" "3em" "4em"]
                                    :default "2em"}
                                   {:name :color
                                    :label "Color"
                                    :type :select
                                    :options ["currentColor" "#ef4444" "#ec4899" "#f59e0b"]
                                    :default "#ef4444"}]
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:font-size (:size params)
                                                       :color (:color params)}})
                                   (if (:filled params)
                                     (icons/HeartFill)
                                     (icons/Heart))))}
                     
                     {:id "icon-bell"
                      :title "Bell Icon"
                      :description "Bell notification icons"
                      :category "Icons"
                      :parameters [{:name :variant
                                    :label "Variant"
                                    :type :select
                                    :options ["regular" "filled" "dot" "dot-filled"]
                                    :default "regular"}
                                   {:name :size
                                    :label "Size"
                                    :type :select
                                    :options ["1em" "1.5em" "2em" "3em"]
                                    :default "1.5em"}]
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:font-size (:size params)}})
                                   (case (:variant params)
                                     "regular" (icons/Bell)
                                     "filled" (icons/BellFill)
                                     "dot" (icons/BellDot)
                                     "dot-filled" (icons/BellDotFill)
                                     (icons/Bell))))}
                     
                     {:id "icon-collection"
                      :title "Icon Collection Sample"
                      :description "A collection of various icons"
                      :category "Icons"
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:display "flex"
                                                       :gap "1rem"
                                                       :flex-wrap "wrap"
                                                       :font-size "2em"}})
                                   (icons/House)
                                   (icons/Person)
                                   (icons/Cog)
                                   (icons/Calendar)
                                   (icons/EnvelopeClosed)
                                   (icons/Phone)
                                   (icons/MagnifyingGlass)
                                   (icons/ShoppingBasket)
                                   (icons/Checkmark)
                                   (icons/XMark)))}
                     
                     {:id "icon-arrows"
                      :title "Arrow Icons"
                      :description "Various arrow and navigation icons"
                      :category "Icons"
                      :parameters [{:name :size
                                    :label "Size"
                                    :type :select
                                    :options ["1em" "1.5em" "2em" "3em"]
                                    :default "2em"}]
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:display "flex"
                                                       :gap "1rem"
                                                       :flex-wrap "wrap"
                                                       :font-size (:size params)}})
                                   (icons/ArrowUp)
                                   (icons/ArrowDown)
                                   (icons/ArrowLeft)
                                   (icons/ArrowRight)
                                   (icons/ChevronUp)
                                   (icons/ChevronDown)
                                   (icons/ChevronLeft)
                                   (icons/ChevronRight)
                                   (icons/CaretUp)
                                   (icons/CaretDown)))}
                     
                     {:id "icon-files"
                      :title "File & Document Icons"
                      :description "File types and document related icons"
                      :category "Icons"
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:display "flex"
                                                       :gap "1rem"
                                                       :flex-wrap "wrap"
                                                       :font-size "2em"
                                                       :color "#374151"}})
                                   (icons/File)
                                   (icons/FileFill)
                                   (icons/FileText)
                                   (icons/FilePdf)
                                   (icons/FileImage)
                                   (icons/FileCode)
                                   (icons/Folder)
                                   (icons/FolderFill)
                                   (icons/FolderPlus)
                                   (icons/Files)))}
                     
                     {:id "icon-transport"
                      :title "Transportation Icons"
                      :description "Various transportation and vehicle icons"
                      :category "Icons"
                      :parameters [{:name :filled
                                    :label "Filled versions"
                                    :type :checkbox
                                    :default false}]
                      :Component (e/fn [params]
                                  (dom/div
                                   (dom/props {:style {:display "flex"
                                                       :gap "1rem"
                                                       :flex-wrap "wrap"
                                                       :font-size "2em"
                                                       :color "#3b82f6"}})
                                   (if (:filled params)
                                     (icons/AirplaneFill)
                                     (icons/Airplane))
                                   (if (:filled params)
                                     (icons/CarFill)
                                     (icons/Car))
                                   (if (:filled params)
                                     (icons/BusFill)
                                     (icons/Bus))
                                   (if (:filled params)
                                     (icons/BoatFill)
                                     (icons/Boat))
                                   (icons/Bicycle)
                                   (if (:filled params)
                                     (icons/MotorcycleFill)
                                     (icons/Motorcycle))))}]})))))

(defn electric-boot [ring-request]
  #?(:clj  (e/boot-server {} Main (e/server ring-request))
     :cljs (e/boot-client {} Main (e/server (e/amb)))))