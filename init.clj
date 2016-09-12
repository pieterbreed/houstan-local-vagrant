#!/usr/bin/env boot

(set-env! :dependencies '[[pieterbreed/tappit "0.9.8"]
                          [pieterbreed/yoostan-lib "0.0.1-SNAPSHOT"]
                          [me.raynes/conch "0.8.0"]
                          [environ "1.0.3"]])


;; ----------------------------------------

;; warnings, drama. we need an unreleased version of clojure for this script
(require 'environ.core)
(when (not (re-find #"1\.9\.0"
                    (environ.core/env :boot-clojure-version)))
  (println "# Set ENV variables like this:")
  (println "# $ export BOOT_CLOJURE_VERSION=1.9.0-alpha10")
  (println "Bail out! Requires BOOT_CLOJURE_VERSION=1.9.0-alpha10 or higher")
  (System/exit 0))

;; ----------------------------------------

(require '[tappit.producer :refer [with-tap! ok]])
(require '[me.raynes.conch :as conch])
(require 'boot.core)
(require 'yoostan-lib.utils)

;; ----------------------------------------

(let [bsf (-> boot.core/*boot-script* clojure.java.io/file)]
  (def vagrantfile-dir
    (-> (or (if (.isAbsolute bsf) bsf)
            (clojure.java.io/file (System/getProperty "user.dir")
                                  bsf))
        .getParentFile
        .getCanonicalPath)))

;; ----------------------------------------

(with-tap!

  ;; ----------------------------------------

  (defn bail [ll]
    "Causes this script to terminate with all lines in ll printed via diag!, the first line of ll being called with bail-out! and System/exit afterwards."
    (->> ll (map diag!) dorun)
    (bail-out! (first ll))
    (System/exit 1))

  (defn diag-lines [ll] (->> ll (map diag!) dorun))

  ;; ----------------------------------------

  (if (not (ok! (yoostan-lib.utils/cmd-is-available "vagrant")
                "vagrant-is-installed"))
    (bail ["vagrant cannot be found"
           "`vagrant` has to be discoverable by `which`"]))

  (if (not (ok! (some-> :houstan-ip
                        environ.core/env)
                "ip-addr-is-set"))
    (bail ["HOUSTAN_IP not set"
           "Set the HOUSTAN_IP env variable to the required vagrant ip address."]))

  (if (not (ok! (some-> :houstan-hostname
                        environ.core/env)
                "hostname-is-set"))
    (bail ["HOUSTAN_HOSTNAME not set."
           "Set the HOUSTAN_HOSTNAME env variable to the required vagrant hostname."]))

  ;; ----------------------------------------

  (diag! (str "Vagrant working directory at: " vagrantfile-dir))
  (diag! "Calling `vagrant status`...")

  (conch/with-programs [vagrant]

    (defn get-vagrant-status
      []
      (let [vagrant-output (vagrant "status" "--machine-readable"
                                    {:seq true
                                     :throw true
                                     :verbose false
                                     :dir vagrantfile-dir})]
        (diag! "vagrant status output:")
        (diag-lines vagrant-output)
        
        ;; get status line
        ;; re-matches returns a vector, the second item in the vector
        ;; will be the match of (.+) in the regex, ie the status
        (->> vagrant-output
             (map #(re-matches #"\d+,default,state,(.+)" %))
             (filter #(not (nil? %)))
             first
             second)))

    ;; check if status is not_created, before calling up
    (if (=! "not_created" (get-vagrant-status)
            "start-status-is-not-created")
      (do
        (diag! "Calling `vagrant up`...")
        (diag-lines (vagrant "up"
                             {:seq true
                              :throw true
                              :verbose false
                              :dir vagrantfile-dir})))
      (diag-lines ["start-status-is-not-created"
                   "The init script assumes that the vagrant machine needs"
                   "to be created from scratch. "
                   "Therefore, not_created is the only valid status to start with."]))

    (=! "running" (get-vagrant-status)
        "end-status-is-running")))
