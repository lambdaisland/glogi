# Unreleased

## Added

- Add support for logging multiple forms with `spy`.

## Fixed

- Release under `com.lambdaisland` as well as `lambdaisland`, see https://github.com/lambdaisland/open-source/issues/9

## Changed

- Version bumps

# 1.1.144 (2021-12-14 / 6f83c7d)

## Fixed

- Fix Closure compatibility issue

# 1.0.136 (2021-10-01 / 40a1dbc)

## Changed

- Set a default level of `:info` for the root logger 

# 1.0.128 (2021-07-19 / 576ceba)

## Fixed

- Fix macro resolve in `ns`, which caused cljs logs to fail

## Changed

# 1.0.116 (2021-04-26 / e1dbcb3)

## Fixed

- Fixed an issue with advanced compilation when calling `level-value`, which is
  used by the console handler

# 1.0.112 (2021-04-21 / a104211)

## Changed

- Artifact name changed to `com.lambdaisland/glogi` in line with Clojars policy

# 1.0.106 (2021-04-20 / 1128462)

## Fixed

- Prevent single-arg `logger` from erasing the log level

# 1.0.100 (2021-04-15 / 8a8ccf0)

## Fixed

- More fixes to deal with upstream changes in Google Closure Library

# 1.0.83 (2021-03-09 / d0f76e8)

## Added

- With `lambdaisland.glogc` there is now an official way to use the
  Glogi/Pedestal-log combo in a consistent cross-platform way

# 1.0.80 (2021-03-09 / 9c5ea2b)

## Fixed

- Maintain compatibility with newer versions of the Google Closure library,
  which introduced breaking changes in v20210302

## Changed

# 1.0.74 (2020-08-26 / acf8c48)

## Added

- Export lambdaisland.glogi.set-levels so you can use it from the browser
  console (pass in an array of two-element arrays of strings)

# 1.0.70 (2020-08-19 / df34f1a)

## Changed

- We no longer pull in a specific Clojure/ClojureScript version, assuming that
  client consumers will already have specific versions declared for their project.

# 1.0.63 (2020-05-05 / bcafca0)

## Added

- the Closure constant `lambdaisland.glogi.console.colorize` can now take four
  possible values, `"true"` (use `console.log` CSS formatting), `"false"` (log
  plain text), `"raw"`, log objects directly (good for cljs-devtools), or
  `"auto"` (detect most suitable option)

# 1.0-60 (2020-04-15 / 71bea10)

## Fixed

- For for when goog.log.ENABLED is false (for use in prod builds)

# 1.0-55 (2020-04-07 / 592208d)

## Fixed

- Fix incorrect variable reference in `logger`
- Honor goog.log.ENABLED, important for release builds

# 1.0-47 (2020-04-02 / 35d7fff)

## Added

- Print support for cljs.core.PersistentQueue

# 1.0-44 (2020-04-02 / 5a377e7)

## Added

- Added colored printing of objects and arrays

## Fixed

- Fixed colored printing of seqs and vectors

## Changed

- Better mapping of log levels to log methods and colors. `:trace` is now an
  alias for `:finer`, `:debug` for `:fine` (before `:trace` = `:fine` , `:debug`
  = `:config`)

# 1.0-41 (2020-03-31 / ab9f97f)

## Added

- Added colorization of Clojure data structures, for places where devtools is
  not available
- Added the `config` macro, which logs to the corresponding log level

# 0.0-36 (2020-03-30 / e2606fb)

## Added

- Added `spy` macro, logs the expression and its return value, returns the value
- Added logging macros corresponding with goog.log log levels: `shout`,
  `severe`, `fine`, `finer`, `finest`

## Fixed

- Use the correct console log method (log, error, warn, info) based on the log
  level

# 0.0-33 (2020-03-25 / cd9df6b)

## Added

- Added the ability to use keywords or symbols to look up a logger, or the
  special `:glogi/root` to find the root logger.

## Fixed

- Fixed `set-levels` to match its docstring. Takes a map.

# 0.0-29 (2020-03-25 / 991866f)

## Fixed

- Got rid of the `LogBuffer/CAPACITY` hack, to prevent issues with advanced
  compilation

# 0.0-25 (2019-06-25 / 0e226a8)

## Added

- Added `set-level` for convenience

## Fixed

- Fix assertion in `set-level`

# 0.0-22 (2019-06-11 / 0d56b02)

## Fixed

- Fix glogi.console when devtools isn't available

# 0.0-18 (2019-06-11 / a27f7fc)

## Added

- `lambdaisland.glogi.console` provides an alternative to `goog.debug.Console`,
  with the main benefit that it will log full data structures, thus playing
  nicely with cljs-devtools
- Added an `info` macro for compat with pedestal.

## Changed

- The default formatter is now `identity` instead of `pr-str`. This way we
  preserve full data structures until the last minute. Note that goog.debug.Logger
  will stringify the message, unless care is taken for it not to.

# 0.0-13 (2019-06-10 / 0668ebe)

First release
