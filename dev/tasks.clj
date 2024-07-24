(ns tasks
  (:require
   [clojure.tools.logging :as log]
   [unifica.lazy.com.biffweb.config :as config]
   [unifica.lazy.unifica.temporal :as ut]
   [unifica.worker.sample :as sample]
   [unifica.worker.tasks :as t]))

(def ^:private config (delay (config/use-aero-config {:biff.config/skip-validation true
                                                    ;;  :biff.config/profile "prod"
                                                      })))

(defn meaning-of-life
  "Run sample workflow"
  []
  (let [ctx ((ut/use-temporal {}) @config)
        w (ut/create-workflow ctx sample/get-sum {:task-queue ::sample/queue})
        _ (ut/start w {:n 2})
        res @(ut/get-result w)]
    (log/info "result:" res)
    (doseq [f (:biff/stop ctx)]
      (log/info "stopping:" (str f))
      (f))))

(def extra-tasks {"meaning-of-life" #'meaning-of-life})

(def tasks (merge t/tasks extra-tasks))
