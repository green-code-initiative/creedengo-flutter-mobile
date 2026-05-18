# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial set of 15 Creedengo Flutter rules grouped under the categories
  `leakage`, `bottleneck`, `optimizedapi`, `userinterface`.
- Built-in `Creedengo Dart Profile` quality profile.
- `CreedengoDartSensor` scanning `.dart` files line by line and delegating to
  category-specific rule implementations.
- Docker Compose setup with SonarQube + PostgreSQL, helper scripts to verify
  the installation and run an analysis on the bundled sample project.
- JaCoCo coverage reporting via Maven.

[Unreleased]: https://github.com/creedengo/creedengo-flutter/compare/HEAD...HEAD
