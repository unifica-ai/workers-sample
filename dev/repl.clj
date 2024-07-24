(ns repl
  (:require [unifica.worker.sample :as main]
            [temporal.client.core :as tc]
            [temporal.client.schedule :as ts])
  (:import (io.temporal.client.schedules ScheduleException)))


(comment

  (+ 1 1)

  ;; Run workflow
  ;; Should be 42
  (let [client (:temporal/client @main/system)
        w (tc/create-workflow client main/get-sum {:task-queue ::main/queue})
        _ (tc/start w {:n 2})]
    @(tc/get-result w))

)
