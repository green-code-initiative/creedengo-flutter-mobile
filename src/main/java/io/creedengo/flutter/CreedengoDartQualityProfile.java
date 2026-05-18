package io.creedengo.flutter;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

/**
 * Built-in Creedengo quality profile for Dart/Flutter.
 *
 * <p>Activates every rule contributed by this plugin and is registered as
 * the default profile for the {@code dart} language.</p>
 */
public class CreedengoDartQualityProfile implements BuiltInQualityProfilesDefinition {

    public static final String PROFILE_NAME = "Creedengo Dart Profile";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, DartLanguage.KEY);

        activateOfficialGCIRules(profile);
        activateFlutterSpecificRules(profile);
        activateMobileRules(profile);

        profile.setDefault(true);
        profile.done();
    }

    /** Rules taken from the official Creedengo specifications. */
    private void activateOfficialGCIRules(NewBuiltInQualityProfile profile) {
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI3");   // Collection size in loop
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI31");  // Lighter image formats
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI72");  // SQL query in loop
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI79");  // Free resources
    }

    /** Flutter-specific extensions. */
    private void activateFlutterSpecificRules(NewBuiltInQualityProfile profile) {
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI201"); // Avoid unnecessary rebuilds
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI202"); // Use ListView.builder
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI203"); // Avoid high-frequency timers
    }

    /** Mobile-focused rules (mix of official Creedengo and Flutter extensions). */
    private void activateMobileRules(NewBuiltInQualityProfile profile) {
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI522"); // Brightness override
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI523"); // Thrifty geolocation
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI530"); // Torch free
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI531"); // Avoid excessive animations
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI532"); // Optimize network usage
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI534"); // Camera resource leaks
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI535"); // Network requests in loops
        profile.activateRule(CreedengoDartRulesDefinition.REPOSITORY_KEY, "GCI536"); // Keep screen on
    }
}
