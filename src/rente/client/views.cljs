(ns rente.client.views
  (:require [reagent.core :as r]
            [rente.client.ws :as socket]
            [taoensso.sente :as sente]))

(defonce messages (r/atom (sorted-map)))

(defonce counter (r/atom 0))

(defn new-message [content]
  (let [timestamp (.getTime (js/Date.))]
    {:thread-id 42 :content content :timestamp timestamp}))

(defn add-message! [message]
  (swap! messages assoc (:timestamp message) message))

; this function could be improved with assoc-in
(defn update-message! [message]
  (swap! messages dissoc (:client-timestamp message))
  (add-message! message)
  (js/console.log messages))

(defn post-message! [value]
  (when-not (or (= nil @value) (= "" @value))
    (let [content @value
          message (new-message content)
          timestamp (:timestamp message)
          callback (fn [edn-reply]
                    (when-not (sente/cb-success? edn-reply) ; Checks for :chsk/closed, :chsk/timeout, :chsk/error
                      (update-message! edn-reply)))] ; edn reply contains the full message response + client-timestamp 
      (add-message! message)
      (socket/post-message! message callback))
      (reset! value "")
      (js/window.scrollTo 0 (.-scrollHeight (.-body js/document)))))

(def initial-focus-wrapper
  (with-meta identity
    {:component-did-mount #(.focus (r/dom-node %))}))

(defn form-input [value]
  (fn []
    [initial-focus-wrapper
      [:input#form-input
        {:type "text"
         :value @value
         :placeholder "new message"
         :autofocus "autofocus"
         :on-change #(reset! value (-> % .-target .-value))
         :on-key-down #(case (.-which %)
                               13 (post-message! value)
                               nil)}]]))

(defn form-button [input]
  [:input#form-button {:type "button" :value "➤" :on-click #(post-message! input)}])

(defn form []
  (let [input (r/atom "")]
    (fn []
      [:div#form
        [form-input input]
        [form-button input]])))

(defn message-item [message]
  [:div#message
   [:p (:content message)]])

(defonce active-gang (r/atom nil))

(defonce gangs (r/atom (sorted-map)))

(defonce active-thread (r/atom nil))

(defonce threads (r/atom (sorted-map)))

(defn gangs-component []
  [:div#gangs-component "gangs component"])

(defn threads-component []
  [:div#threads-component "threads component"])

(defn messages-component [data]
  [:div#messages-component 
   [:div#messages 
    [:ul#messages-li
     (for [message @messages]
       ^{:key (first message)} [message-item (second message)])]]
   [form]])

(defn main [data]
  [:div
    [:header#header "gang"]
    [:div#main-container
      [gangs-component]
      [threads-component]
      [messages-component data]]])

