# Unreleased

## Added

## Fixed

## Changed

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
