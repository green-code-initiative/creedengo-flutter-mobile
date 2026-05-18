# Contributing to Creedengo Flutter

Thanks for taking the time to contribute. This document describes how to set up a development environment, the conventions used in the codebase and the expected workflow for changes.

## Prerequisites

- JDK 11+
- Maven 3.6+
- Git
- Docker and Docker Compose (only required to run the integration loop against SonarQube)

## Local setup

```bash
git clone https://github.com/creedengo/creedengo-flutter.git
cd creedengo-flutter
mvn verify
```

`mvn verify` compiles the project, runs all unit tests and packages the plugin under `target/`. JaCoCo produces a coverage report at `target/site/jacoco/index.html`.

To validate a change end-to-end against a real SonarQube instance:

```bash
mvn clean package
docker compose up -d sonarqube-db sonarqube
./scripts/verify-plugin.sh
./scripts/run-analysis.sh
```

## Branching and commits

- Branch off `main` for any change. Suggested prefixes: `feat/`, `fix/`, `docs/`, `chore/`, `refactor/`.
- Use [Conventional Commits](https://www.conventionalcommits.org/) for commit messages, for example:

  ```
  feat(rules): add GCI42 detection for unbounded streams
  fix(GCI31): handle uppercase BMP extensions
  docs(readme): document the Docker quick start
  ```

- Keep commits focused. Prefer multiple small commits over one large one.

## Adding or updating a rule

1. Pick a key. Reuse the upstream `GCI<n>` identifier when the rule already exists in the [Creedengo specifications](https://github.com/green-code-initiative/creedengo-rules-specifications). Otherwise, choose the next free `GCI<n>` and document that it is a Flutter-specific extension.
2. Drop the implementation in the right category package under `src/main/java/io/creedengo/flutter/rules/<category>/`. Each rule extends `AbstractDartRule`.
3. Register the rule:
   - In `CreedengoDartRulesDefinition` (metadata, severity, type, tags).
   - In `RuleManager` so that the sensor calls it.
   - In `CreedengoDartQualityProfile` if it should be active in the default profile.
4. Add unit tests that cover at least one positive and one negative case. Place them under the matching test package.
5. Update the rule table in `README.md`.

A rule should:

- Document the rationale in its Javadoc, ideally linking to the upstream specification or a CNUMR best practice.
- Produce an actionable English message that mentions both the problem and a suggested fix.
- Stay focused. If two checks are unrelated, prefer two rules over a combined one.

## Coding conventions

- Java 11 source level.
- 4-space indentation, no tabs.
- One top-level type per file.
- Private fields are `final` whenever possible.
- Compile patterns once as `static final Pattern` constants instead of recompiling them on every call.
- Avoid mutable static state. Rule instances may be reused across files.

## Testing

- Tests use JUnit 5, Mockito and AssertJ.
- The base class `AbstractDartRuleTest` wires up a mock SonarQube sensor context. Extend it for new rule tests.
- Aim for tests that fail when the rule changes behaviour, not just when the message string changes. Prefer matching on stable substrings.

## Reporting bugs and proposing features

Open an issue with:

- A short summary.
- Steps to reproduce (a Dart snippet that triggers the problem is ideal).
- The expected and actual behaviour.
- Plugin version, SonarQube version, OS, Java version.

For larger features (new rule categories, AST-based analyzer, etc.), please start a discussion or draft a short design note in the issue before writing code.

## Code of conduct

Be respectful. Discriminatory, harassing or otherwise abusive behaviour is not tolerated.

## License

By contributing you agree that your contributions are licensed under the project's [GPL-3.0](LICENSE) license.
