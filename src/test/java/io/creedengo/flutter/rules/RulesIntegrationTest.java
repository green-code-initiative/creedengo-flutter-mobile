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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test d'intégration pour valider que les règles détectent correctement les problèmes
 * dans un code Flutter réaliste avec plusieurs violations.
 */
class RulesIntegrationTest {
    
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
        
        // Configuration des mocks
        when(sensorContext.newIssue()).thenReturn(newIssue);
        when(newIssue.forRule(any(RuleKey.class))).thenReturn(newIssue);
        when(newIssue.newLocation()).thenReturn(newIssueLocation);
        when(newIssueLocation.on(any(InputFile.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.at(any(TextRange.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.message(anyString())).thenReturn(newIssueLocation);
        when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);
        when(inputFile.selectLine(anyInt())).thenReturn(textRange);
        
        ruleManager = new RuleManager();
    }
    
    @Test
    void shouldDetectLeakageViolations() {
        // Given - Code avec violations de fuites de ressources
        String leakageCode = "class _MyWidgetState extends State<MyWidget> {\n" +
            "  late CameraController controller;\n" +
            "  void initState() {\n" +
            "    controller = CameraController(cameras[0], ResolutionPreset.medium);\n" +
            "  }\n" +
            "}";
        
        // When
        ruleManager.analyzeFile(sensorContext, inputFile, leakageCode);
        
        // Then - Devrait détecter GCI79 (pas de dispose) et GCI534 (CameraController)
        verify(newIssue, atLeast(1)).save();
    }
    
    @Test
    void shouldDetectBottleneckViolations() {
        // Given - Code avec violations de goulots d'étranglement
        String bottleneckCode = "for (int i = 0; i < items.length; i++) {\n" +
            "  await database.query('SELECT * FROM users WHERE id = ?', [i]);\n" +
            "  http.get(Uri.parse('https://api.example.com/item/$i'));\n" +
            "}";
        
        // When
        ruleManager.analyzeLine(sensorContext, inputFile, bottleneckCode, 1);
        
        // Then - Devrait détecter des violations
        verify(newIssue, atLeast(1)).save();
    }
    
    @Test
    void shouldDetectUserInterfaceViolations() {
        // Given - Code avec violations d'interface utilisateur
        String uiCode1 = "Image.asset('assets/logo.bmp')";
        String uiCode2 = "Timer.periodic(Duration(milliseconds: 50), callback)";
        
        // When
        ruleManager.analyzeLine(sensorContext, inputFile, uiCode1, 1);
        ruleManager.analyzeLine(sensorContext, inputFile, uiCode2, 2);
        
        // Then - Devrait détecter des violations
        verify(newIssue, atLeast(2)).save();
    }
    
    @Test
    void shouldDetectOptimizedApiViolations() {
        // Given - Code avec violations d'API optimisées
        String apiCode1 = "ListView(children: widgets)";
        String apiCode2 = "items.forEach((item) => setState(() => processItem(item)));";
        
        // When
        ruleManager.analyzeLine(sensorContext, inputFile, apiCode1, 1);
        ruleManager.analyzeLine(sensorContext, inputFile, apiCode2, 2);
        
        // Then - Devrait détecter des violations
        verify(newIssue, atLeast(2)).save();
    }
    
    @Test
    void shouldNotDetectViolationsInCleanCode() {
        // Given - Code propre sans violations
        String cleanCode = "class _CleanWidgetState extends State<CleanWidget> {\n" +
            "  Timer? timer;\n" +
            "  void initState() {\n" +
            "    timer = Timer.periodic(Duration(seconds: 1), updateData);\n" +
            "  }\n" +
            "  void dispose() {\n" +
            "    timer?.cancel();\n" +
            "    super.dispose();\n" +
            "  }\n" +
            "}";
        
        // When
        ruleManager.analyzeFile(sensorContext, inputFile, cleanCode);
        
        // Then - Aucune violation ne devrait être détectée
        verify(newIssue, never()).save();
    }
    
    @Test
    void shouldHandleNullAndEmptyInput() {
        // Given
        String nullContent = null;
        String emptyContent = "";
        
        // When & Then - Ne devrait pas lever d'exception
        ruleManager.analyzeFile(sensorContext, inputFile, nullContent);
        ruleManager.analyzeFile(sensorContext, inputFile, emptyContent);
        ruleManager.analyzeLine(sensorContext, inputFile, null, 1);
        ruleManager.analyzeLine(sensorContext, inputFile, "", 1);
    }
}