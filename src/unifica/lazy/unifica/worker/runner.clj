(ns unifica.lazy.unifica.worker.runner
  (:require [com.biffweb.task-runner.lazy :as lazy]))

(lazy/refer-many unifica.worker.runner [use-jetty use-temporal])
