package io.creedengo.flutter.rules;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de base pour les tests de règles Dart
 */
public abstract class AbstractDartRuleTest {
    
    @Mock
    protected SensorContext sensorContext;
    
    @Mock
    protected InputFile inputFile;
    
    @Mock
    protected NewIssue newIssue;
    
    @Mock
    protected NewIssueLocation newIssueLocation;
    
    @Mock
    protected TextRange textRange;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configuration des mocks
        when(sensorContext.newIssue()).thenReturn(newIssue);
        when(newIssue.forRule(any(RuleKey.class))).thenReturn(newIssue);
        when(newIssue.newLocation()).thenReturn(newIssueLocation);
        when(newIssueLocation.on(any(InputFile.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.at(any(TextRange.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.message(anyString())).thenReturn(newIssueLocation);
        when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);
        when(inputFile.selectLine(anyInt())).thenReturn(textRange);
    }
    
    /**
     * Vérifie qu'une issue a été créée
     */
    protected void verifyIssueCreated() {
        verify(newIssue).save();
    }
    
    /**
     * Vérifie qu'aucune issue n'a été créée
     */
    protected void verifyNoIssueCreated() {
        verify(newIssue, never()).save();
    }
    
    /**
     * Vérifie qu'une issue a été créée avec un message spécifique
     */
    protected void verifyIssueCreatedWithMessage(String expectedMessage) {
        verify(newIssueLocation).message(expectedMessage);
        verify(newIssue).save();
    }
}