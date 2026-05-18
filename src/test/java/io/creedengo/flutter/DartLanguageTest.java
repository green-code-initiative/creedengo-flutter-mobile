package io.creedengo.flutter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DartLanguageTest {

    @Test
    void testLanguageProperties() {
        DartLanguage language = new DartLanguage();
        
        assertThat(language.getKey()).isEqualTo("dart");
        assertThat(language.getName()).isEqualTo("Dart");
        assertThat(language.getFileSuffixes()).containsExactly(".dart");
    }
}