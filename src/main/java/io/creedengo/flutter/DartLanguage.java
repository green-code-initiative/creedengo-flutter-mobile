package io.creedengo.flutter;

import org.sonar.api.resources.AbstractLanguage;

/**
 * Registers Dart as a SonarQube language so that the plugin's sensor can
 * be invoked on {@code .dart} source files.
 */
public class DartLanguage extends AbstractLanguage {

    public static final String KEY = "dart";
    public static final String NAME = "Dart";

    public DartLanguage() {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[]{".dart"};
    }
}
