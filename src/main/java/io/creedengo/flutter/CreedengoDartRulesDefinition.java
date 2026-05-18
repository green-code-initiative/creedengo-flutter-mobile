package io.creedengo.flutter;

import io.creedengo.flutter.rules.RuleDefinitionGenerator;
import org.sonar.api.server.rule.RulesDefinition;

/**
 * Declares the Creedengo Dart rule repository and delegates the actual rule
 * registration to {@link RuleDefinitionGenerator}.
 */
public class CreedengoDartRulesDefinition implements RulesDefinition {

    public static final String REPOSITORY_KEY = "creedengo-dart";
    public static final String REPOSITORY_NAME = "Creedengo Dart Rules";

    @Override
    public void define(Context context) {
        NewRepository repository = context
            .createRepository(REPOSITORY_KEY, DartLanguage.KEY)
            .setName(REPOSITORY_NAME);

        RuleDefinitionGenerator.createAllRules(repository);

        repository.done();
    }
}
