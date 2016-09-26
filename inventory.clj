#!/usr/bin/env boot

(set-env! :dependencies '[[pieterbreed/ansible-inventory-clj "0.1.1"]
                          [environ "1.0.3"]
                          [org.clojure/data.json "0.2.6"]])

;; ----------------------------------------

(require '[ansible-inventory-clj.core :as yinv])
(require '[environ.core :as env])
(require '[clojure.data.json :as json])

;; ----------------------------------------

(let [vagrant-host ["vagrant" (env/env :houstan-hostname) 22]] 
  (as-> yinv/empty-inventory $
    (yinv/add-target $ vagrant-host {"ansible_user" "vagrant"
                                     "ansible_host" (env/env :houstan-hostname)})
    (yinv/add-target-to-group $ vagrant-host "vagrant-hosts")
    (yinv/add-target-to-group $ vagrant-host "java-machines")
    (yinv/make-ansible-list $)
    (json/write-str $)
    (println $)))
