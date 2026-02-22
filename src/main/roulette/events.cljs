(ns roulette.events
  (:require [re-frame.core :as rf]
            [roulette.db :as db]))

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
    db/default-db))

(rf/reg-event-db
  :roulette/toggle-category
  (fn [db [_ category-id]]
    (update-in db [:categories category-id :enabled?] not)))

(rf/reg-event-db
  :roulette/set-tempo-mode
  (fn [db [_ mode]]
    (assoc-in db [:categories :tempo :mode] mode)))

(rf/reg-event-db
  :roulette/roll
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
