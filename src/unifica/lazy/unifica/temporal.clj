(ns unifica.lazy.unifica.temporal
  (:require [com.biffweb.task-runner.lazy :as lazy]))

(lazy/refer-many unifica.temporal [use-temporal create-workflow start get-result])
