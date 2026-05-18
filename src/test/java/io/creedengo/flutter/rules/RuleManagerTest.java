package io.creedengo.flutter.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class RuleManagerTest {
    
    @Mock
    private SensorContext sensorContext;
    
    @Mock
    private InputFile inputFile;
    
    @Mock
    private NewIssue newIssue;
    
    @Mock
    private NewIssueLocation newIssueLocation;
    
    @Mock
    private TextRange textRange;
    
    private RuleManager ruleManager;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ruleManager = new RuleManager();
        
        // Setup mocks for issue creation
        when(sensorContext.newIssue()).thenReturn(newIssue);
        when(newIssue.forRule(any(RuleKey.class))).thenReturn(newIssue);
        when(newIssue.newLocation()).thenReturn(newIssueLocation);
        when(newIssueLocation.on(any(InputFile.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.at(any(TextRange.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.message(anyString())).thenReturn(newIssueLocation);
        when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);
        when(inputFile.selectLine(anyInt())).thenReturn(textRange);
    }
    
    @Test
    void shouldHaveAllExpectedRules() {
        // Given & When
        var rules = ruleManager.getRules();
        
        // Then
        assertThat(rules).hasSize(15);
        
        // Vérifier que toutes les catégories sont représentées
        var ruleKeys = rules.stream().map(rule -> rule.getClass().getSimpleName()).collect(java.util.stream.Collectors.toList());
        
        // Leakage rules (3)
        assertThat(ruleKeys).contains("GCI79FreeResourcesRule");
        assertThat(ruleKeys).contains("GCI534CameraResourceLeakRule");
        assertThat(ruleKeys).contains("GCI530TorchFreeRule");
        
        // Bottleneck rules (3)
        assertThat(ruleKeys).contains("GCI3CollectionSizeInLoopRule");
        assertThat(ruleKeys).contains("GCI72SqlQueryInLoopRule");
        assertThat(ruleKeys).contains("GCI535NetworkRequestsInLoopRule");
        
        // Optimized API rules (4)
        assertThat(ruleKeys).contains("GCI201AvoidUnnecessaryRebuildsRule");
        assertThat(ruleKeys).contains("GCI202UseListViewBuilderRule");
        assertThat(ruleKeys).contains("GCI523ThriftyGeolocationRule");
        assertThat(ruleKeys).contains("GCI532OptimizeNetworkUsageRule");
        
        // User Interface rules (5)
        assertThat(ruleKeys).contains("GCI31LighterImageFormatsRule");
        assertThat(ruleKeys).contains("GCI203AvoidHighFrequencyTimersRule");
        assertThat(ruleKeys).contains("GCI522BrightnessOverrideRule");
        assertThat(ruleKeys).contains("GCI531AvoidExcessiveAnimationsRule");
        assertThat(ruleKeys).contains("GCI536KeepScreenOnRule");
    }
    
    @Test
    void shouldAnalyzeLineWithAllRules() {
        // Given
        String line = "setState(() => counter++);";
        int lineNumber = 1;
        
        // When & Then - Should not throw exceptions
        ruleManager.analyzeLine(sensorContext, inputFile, line, lineNumber);
    }
    
    @Test
    void shouldAnalyzeFileWithAllRules() {
        // Given
        String content = "class MyWidget extends StatefulWidget {}";
        
        // When & Then - Should not throw exceptions
        ruleManager.analyzeFile(sensorContext, inputFile, content);
    }
    
    @Test
    void shouldHaveRulesFromAllCategories() {
        // Given & When
        var rules = ruleManager.getRules();
        
        // Then - Vérifier la répartition par package (catégorie)
        long leakageRules = rules.stream()
            .filter(rule -> rule.getClass().getPackageName().contains("leakage"))
            .count();
        
        long bottleneckRules = rules.stream()
            .filter(rule -> rule.getClass().getPackageName().contains("bottleneck"))
            .count();
        
        long optimizedApiRules = rules.stream()
            .filter(rule -> rule.getClass().getPackageName().contains("optimizedapi"))
            .count();
        
        long userInterfaceRules = rules.stream()
            .filter(rule -> rule.getClass().getPackageName().contains("userinterface"))
            .count();
        
        assertThat(leakageRules).isEqualTo(3);
        assertThat(bottleneckRules).isEqualTo(3);
        assertThat(optimizedApiRules).isEqualTo(4);
        assertThat(userInterfaceRules).isEqualTo(5);
    }
    
    @Test
    void shouldNotThrowExceptionOnNullInput() {
        // Given
        String nullLine = null;
        String nullContent = null;
        
        // When & Then - Should not throw exceptions
        ruleManager.analyzeLine(sensorContext, inputFile, nullLine, 1);
        ruleManager.analyzeFile(sensorContext, inputFile, nullContent);
    }
    
    @Test
    void shouldNotThrowExceptionOnEmptyInput() {
        // Given
        String emptyLine = "";
        String emptyContent = "";
        
        // When & Then - Should not throw exceptions
        ruleManager.analyzeLine(sensorContext, inputFile, emptyLine, 1);
        ruleManager.analyzeFile(sensorContext, inputFile, emptyContent);
    }
}