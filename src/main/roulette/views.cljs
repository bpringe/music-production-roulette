(ns roulette.views
  (:require [re-frame.core :as rf]
            [roulette.db :as db]))

(defn tempo-mode-toggle []
  (let [mode @(rf/subscribe [:roulette/tempo-mode])]
    [:div {:class "flex gap-2 mt-2"}
     [:button {:class (str "px-3 py-1 rounded text-sm font-medium transition-colors "
                           (if (= mode :exact)
                             "bg-indigo-600 text-white"
                             "bg-gray-200 text-gray-600 hover:bg-gray-300"))
               :on-click #(rf/dispatch [:roulette/set-tempo-mode :exact])}
      "Exact"]
     [:button {:class (str "px-3 py-1 rounded text-sm font-medium transition-colors "
                           (if (= mode :range)
                             "bg-indigo-600 text-white"
                             "bg-gray-200 text-gray-600 hover:bg-gray-300"))
               :on-click #(rf/dispatch [:roulette/set-tempo-mode :range])}
      "Range"]]))

(defn category-toggle [cat-def]
  (let [cat-id   (:id cat-def)
        enabled? @(rf/subscribe [:roulette/category-enabled? cat-id])]
    [:div {:class (str "flex items-center justify-between p-4 rounded-lg border-2 transition-colors "
                       (if enabled?
                         "border-indigo-500 bg-indigo-50"
                         "border-gray-200 bg-white"))}
     [:div
      [:span {:class "font-medium text-gray-900"} (:label cat-def)]
      (when (and (= cat-id :tempo) enabled?)
        [tempo-mode-toggle])]
     [:button {:class (str "relative inline-flex h-6 w-11 shrink-0 items-center rounded-full transition-colors cursor-pointer "
                           (if enabled? "bg-indigo-600" "bg-gray-300"))
               :on-click #(rf/dispatch [:roulette/toggle-category cat-id])
               :role "switch"
               :aria-checked (str enabled?)}
      [:span {:class (str "inline-block h-4 w-4 rounded-full bg-white shadow transform transition-transform "
                          (if enabled? "translate-x-6" "translate-x-1"))}]]]))

(defn categories-panel []
  [:div {:class "space-y-3"}
   (for [cat-def db/categories]
     ^{:key (:id cat-def)}
     [category-toggle cat-def])])

(defn roll-button []
  (let [any-enabled? @(rf/subscribe [:roulette/any-enabled?])]
    [:button {:class    (str "w-full py-4 rounded-xl text-xl font-bold transition-all "
                             (if any-enabled?
                               "bg-indigo-600 hover:bg-indigo-700 text-white cursor-pointer shadow-lg hover:shadow-xl active:scale-95"
                               "bg-gray-300 text-gray-500 cursor-not-allowed"))
              :disabled (not any-enabled?)
              :on-click #(rf/dispatch [:roulette/roll])}
     "Roll!"]))

(defn result-card [cat-id result-text]
  (let [cat-def (get db/category-by-id cat-id)]
    [:div {:class "bg-white rounded-lg p-4 shadow text-center"}
     [:div {:class "text-sm text-gray-500 mb-1"} (:label cat-def)]
     [:div {:class "text-2xl font-bold text-indigo-700"} result-text]]))

(defn results-panel []
  (let [results @(rf/subscribe [:roulette/results])]
    (when results
      [:div {:class "mt-8"}
       [:h2 {:class "text-lg font-semibold text-gray-900 mb-4"} "Your Constraints"]
       [:div {:class "grid grid-cols-1 sm:grid-cols-2 gap-4"}
        (for [[cat-id text] results]
          ^{:key cat-id}
          [result-card cat-id text])]])))

(defn app []
  [:div {:class "min-h-screen bg-gray-100"}
   [:div {:class "max-w-md mx-auto px-4 py-8"}
    [:h1 {:class "text-3xl font-bold text-center text-gray-900 mb-8"}
     "Music Production Roulette"]
    [categories-panel]
    [:div {:class "mt-6"} [roll-button]]
    [results-panel]]])
