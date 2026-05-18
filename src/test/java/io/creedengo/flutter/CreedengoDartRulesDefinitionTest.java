package io.creedengo.flutter;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class CreedengoDartRulesDefinitionTest {

    private static final String REPOSITORY_KEY = "creedengo-dart";

    private RulesDefinition.Repository definedRepository() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        new CreedengoDartRulesDefinition().define(context);
        return context.repository(REPOSITORY_KEY);
    }

    @Test
    void registersRepositoryWithExpectedMetadata() {
        RulesDefinition.Repository repository = definedRepository();

        assertThat(repository).isNotNull();
        assertThat(repository.name()).isEqualTo("Creedengo Dart Rules");
        assertThat(repository.language()).isEqualTo("dart");
        assertThat(repository.rules()).hasSize(15);
    }

    @Test
    void officialGciRulesAreRegistered() {
        RulesDefinition.Repository repository = definedRepository();

        assertThat(repository.rule("GCI3").name())
            .isEqualTo("Getting the size of the collection in the loop");
        assertThat(repository.rule("GCI31").name())
            .isEqualTo("Lighter formats should be used for image files");
        assertThat(repository.rule("GCI72").name())
            .isEqualTo("Perform an SQL query inside a loop");
        assertThat(repository.rule("GCI79").name()).isEqualTo("Free resources");
        assertThat(repository.rule("GCI79").severity()).isEqualTo("CRITICAL");
    }

    @Test
    void flutterSpecificRulesAreRegistered() {
        RulesDefinition.Repository repository = definedRepository();

        assertThat(repository.rule("GCI201").name()).isEqualTo("Avoid unnecessary widget rebuilds");
        assertThat(repository.rule("GCI202").name()).isEqualTo("Use ListView.builder for large lists");
        assertThat(repository.rule("GCI203").name()).isEqualTo("Avoid high-frequency timers");
    }

    @Test
    void mobileRulesAreRegistered() {
        RulesDefinition.Repository repository = definedRepository();

        assertThat(repository.rule("GCI522").name()).isEqualTo("Sobriety: Brightness Override");
        assertThat(repository.rule("GCI523").name()).isEqualTo("Sobriety: Thrifty Geolocation (minTime)");
        assertThat(repository.rule("GCI530").name()).isEqualTo("Sobriety: Torch Free");
        assertThat(repository.rule("GCI530").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("GCI531").name()).isEqualTo("Avoid excessive animations on mobile");
        assertThat(repository.rule("GCI532").name()).isEqualTo("Optimize network usage on mobile");
    }

    @Test
    void cnumrInspiredRulesAreRegistered() {
        RulesDefinition.Repository repository = definedRepository();

        assertThat(repository.rule("GCI534").name()).isEqualTo("Avoid camera resource leaks");
        assertThat(repository.rule("GCI535").name()).isEqualTo("Avoid network requests in loops");
        assertThat(repository.rule("GCI536").name()).isEqualTo("Avoid keeping the screen on unnecessarily");
    }

    @Test
    void everyRuleCarriesCreedengoEcoDesignTags() {
        RulesDefinition.Repository repository = definedRepository();

        repository.rules().forEach(rule -> {
            assertThat(rule.tags()).contains("creedengo");
            assertThat(rule.tags()).contains("eco-design");
        });
    }

    @Test
    void ruleKeysFollowGciConvention() {
        RulesDefinition.Repository repository = definedRepository();

        repository.rules().forEach(rule ->
            assertThat(rule.key()).matches("GCI\\d+"));
    }

    @Test
    void severitiesMatchExpectedClassification() {
        RulesDefinition.Repository repository = definedRepository();

        // Critical: leaks and the most energy-intensive APIs.
        assertThat(repository.rule("GCI79").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("GCI79").type().name()).isEqualTo("BUG");
        assertThat(repository.rule("GCI530").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("GCI534").severity()).isEqualTo("CRITICAL");

        // Minor: micro-optimisation.
        assertThat(repository.rule("GCI3").severity()).isEqualTo("MINOR");

        // Major: everything else.
        assertThat(repository.rule("GCI31").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI72").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI201").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI202").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI203").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI522").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI523").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI531").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI532").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI535").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("GCI536").severity()).isEqualTo("MAJOR");
    }
}
