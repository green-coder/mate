## Unreleased

## v0.0.8

### Added

- `re-with-flags` (Cljs)
- `indexed-re-find` (Clj & Cljs)
- `indexed-re-groups` (Clj).

## v0.0.7

### Added

- Thread macro `if->`.
- Thread macro `let->`.

## v0.0.6

### Added

- Added tests.
- Added n-arity on function `mate.re-frame/conj-fx`.

## v0.0.5

### Added

- New function `mate.core/ungroup-keys` and its test.
- New functions `mate.re-frame/into-fx` and `mate.re-frame/into-fx-using-db`, and some tests.

## v0.0.4

### Added

Added namespace `mate.re-frame` with utility functions useful for composing functions
on Re-frame effects via the `->` thread macro.

See `/test/mate/re_frame_test.cljc` for example of usage.

## v0.0.3

### Added

In `mate.io`:
- `implies`

### Fixed

- Function `inline-resource*` should be within `#?(:clj ...)`.

## v0.0.2

### Added

In `mate.io`:
- `inline-resource`

## v0.0.1

### Added

In `mate.core`:
- `seq-indexed`
- `comp->`
- `group-by`
- `index-by`
