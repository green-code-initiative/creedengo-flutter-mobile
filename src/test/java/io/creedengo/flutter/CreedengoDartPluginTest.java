package io.creedengo.flutter;

import org.junit.jupiter.api.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

class CreedengoDartPluginTest {

    @Test
    void registersExpectedExtensions() {
        SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(
            Version.create(9, 4), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY);
        Plugin.Context context = new Plugin.Context(runtime);

        new CreedengoDartPlugin().define(context);

        assertThat(context.getExtensions()).containsExactly(
            DartLanguage.class,
            CreedengoDartQualityProfile.class,
            CreedengoDartRulesDefinition.class,
            CreedengoDartSensor.class
        );
    }

    @Test
    void exposesDartLanguageKey() {
        assertThat(CreedengoDartPlugin.LANGUAGE_KEY).isEqualTo("dart");
    }

    @Test
    void exposesDartLanguageName() {
        assertThat(CreedengoDartPlugin.LANGUAGE_NAME).isEqualTo("Dart");
    }
}
