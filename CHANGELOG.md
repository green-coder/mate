## Unreleased

## v0.0.4

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
