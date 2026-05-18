# Security policy

## Supported versions

Only the latest minor release line of Creedengo Flutter receives security
fixes. Older versions may be patched on a best-effort basis if the fix is
trivial to backport.

| Version | Supported |
| ------- | --------- |
| 1.0.x   | yes       |
| < 1.0   | no        |

## Reporting a vulnerability

Please **do not** open a public GitHub issue for security problems.

Instead, send a description of the issue to **security@creedengo.io** (placeholder
mailbox; replace with the real address before publishing). Include:

- A description of the vulnerability and its impact.
- The version(s) of the plugin and SonarQube where it was observed.
- Reproduction steps or a proof of concept.
- Any suggested mitigation, if you have one.

You can expect:

- An acknowledgement within 5 business days.
- A status update within 15 business days, including whether the report is
  accepted, rejected or needs more information.
- Coordinated disclosure: we will agree on a timeline and credit you in the
  release notes if you wish.

## Scope

This policy covers the source code in this repository and the plugin JAR it
produces. Vulnerabilities in upstream dependencies (SonarQube itself, the JVM,
PostgreSQL, etc.) should be reported to their respective maintainers.
