(ns rente.client.views
  (:require [reagent.core :as r]
            [rente.client.ws :as socket]))

(defn header [data]
 [:h1 (:title @data)])

(defn post-message! [value]
  (socket/post-message! @value)
  (reset! value ""))

(def initial-focus-wrapper
  (with-meta identity
    {:component-did-mount #(.focus (r/dom-node %))}))

(defn input-box [value]
  (fn []
    [initial-focus-wrapper
      [:input {:type "text"
               :value @value
               :autofocus "autofocus"
               :on-change #(reset! value (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                                     13 (post-message! value)
                                     nil)}]]))

(defn bottom-bar []
  (let [input (r/atom "")]
    [:div
     [input-box input]
     [:input {:type "button" :value ">" :on-click #(post-message! input)}]]))

(defn main [data]
  [:div
   [header data]
   [:div "gang is coming!"]
   [:br]
   [:button {:on-click socket/test-socket-callback} "Send Message Callback"]
   [:text "\t"]
   [:button {:on-click socket/test-socket-event} "Send Message Event"]
   [:br]
  [:div
   [bottom-bar]]])
