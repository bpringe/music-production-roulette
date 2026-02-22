(ns roulette.db)

;; ── Static category definitions (plain data, not stored in app-db) ──

(def categories
  "Ordered vector of category definitions. Order determines display order."
  [{:id    :tempo
    :label "Tempo (BPM)"
    :modes {:exact {:label  "Exact"
                    :values [60 70 80 90 100 110 120 130 140 150 160 170 180]}
            :range {:label  "Range"
                    :values [{:label "Slow"      :range [60 80]}
                             {:label "Medium"    :range [80 110]}
                             {:label "Fast"      :range [110 140]}
                             {:label "Very Fast" :range [140 180]}]}}}

   {:id     :key
    :label  "Key"
    :values ["C" "C#" "D" "Eb" "E" "F" "F#" "G" "Ab" "A" "Bb" "B"]}

   {:id     :scale
    :label  "Scale / Mode"
    :values ["Major (Ionian)" "Dorian" "Phrygian" "Lydian" "Mixolydian"
             "Minor (Aeolian)" "Locrian" "Harmonic Minor" "Melodic Minor"
             "Pentatonic Major" "Pentatonic Minor" "Blues" "Whole Tone"]}

   {:id     :time-sig
    :label  "Time Signature"
    :values ["4/4" "3/4" "6/8" "5/4" "7/8" "2/4"]}

   {:id     :chord-prog
    :label  "Chord Progression"
    :values ["I - IV - V - I"
             "ii - V - I"
             "I - V - vi - IV"
             "I - vi - IV - V"
             "vi - IV - I - V"
             "i - VI - III - VII"
             "i - iv - v"
             "I - IV - vi - V"
             "I - iii - IV - V"
             "ii - IV - V"]}])

(def category-by-id
  "Lookup map: category-id -> category definition."
  (into {} (map (juxt :id identity)) categories))

;; ── Default app-db ──

(def default-db
  {:categories {:tempo    {:enabled? true :mode :exact}
                :key      {:enabled? true}
                :scale    {:enabled? true}
                :time-sig    {:enabled? true}
                :chord-prog {:enabled? true}}
   :results nil})
