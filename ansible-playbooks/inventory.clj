#!/usr/bin/env boot

(set-env! :dependencies '[[pieterbreed/yoostan-lib "0.0.1-SNAPSHOT"]
                          [environ "1.0.3"]
                          [org.clojure/data.json "0.2.6"]])

;; ----------------------------------------

(require '[yoostan-lib.inventory :as yinv])
(require '[environ.core :as env])
(require '[clojure.data.json :as json])

;; ----------------------------------------

(let [vagrant-host ["vagrant" (env/env :houstan-hostname) 22]] 
  (as-> yinv/empty-inventory $
    (yinv/target-> $ vagrant-host {"ansible_user" "vagrant"
                                   "ansible_host" (env/env :houstan-hostname)})
    (yinv/target->group-> $ vagrant-host "vagrant-hosts")
    (yinv/target->group-> $ vagrant-host "java-machines")
    (yinv/inv->ansible--list $)
    (json/write-str $)
    (println $)))
