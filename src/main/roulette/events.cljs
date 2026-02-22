(ns roulette.events
  (:require [cljs.reader :as reader]
            [re-frame.core :as rf]
            [roulette.db :as db]))

(def ls-key "music-production-roulette")

(defn- save-db! [db]
  (.setItem js/localStorage ls-key (pr-str db)))

(defn- load-db []
  (when-let [stored (.getItem js/localStorage ls-key)]
    (reader/read-string stored)))

(def persist
  (rf/after (fn [db _] (save-db! db))))

(defn- roll-category
  "Given a category definition and its runtime state, return a random result string."
  [cat-def cat-state]
  (if (:modes cat-def)
    ;; Tempo with modes
    (let [mode     (:mode cat-state :exact)
          mode-def (get-in cat-def [:modes mode])
          chosen   (rand-nth (:values mode-def))]
      (if (map? chosen)
        (str (:label chosen) " (" (first (:range chosen)) "-" (second (:range chosen)) " BPM)")
        (str chosen " BPM")))
    ;; All other categories: flat values vector
    (rand-nth (:values cat-def))))

(rf/reg-event-db
  :roulette/initialize
  (fn [_ _]
    (or (load-db) db/default-db)))

(rf/reg-event-db
  :roulette/toggle-category
  [persist]
  (fn [db [_ category-id]]
    (update-in db [:categories category-id :enabled?] not)))

(rf/reg-event-db
  :roulette/set-tempo-mode
  [persist]
  (fn [db [_ mode]]
    (assoc-in db [:categories :tempo :mode] mode)))

(rf/reg-event-db
  :roulette/roll
  [persist]
  (fn [db _]
    (let [enabled-ids (->> (:categories db)
                           (filter (fn [[_ v]] (:enabled? v)))
                           (map first))]
      (assoc db :results
             (into {}
                   (map (fn [cat-id]
                          [cat-id (roll-category
                                    (get db/category-by-id cat-id)
                                    (get-in db [:categories cat-id]))]))
                   enabled-ids)))))
