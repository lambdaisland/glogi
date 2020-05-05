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
lambdaisland/glogi {:mvn/version "1.0-60"}
```

project.clj

```
[lambdaisland/glogi "1.0-60"]
```

## Quickstart

It is recommended to initialize Glögi at the top of your main namespace, so that
no log messages are lost.

```clojure
(ns my.app
  (:require [lambdaisland.glogi :as log]
            [lambdaisland.glogi.console :as glogi-console]))

(glogi-console/install!)

(log/set-levels
  {:glogi/root   :info    ;; Set a root logger level, this will be inherited by all loggers
   'my.app.thing :trace   ;; Some namespaces you might want detailed logging
   'my.app.other :error   ;; or for others you only want to see errors.
   })

(log/info :hello {:message "Hello, world!"})
```

Result in your browser console (but pretty with colors):

```
[my.app] {:hello {:message "Hello, world!"}}
```

Before you can start logging you need to install a handler that knows where to
output the log messages (browser console, in a div, ...).
`(glogi-console/install!)` is recommended. It contains some smarts so that your
Clojure data is logged nicely. When cljs-devtools is active then it will pass
data structures unchanged to `js/console.log` so devtools can render them. If
not then it will stringify and colorize them so you get pretty EDN in your
console, instead of seeing the implementation details of ClojureScript
persistent data structures.

Log functions take key/value pairs.

## Loggers and log levels

In `goog.log`, which glogi is based on, loggers have hierarchical names, with
segments separated by dots. If you use Glogi's logging macros then it will
automatically get the logger based on the namespace name.

When you call a logging function it will check the log level of the logger to
decide if the message should be output ot not. To find this level the logger
will go up the hierarchy until it finds a logger with an explicit level set.
This is usually the root logger, but it doesn't have to be.

So say you have these namespaces:

```
my.app.ui.subs
my.app.ui.events
my.app.api
my.app.api.routes
some.lib.config
some.lib.print
```

You can set the level for individual namespaces

``` clojure
(log/set-levels '{my.app.ui.subs :debug
                  my.app.ui.events :config
                  ...})
```

But you can also set the level for a subtree of namespaces. So say you're debugging the API, which uses `some.lib`.

``` clojure
(log/set-levels '{my.app.api :all
                  some.lib :debug
                  ...})
```

This is really convenient and powerful. By sprinkling your code with logging at
various levels you can easily get insight in what a particular part is doing, on
demand.

Instead of adding more and more print statements as you debug, and then deleting
them afterwards, you can add increasingly detailed levels of logging instead.
Later when you find yourself in the same waters you can dial the verbosity up or
down as you see fit.

Glogi is based on goog.log, and so it understand the log levels that goog.log
provides. Glogi also aims to be at least partially API compatible with
pedestal.log, and so we provide extra log levels that internally map to goog.log
levels.

| pedestal | goog.log |    value | description                                                  |
|----------|----------|----------|--------------------------------------------------------------|
|          | :off     | Infinity | Special value to disable logging                             |
|          | :shout   |     1200 | Critical error                                               |
| :error   | :severe  |     1000 | Serious failure                                              |
| :warn    | :warning |      900 | Potential problem, but program continues                     |
|          | :info    |      800 | Informational message, e.g. to make background tasks visible |
|          | :config  |      700 | Configuration info                                           |
| :debug   | :fine    |      500 | Step-by-step debug messages                                  |
| :trace   | :finer   |      400 | More verbose, detailed tracing messages                      |
|          | :finest  |      300 | Highly verbose and detailed tracing                          |
|          | :all     |        0 | Special value to show all log messages                       |

There are also two special levels, `:all` and `:off`, which you can use in
`set-levels` to turn logging up to the maximum, or turn it off instead.

It is recommended to use a consistent set of logging methods, for instance to
use only the pedestal version, possibly augmented by the goog.log levels that
don't have a pedestal equivalent.

for instance:

``` clojure
(log/shout ,,,)
(log/severe ,,,)
(log/error ,,,)
(log/warn ,,,)
(log/info ,,,)
(log/config ,,,)
(log/debug ,,,)
(log/trace ,,,)
(log/finest ,,,)
```

If you are using `lambdaisland.glogi.console`, then these levels will also
influence with `js/console` method is used, as well as the color used to print
the namespace name.

### Spy

There is a convenience macro `spy` which you can use to have a quick look at a
value. It outputs the form, the value, and returns the value so you can simply
wrap any expression you want to see the value. Spy expressions are logged at the
`:debug` level.

``` clojure
(let [x (spy (+ 1 1))
      y (spy (+ x x))]
  (+ x y))
;;=> 6
```

```
[my.ns] {:spy (+ 1 1) :=> 2}
[my.ns] {:spy (+ x x) :=> 4}
```

### Special keys

Two keywords have a special meaning in logging calls.

- `:exception` use this if you want to log an exception, this will make sure you
  get a proper stacktrace in the browser console
- `:lambdaisland.glogi/logger` name of the logger to use, defaults to `(str *ns*)`

### Controlling colorization

When using `lambdaisland.glogi.console/install!` it will try to detect what the
best logging strategy is for your environment.

- if cljs-devtools is detected then it will log ClojureScript objects directly
- if it detects a browser that is not pre-chromium IE/Edge, then it will use `console.log` `"%c"` CSS-based colorization
- all other cases it logs plain text

This behaviour can be changed by setting `lambdaisland.glogi.console.colorize`
in `:closure-defines` in your compiler options.

- `"auto"` the autodetect behavior described above (default)
- `"raw"` always log ClojureScript objects directly
- `"true"` format using CSS
- `"false"` format  as plain text

### Use with Pedestal

`lambdaisland.glogi`'s API overlaps with the API of `io.pedestal.log`, so if you
are writing cross-platform code you can do this:

``` clojure
(ns my.ns
  (:require #?(:clj [io.pedestal.log :as log]
               :cljs [lambdaisland.glogi :as log])))

(log/debug :foo :bar)
```

Note that goog.log has more distinct log levels than Pedestal, for this use case you need to limit yourself to

- trace
- debug
- info
- warn
- error
- spy

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

<!-- license-mpl -->
## License
&nbsp;
Copyright &copy; 2019-2020 Arne Brasseur
&nbsp;
Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license-epl -->
