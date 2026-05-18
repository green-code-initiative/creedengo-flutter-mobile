package io.creedengo.flutter;

import org.sonar.api.Plugin;

/**
 * Entry point of the Creedengo Flutter SonarQube plugin.
 *
 * <p>Registers the {@code dart} language, the rules repository, the default
 * quality profile and the sensor that scans Dart source files.</p>
 */
public class CreedengoDartPlugin implements Plugin {

    public static final String LANGUAGE_KEY = "dart";
    public static final String LANGUAGE_NAME = "Dart";

    @Override
    public void define(Context context) {
        context.addExtension(DartLanguage.class);
        context.addExtension(CreedengoDartQualityProfile.class);
        context.addExtension(CreedengoDartRulesDefinition.class);
        context.addExtension(CreedengoDartSensor.class);
    }
}
