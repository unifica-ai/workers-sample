(ns unifica.worker.sample
  (:require
   [clojure.tools.logging :as log]
   [com.biffweb.config :as config]
   [nrepl.cmdline :as nrepl-cmd]
   [temporal.activity :refer [defactivity] :as ta]
   [temporal.workflow :refer [defworkflow] :as tw]
   [unifica.temporal :as temporal]
   [ring.adapter.jetty9 :as jetty]
   [clojure.tools.namespace.repl :as tn-repl])
  (:gen-class))

(defn handler [_req]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "OK"})

;; Scheduled workflow
(defactivity say-hi [ctx args]
  (log/info "Hello"))

(defworkflow greet [args]
  @(ta/invoke say-hi args))

;; On-demand workflow
;;
;; See resources/config.edn for origin
(defactivity add-n [{:keys [unifica.worker.sample/origin] :or {origin 0}} {:keys [n]}]
  (+ origin n))

(defworkflow get-sum [args]
 @(ta/invoke add-n args))

(def modules
  [{:tasks {:ping {:schedule {:trigger-immediately? false}
                   :state {:paused? false}
                   :policy {:pause-on-failure? true}
                   :spec {:cron-expressions ["* * * * *"] ;; every minute
                          :timezone "US/Central"}
                   :action {:arguments {}
                            :workflow-type greet
                            :options {:workflow-id "greet"
                                      :task-queue ::queue}}}}}])


;; Cloud Run requires an HTTP endpint
(defn use-jetty [{:app/keys [host port handler] :as ctx}]
  (let [server (jetty/run-jetty handler
                   {:host host
                    :port port
                    :join? false})]
    (log/info "Jetty running on port" (str "http://" host ":" port))
    (update ctx :biff/stop conj #(jetty/stop-server server))))

(def components
  [config/use-aero-config
   (temporal/use-temporal {:worker-options [{:task-queue ::queue}]
                           :use-schedules true})
   use-jetty])

(def initial-system
  {:app/modules #'modules
   :biff.config/skip-validation true
   :app/handler handler})

(defonce system (atom {}))

;; REPL zone
(comment
  (let [w (temporal/create-workflow @system get-sum {:task-queue ::queue})
        _ (temporal/start w {:n 2})]
    @(temporal/get-result w))
  )

(defn start []
  (let [new-system (reduce (fn [system component]
                             (log/info "Starting " (str component))
                             (component system))
                           initial-system
                           components)]
    (log/info "System started")
    (reset! system new-system)))

;; From Biff
(defn refresh []
  (doseq [f (:biff/stop @system)]
    (log/info "stopping:" (str f))
    (f))
  (tn-repl/refresh :after `start)
  :done)

;; REPL zone
(comment
  ;; If you change anything in config.edn, refresh here!
  ;; This will stop Jetty and Temporal, re-read the config files, then
  ;; start them up again.
  (refresh)

  )

(defn -main []
  (let [{:keys [nrepl/args]} (start)]
    (apply nrepl-cmd/-main args)))
