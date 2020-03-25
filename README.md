# Glögi

<!-- badges -->
[![CircleCI](https://circleci.com/gh/lambdaisland/glogi.svg?style=svg)](https://circleci.com/gh/lambdaisland/glogi) [![cljdoc badge](https://cljdoc.org/badge/lambdaisland/glogi)](https://cljdoc.org/d/lambdaisland/glogi) [![Clojars Project](https://img.shields.io/clojars/v/lambdaisland/glogi.svg)](https://clojars.org/lambdaisland/glogi)
<!-- /badges -->

A thin wrapper around `goog.log` inspired by `pedestal.log`.

For more info see the accompanying blog post: [ClojureScript logging with goog.log](https://lambdaisland.com/blog/2019-06-10-goog-log)

Many thanks to [Nextjournal](https://nextjournal.com/) for coming up with an interesting problem, and giving me the opportunity to explore and solve it.

## Installation

deps.edn

```
lambdaisland/glogi {:mvn/version "0.0-29"}
```

project.clj

```
[lambdaisland/glogi "0.0-29"]
```

## Quickstart

```clojure
(ns my.app
  (:require [lambdaisland.glogi :as log]
            [lambdaisland.glogi.console :as glogi-console]))

(defonce install-logger
  (glogi-console/install!)))

(log/info :hello {:message "Hello, world!"})
```



<!-- license-epl -->
## License

Copyright &copy; 2019 Arne Brasseur

Available under the terms of the Eclipse Public License 1.0, see LICENSE.txt
<!-- /license-epl -->
