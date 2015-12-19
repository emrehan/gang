(ns rente.client.views
  (:require [reagent.core :as r]
            [rente.client.ws :as socket]
            [taoensso.sente :as sente]))

(defonce messages (r/atom (sorted-map)))

(defonce counter (r/atom 0))

(defn new-message [text]
  (let [timestamp (.getTime (js/Date.))]
    {:text text :client-timestamp timestamp :timestamp timestamp :acknowledged? false}))

(defn add-message! [message]
  (swap! messages assoc (:timestamp message) message))

; this function could be improved with assoc-in
(defn update-message! [message]
  (swap! messages dissoc (:client-timestamp message))
  (add-message! message))

(defn post-message! [value]
  (let [text @value
        message (new-message text)
        timestamp (:timestamp message)
        callback (fn [edn-reply]
                  (println "edn-reply is " edn-reply)
                  (when-not (sente/cb-success? edn-reply) ; Checks for :chsk/closed, :chsk/timeout, :chsk/error
                    (update-message! edn-reply)))] ; edn reply contains the full message response + client-timestamp 
    (add-message! message)
    (socket/post-message! message callback))
    (reset! value ""))

(def initial-focus-wrapper
  (with-meta identity
    {:component-did-mount #(.focus (r/dom-node %))}))

(defn input-box [value]
  (fn []
    [initial-focus-wrapper
      [:input {:type "text"
               :value @value
               :placeholder "new message"
               :autofocus "autofocus"
               :on-change #(reset! value (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                                     13 (post-message! value)
                                     nil)}]]))

(defn bottom-bar []
  (let [input (r/atom "")]
    [:div
     [input-box input]
     [:input {:type "button" :value "➤" :on-click #(post-message! input)}]]))

(defn message-item [message]
  [:div
   [:p (:text message)]])

(defn main [data]
  [:div
   [:h1 (:title @data)]
   [:div "gang is coming!"]
   [:br]
  [:div 
   [:ul#messages
    (for [message @messages]
      ^{:key (first message)} [message-item (second message)])]]
  [:div
   [bottom-bar]]])
