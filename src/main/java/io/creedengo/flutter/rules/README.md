# Rule packages

Rules are grouped following the [CNUMR mobile best practices](https://github.com/cnumr/best-practices-mobile) categorisation. Each subpackage contains the rules that belong to one category.

| Package        | Category       | Focus                                                                  |
| -------------- | -------------- | ---------------------------------------------------------------------- |
| `leakage`      | Leakage        | Resource leaks (timers, controllers, sensors) draining the battery.    |
| `bottleneck`   | Bottleneck     | Inefficient or repeated operations (loops, redundant queries).         |
| `optimizedapi` | Optimized API  | Choosing power-aware APIs (lazy loading, low-precision GPS, caching).  |
| `userinterface`| User Interface | UI-driven energy waste (animations, screen brightness, image formats). |

## Adding a new rule

1. Create a class extending [`AbstractDartRule`](AbstractDartRule.java) in the matching subpackage.
2. Register the rule in [`CreedengoDartRulesDefinition`](../CreedengoDartRulesDefinition.java) and, when relevant, in [`CreedengoDartQualityProfile`](../CreedengoDartQualityProfile.java).
3. Append the rule instance to [`RuleManager`](RuleManager.java).
4. Add unit tests under `src/test/java/.../rules/<category>/` and update the rule table in the project [README](../../../../../../../../README.md).

## References

- [Creedengo / Green Code Initiative rule specifications](https://github.com/green-code-initiative/creedengo-rules-specifications)
- [CNUMR mobile best practices](https://github.com/cnumr/best-practices-mobile)
- [GR491 — référentiel de conception responsable de service numérique](https://gr491.isit-europe.org/)
