package io.creedengo.flutter.rules;

import org.junit.jupiter.api.Test;

/**
 * Suite de tests complète pour toutes les règles Creedengo Flutter
 * organisées par catégories CNUMR
 * 
 * Structure des tests :
 * - Bottleneck (Goulots d'étranglement) : 3 règles
 * - Leakage (Fuites de ressources) : 3 règles  
 * - Optimized API (API optimisées) : 4 règles
 * - User Interface (Interface utilisateur) : 5 règles
 * 
 * Total : 15 règles testées
 */
public class AllRulesTestSuite {

    @Test
    void testAllRulesAreImplemented() {
        // Ce test vérifie que toutes les règles sont bien implémentées
        RuleManager ruleManager = new RuleManager();
        var rules = ruleManager.getRules();
        
        // Vérification du nombre total de règles
        assert rules.size() == 15 : "Expected 15 rules, but found " + rules.size();
        
        // Vérification par catégorie
        long bottleneckRules = rules.stream()
            .filter(rule -> rule.getClass().getPackage().getName().contains("bottleneck"))
            .count();
        assert bottleneckRules == 3 : "Expected 3 bottleneck rules, found " + bottleneckRules;
        
        long leakageRules = rules.stream()
            .filter(rule -> rule.getClass().getPackage().getName().contains("leakage"))
            .count();
        assert leakageRules == 3 : "Expected 3 leakage rules, found " + leakageRules;
        
        long optimizedApiRules = rules.stream()
            .filter(rule -> rule.getClass().getPackage().getName().contains("optimizedapi"))
            .count();
        assert optimizedApiRules == 4 : "Expected 4 optimized API rules, found " + optimizedApiRules;
        
        long userInterfaceRules = rules.stream()
            .filter(rule -> rule.getClass().getPackage().getName().contains("userinterface"))
            .count();
        assert userInterfaceRules == 5 : "Expected 5 user interface rules, found " + userInterfaceRules;
    }
}