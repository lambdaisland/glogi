# Glögi

<!-- badges -->
[![CircleCI](https://circleci.com/gh/lambdaisland/glogi.svg?style=svg)](https://circleci.com/gh/lambdaisland/glogi) [![cljdoc badge](https://cljdoc.org/badge/lambdaisland/glogi)](https://cljdoc.org/d/lambdaisland/glogi) [![Clojars Project](https://img.shields.io/clojars/v/lambdaisland/glogi.svg)](https://clojars.org/lambdaisland/glogi)
<!-- /badges -->

A thin wrapper around `goog.log` inspired by `pedestal.log`.

For more info see the accompanying blog post: [ClojureScript logging with goog.log](https://lambdaisland.com/blog/2019-06-10-goog-log)

Many thanks to [Nextjournal](https://nextjournal.com/) for coming up with an interesting problem, and giving me the opportunity to explore and solve it.

<!-- opencollective -->
### Support Lambda Island Open Source

If you find value in our work please consider [becoming a backer on Open Collective](http://opencollective.com/lambda-island#section-contribute)
<!-- /opencollective -->

## Installation

deps.edn

```
lambdaisland/glogi {:mvn/version "0.0-33"}
```

project.clj

```
[lambdaisland/glogi "0.0-33"]
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

Before you can start logging you need to install a handler that knows where to
output the log messages (browser console, in a div, ...).
`(glogi-console/install!)` is recommended. It contains some smarts so that your
Clojure data is logged nicely. When cljs-devtools is active then it will pass
data structures unchanged to `js/console.log` so devtools can render them. If
not then it will stringify them so you get regular EDN in your console, instead
of seeing the implementation details of ClojureScript persistent data
structures.

Log functions take key/value pairs.

## Loggers and log levels

``` clojure
(log/set-levels
  {:glogi/root   :info    ;; Set a root logger level, this will be inherited by all loggers
   'my.app.thing :trace   ;; Some namespaces you might want detailed logging
   'my.app.other :error   ;; or for others you only want to see errors.
   })
```

<!-- contributing -->
### Contributing

Everyone has a right to submit patches to this projects, and thus become a contributor.

Contributors MUST

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem. Start by stating the problem, then supply a minimal solution. `*`
- agree to license their contributions as MPLv2.
- not break the contract with downstream consumers. `**`
- not break the tests.

Contributors SHOULD

- update the CHANGELOG and README.
- add tests for new functionality.

If you submit a pull request that adheres to these rules, then it will almost
certainly be merged immediately. However some things may require more
consideration. If you add new dependencies, or significantly increase the API
surface, then we need to decide if these changes are in line with the project's
goals. In this case you can start by [writing a
pitch](https://nextjournal.com/lambdaisland/pitch-template), and collecting
feedback on it.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves, then supply a minimal solution.

`**` As long as this project has not seen a public release (i.e. is not on Clojars)
we may still consider making breaking changes, if there is consensus that the
changes are justified.
<!-- /contributing -->

<!-- license-epl -->
## License

Copyright &copy; 2019-2020 Arne Brasseur and Contributors

Available under the terms of the Eclipse Public License 1.0, see LICENSE.txt
<!-- /license-epl -->
