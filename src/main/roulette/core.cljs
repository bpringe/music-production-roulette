(ns roulette.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [roulette.events]
            [roulette.subs]
            [roulette.views :as views]))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (rdom/render [views/app]
               (.getElementById js/document "app")))

(defn init []
  (rf/dispatch-sync [:roulette/initialize])
  (mount-root))
