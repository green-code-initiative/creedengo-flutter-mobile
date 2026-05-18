package io.creedengo.flutter.rules;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Tests unitaires simples pour vérifier le bon fonctionnement de base des règles
 * 
 * Pour des tests complets par catégorie, voir :
 * - BottleneckRulesTest
 * - LeakageRulesTest  
 * - OptimizedApiRulesTest
 * - UserInterfaceRulesTest
 */
class SimpleRulesTest extends AbstractDartRuleTest {
    
    @Test
    void testRulesDoNotThrowExceptions() {
        // Given
        var rules = new RuleManager().getRules();
        String[] testLines = {
            "setState(() => counter++);",
            "for (int i = 0; i < items.length; i++) {}",
            "Image.asset('logo.bmp')",
            "ListView(children: widgets)",
            "Timer.periodic(Duration(milliseconds: 50), callback)",
            "CameraController(cameras[0], ResolutionPreset.medium)",
            "http.get(Uri.parse(url))",
            "",
            null
        };
        
        // When & Then - Should not throw exceptions
        for (var rule : rules) {
            for (String line : testLines) {
                rule.analyzeLine(sensorContext, inputFile, line, 1);
                rule.analyzeFile(sensorContext, inputFile, line);
            }
        }
    }
    
    @Test
    void testRuleManagerHasAllRules() {
        // Given
        RuleManager ruleManager = new RuleManager();
        
        // When
        var rules = ruleManager.getRules();
        
        // Then
        assert rules.size() == 15 : "Expected 15 rules, but found " + rules.size();
        assert rules.stream().noneMatch(rule -> rule == null) : "Found null rule in list";
    }
}