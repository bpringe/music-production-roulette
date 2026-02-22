(ns roulette.subs
  (:require [re-frame.core :as rf]))

;; Layer 2: extractors

(rf/reg-sub
  :roulette/categories
  (fn [db _]
    (:categories db)))

(rf/reg-sub
  :roulette/results
  (fn [db _]
    (:results db)))

;; Layer 3: derived

(rf/reg-sub
  :roulette/category-enabled?
  :<- [:roulette/categories]
  (fn [categories [_ category-id]]
    (get-in categories [category-id :enabled?])))

(rf/reg-sub
  :roulette/tempo-mode
  :<- [:roulette/categories]
  (fn [categories _]
    (get-in categories [:tempo :mode])))

(rf/reg-sub
  :roulette/any-enabled?
  :<- [:roulette/categories]
  (fn [categories _]
    (boolean (some :enabled? (vals categories)))))