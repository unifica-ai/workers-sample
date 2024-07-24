(ns unifica.lazy.temporal.client.core
    (:require [com.biffweb.task-runner.lazy :as lazy]))

(lazy/refer-many temporal.client.core [create-workflow start get-result])
