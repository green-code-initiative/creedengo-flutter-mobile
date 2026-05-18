# Creedengo Flutter Plugin

[![Build Status](https://github.com/creedengo/creedengo-flutter/actions/workflows/ci.yml/badge.svg)](https://github.com/creedengo/creedengo-flutter/actions)
[![License: GPL v3](https://img.shields.io/badge/License-GPL_v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![SonarQube](https://img.shields.io/badge/SonarQube-9.4%2B-orange)](https://www.sonarqube.org/)
[![Java](https://img.shields.io/badge/Java-11%2B-red)](https://adoptium.net/)

A SonarQube plugin that surfaces eco-design issues in Flutter and Dart codebases. It implements a subset of the [Creedengo / Green Code Initiative](https://github.com/green-code-initiative/creedengo-rules-specifications) specifications, adapted to the specifics of mobile and Flutter development.

> **Status:** experimental. Detection is currently regex-based; an AST-based analyzer using the [Dart analyzer](https://pub.dev/packages/analyzer) is on the roadmap (see [Limitations](#limitations)).

## Table of contents

- [Features](#features)
- [Rules](#rules)
- [Quick start (Docker)](#quick-start-docker)
- [Manual installation](#manual-installation)
- [Configuring an analysis](#configuring-an-analysis)
- [Building from source](#building-from-source)
- [Project structure](#project-structure)
- [Limitations](#limitations)
- [Compatibility](#compatibility)
- [Contributing](#contributing)
- [License](#license)

## Features

- Registers `dart` as a SonarQube language (extensions: `.dart`)
- 15 rules grouped by CNUMR mobile categories: leakage, bottleneck, optimized API, user interface
- Built-in quality profile activating all Creedengo Flutter rules by default
- Sensor that scans `.dart` files line by line and creates issues against the dedicated repository
- Reuses the standard Creedengo / Green Code Initiative `GCI*` identifiers when applicable

## Rules

All rules are exposed under the SonarQube repository key `creedengo-dart`.

### Leakage — resource leaks

| Key    | Name                  | Severity | Source                                                                                                              |
| ------ | --------------------- | -------- | ------------------------------------------------------------------------------------------------------------------- |
| GCI79  | Free resources        | Critical | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI79) |
| GCI530 | Torch free            | Critical | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI530) |
| GCI534 | Camera resource leaks | Critical | Flutter-specific extension                                                                                          |

### Bottleneck — inefficient operations

| Key    | Name                       | Severity | Source                                                                                                              |
| ------ | -------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------- |
| GCI3   | Collection size in loop    | Minor    | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI3)  |
| GCI72  | SQL query in loop          | Major    | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI72) |
| GCI535 | Network requests in a loop | Major    | Flutter-specific extension                                                                                          |

### Optimized API — adapted APIs

| Key    | Name                        | Severity | Source                                                                                                              |
| ------ | --------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------- |
| GCI201 | Avoid unnecessary rebuilds  | Major    | Flutter-specific extension                                                                                          |
| GCI202 | Use `ListView.builder`      | Major    | Flutter-specific extension                                                                                          |
| GCI523 | Thrifty geolocation         | Major    | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI523) |
| GCI532 | Optimize network usage      | Major    | Flutter-specific extension                                                                                          |

### User Interface — UI optimizations

| Key    | Name                          | Severity | Source                                                                                                              |
| ------ | ----------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------- |
| GCI31  | Lighter image formats         | Major    | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI31) |
| GCI203 | Avoid high-frequency timers   | Major    | Flutter-specific extension                                                                                          |
| GCI522 | Brightness override           | Major    | [Creedengo](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/src/main/rules/GCI522) |
| GCI531 | Avoid excessive animations    | Major    | Flutter-specific extension                                                                                          |
| GCI536 | Keep screen on                | Major    | Flutter-specific extension                                                                                          |

> Identifiers prefixed `GCI` that do not yet exist upstream are tracked as Flutter-specific extensions and may be proposed back to the [Creedengo specifications repository](https://github.com/green-code-initiative/creedengo-rules-specifications).

## Quick start (Docker)

The repository ships a `docker-compose.yml` that provisions PostgreSQL, SonarQube and mounts the freshly built plugin JAR.

```bash
# 1. Build the plugin JAR (drops it in target/)
mvn clean package

# 2. Start SonarQube + PostgreSQL with the plugin mounted
docker compose up -d sonarqube-db sonarqube

# 3. Wait until http://localhost:9000 reports status UP, then log in (admin/admin)
./scripts/verify-plugin.sh

# 4. Run an analysis on the bundled sample project
./scripts/run-analysis.sh
```

The bundled `flutter-test-project/` contains intentionally non-compliant Dart snippets so you can validate that issues are reported in SonarQube.

To tear everything down (containers, volumes):

```bash
./scripts/cleanup.sh
```

## Manual installation

If you already run a SonarQube instance:

1. Build the JAR: `mvn clean package`.
2. Copy `target/creedengo-flutter-1.0.0-SNAPSHOT.jar` into `$SONARQUBE_HOME/extensions/plugins/`.
3. Restart SonarQube.
4. Open *Rules* and filter by repository `Creedengo Dart Rules` to confirm the plugin is loaded.

## Configuring an analysis

Add a `sonar-project.properties` file at the root of your Flutter project:

```properties
sonar.projectKey=my-flutter-app
sonar.projectName=My Flutter App
sonar.projectVersion=1.0.0

sonar.sources=lib
sonar.tests=test
sonar.sourceEncoding=UTF-8

# Optional: skip generated files
sonar.exclusions=**/*.g.dart,**/*.freezed.dart
```

Then run the SonarScanner pointing at your SonarQube instance:

```bash
sonar-scanner \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<token>
```

The plugin only contributes Creedengo rules. Test reports, coverage and other Dart-related metrics are not produced by this plugin and need a complementary tool if you want them in SonarQube.

## Building from source

Requirements:

- JDK 11 or newer
- Maven 3.6 or newer

```bash
# Compile, run unit tests, package the plugin
mvn clean verify

# Build only (skip tests)
mvn clean package -DskipTests

# Run a single test class
mvn -Dtest=RuleManagerTest test
```

A coverage report is produced by JaCoCo at `target/site/jacoco/index.html`.

## Project structure

```
.
├── src/
│   ├── main/java/io/creedengo/flutter/
│   │   ├── CreedengoDartPlugin.java          # plugin entry point
│   │   ├── DartLanguage.java                # registers the dart language
│   │   ├── CreedengoDartRulesDefinition.java # rule catalogue
│   │   ├── CreedengoDartQualityProfile.java  # default quality profile
│   │   ├── CreedengoDartSensor.java          # file scanner
│   │   └── rules/                           # one package per CNUMR category
│   │       ├── leakage/
│   │       ├── bottleneck/
│   │       ├── optimizedapi/
│   │       └── userinterface/
│   └── test/java/                           # unit tests mirroring the package layout
├── flutter-test-project/                    # sample Dart code with intentional violations
├── scripts/                                 # helper shell scripts (Docker-based test loop)
├── docker-compose.yml
├── Dockerfile.sonarqube
└── pom.xml
```

## Limitations

- Detection is **regex-based**. False positives and false negatives are expected, especially on multi-line constructs or when violations span several files. Do not rely on the plugin for safety-critical reviews.
- The Dart language registration is minimal: the plugin does not parse Dart code into an AST, does not provide syntax highlighting, and does not compute Dart-specific metrics (lines of code per type, cognitive complexity, etc.).
- The plugin does not import test or coverage reports. Pair it with another tool if you need those.
- Tested against SonarQube 9.4 LTS and 10.3 Community. Other versions may work but are not part of the CI matrix yet.

## Compatibility

| Plugin version | SonarQube           | Java |
| -------------- | ------------------- | ---- |
| 1.0.x          | 9.4 LTS – 10.3 CE   | 11+  |

## Contributing

Bug reports and pull requests are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening an issue or PR. By contributing you agree that your contributions are licensed under the same terms as the project (GPL-3.0).

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

## Acknowledgements

- The [Green Code Initiative](https://github.com/green-code-initiative) for the Creedengo specifications.
- The [CNUMR mobile best practices](https://github.com/cnumr/best-practices-mobile) catalogue, which inspired the rule categorization.
