package io.creedengo.flutter.rules.leakage;

import io.creedengo.flutter.rules.AbstractDartRuleTest;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour les règles de la catégorie Leakage (Fuites de ressources)
 * 
 * Catégorie CNUMR : Leakage
 * Règles testées :
 * - GCI79: Free resources (Media/Sensor Leak)
 * - GCI534: Camera resource leaks
 * - GCI530: Torch Free (composant énergivore)
 */
public class LeakageRulesTest extends AbstractDartRuleTest {

    // ===== GCI79: Free Resources =====
    
    @Test
    void testGCI79_DetectsStateClassWithoutDispose() {
        // Given
        GCI79FreeResourcesRule rule = new GCI79FreeResourcesRule();
        String fileContent = "class _MyWidgetState extends State<MyWidget> {\n" +
            "  Timer? timer;\n" +
            "  void initState() {\n" +
            "    super.initState();\n" +
            "  }\n" +
            "}";
        
        // When - Analyze complete file
        rule.analyzeFile(sensorContext, inputFile, fileContent);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI79_AllowsStateClassWithDispose() {
        // Given
        GCI79FreeResourcesRule rule = new GCI79FreeResourcesRule();
        String fileContent = "class _MyWidgetState extends State<MyWidget> {\n" +
            "  Timer? timer;\n" +
            "  @override\n" +
            "  void dispose() {\n" +
            "    timer?.cancel();\n" +
            "    super.dispose();\n" +
            "  }\n" +
            "}";
        
        // When - Only analyze complete file
        rule.analyzeFile(sensorContext, inputFile, fileContent);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI534: Camera Resource Leak =====
    
    @Test
    void testGCI534_DetectsCameraControllerUsage() {
        // Given
        GCI534CameraResourceLeakRule rule = new GCI534CameraResourceLeakRule();
        String violatingLine = "final controller = CameraController(cameras[0], ResolutionPreset.medium);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI534_DetectsCameraStreamUsage() {
        // Given
        GCI534CameraResourceLeakRule rule = new GCI534CameraResourceLeakRule();
        String violatingLine = "camera.startImageStream((image) => processImage(image));";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI534_AllowsCameraWithProperDispose() {
        // Given
        GCI534CameraResourceLeakRule rule = new GCI534CameraResourceLeakRule();
        String fileContent = "class CameraScreen extends StatefulWidget {\n" +
            "  CameraController controller = CameraController(cameras[0], ResolutionPreset.high);\n" +
            "  @override\n" +
            "  void dispose() {\n" +
            "    controller.dispose();\n" +
            "    super.dispose();\n" +
            "  }\n" +
            "}";
        
        // When
        rule.analyzeFile(sensorContext, inputFile, fileContent);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI530: Torch Free =====
    
    @Test
    void testGCI530_DetectsTorchUsage() {
        // Given
        GCI530TorchFreeRule rule = new GCI530TorchFreeRule();
        String violatingLine = "await controller.setTorchMode(true);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI530_DetectsFlashModeUsage() {
        // Given
        GCI530TorchFreeRule rule = new GCI530TorchFreeRule();
        String violatingLine = "controller.setFlashMode(FlashMode.torch);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI530_AllowsNormalCameraUsage() {
        // Given
        GCI530TorchFreeRule rule = new GCI530TorchFreeRule();
        String validLine = "controller.setFlashMode(FlashMode.off);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
}