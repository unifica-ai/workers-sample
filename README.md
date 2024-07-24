# Sample Worker

This sample worker shows how to run and schedule workflows with Temporal using Clojure.

The project layout and some modules (config, XTDB) are from Biff. The objective
is to use this scaffold for runner code that does not have any UI stuff. It is
easier to do this than to use Biff with a ton of exclusions.

There are two workflows: one says "hello" every minute, and the other one runs
on demand and performs a calculation.

## Run locally

Install Temporal, then run 

``` shell
temporal server start-dev
```

Then start this project.

```shell
clj -M:dev dev
```

You will see this in the logs once a minute:

```
INFO unifica.worker.sample - Hello
```

You can also see the HTTP endpoint

```
curl localhost:8080
OK
```

## Meaning of life

Youc an ask this worker for the meaning of life, the universe, and everything. 

This adds two to the value of `:unifica.worker.sample/origin` in `resources/config.edn`.

You can do this from the shell using this command

```
clj -M:dev meaning-of-life
```

Then see the result in the logs:

```
[main] INFO tasks - result: 42
```

## REPL

You can connect to the REPL, and go to `dev/repl.clj`. You can execute a workflow there and see the result.

In `sample.clj`, look for the "REPL Zone" comments.

Try to get a new meaning of life value by changing `:unifica.worker.sample/origin`, then executing `(refresh)` 
in one o the REPL Zone comments.

## Temporal Cloud

To use with Temporal Cloud, fill out `TEMPORAL_PROD_TARGET` and `TEMPORAL_PROD_NAMESPACE` in `config.env`, 
and provide certificates in the `TEMPORAL_CLIENT_CERT_PATH`.

