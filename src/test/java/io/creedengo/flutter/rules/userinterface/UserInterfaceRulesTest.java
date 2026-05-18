package io.creedengo.flutter.rules.userinterface;

import io.creedengo.flutter.rules.AbstractDartRuleTest;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour les règles de la catégorie User Interface (Interface utilisateur)
 * 
 * Catégorie CNUMR : User Interface
 * Règles testées :
 * - GCI31: Lighter image formats (WebP)
 * - GCI203: Avoid high frequency timers
 * - GCI522: Brightness Override
 * - GCI531: Avoid excessive animations
 * - GCI536: Keep Screen On
 */
public class UserInterfaceRulesTest extends AbstractDartRuleTest {

    // ===== GCI31: Lighter Image Formats =====
    
    @Test
    void testGCI31_DetectsBmpFormat() {
        // Given
        GCI31LighterImageFormatsRule rule = new GCI31LighterImageFormatsRule();
        String violatingLine = "Image.asset('assets/images/logo.bmp')";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Heavy image format detected"));
    }
    
    @Test
    void testGCI31_DetectsTiffFormat() {
        // Given
        GCI31LighterImageFormatsRule rule = new GCI31LighterImageFormatsRule();
        String violatingLine = "NetworkImage('https://example.com/image.tiff')";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("WebP, AVIF or optimized PNG/JPEG"));
    }
    
    @Test
    void testGCI31_DetectsTifFormat() {
        // Given
        GCI31LighterImageFormatsRule rule = new GCI31LighterImageFormatsRule();
        String violatingLine = "File('path/to/image.tif')";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("reduce memory"));
    }
    
    @Test
    void testGCI31_AllowsWebPFormat() {
        // Given
        GCI31LighterImageFormatsRule rule = new GCI31LighterImageFormatsRule();
        String validLine = "Image.asset('assets/images/logo.webp')";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
    
    @Test
    void testGCI31_AllowsPngFormat() {
        // Given
        GCI31LighterImageFormatsRule rule = new GCI31LighterImageFormatsRule();
        String validLine = "Image.network('https://example.com/optimized.png')";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI203: Avoid High Frequency Timers =====
    
    @Test
    void testGCI203_DetectsHighFrequencyTimer() {
        // Given
        GCI203AvoidHighFrequencyTimersRule rule = new GCI203AvoidHighFrequencyTimersRule();
        String violatingLine = "Timer.periodic(Duration(milliseconds: 50), (timer) => updateUI());";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Timer with 50ms interval too frequent"));
    }
    
    @Test
    void testGCI203_DetectsVeryHighFrequencyTimer() {
        // Given
        GCI203AvoidHighFrequencyTimersRule rule = new GCI203AvoidHighFrequencyTimersRule();
        String violatingLine = "Timer.periodic(Duration(milliseconds: 16), callback);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("consumes a lot of energy"));
    }
    
    @Test
    void testGCI203_AllowsReasonableTimer() {
        // Given
        GCI203AvoidHighFrequencyTimersRule rule = new GCI203AvoidHighFrequencyTimersRule();
        String validLine = "Timer.periodic(Duration(milliseconds: 500), (timer) => checkStatus());";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
    
    @Test
    void testGCI203_AllowsSecondBasedTimer() {
        // Given
        GCI203AvoidHighFrequencyTimersRule rule = new GCI203AvoidHighFrequencyTimersRule();
        String validLine = "Timer.periodic(Duration(seconds: 1), updateClock);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI522: Brightness Override =====
    
    @Test
    void testGCI522_DetectsBrightnessOverride() {
        // Given
        GCI522BrightnessOverrideRule rule = new GCI522BrightnessOverrideRule();
        String violatingLine = "SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(brightness: Brightness.light));";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Avoid forcing screen brightness"));
    }
    
    @Test
    void testGCI522_DetectsScreenBrightness() {
        // Given
        GCI522BrightnessOverrideRule rule = new GCI522BrightnessOverrideRule();
        String violatingLine = "Screen.setBrightness(0.8);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("save battery"));
    }
    
    @Test
    void testGCI522_DetectsManualBrightnessValue() {
        // Given
        GCI522BrightnessOverrideRule rule = new GCI522BrightnessOverrideRule();
        String violatingLine = "AppBar(brightness: 0.9)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("automatically adapt brightness"));
    }
    
    @Test
    void testGCI522_AllowsAutomaticBrightness() {
        // Given
        GCI522BrightnessOverrideRule rule = new GCI522BrightnessOverrideRule();
        String validLine = "SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle.dark);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI531: Avoid Excessive Animations =====
    
    @Test
    void testGCI531_DetectsLongAnimation() {
        // Given
        GCI531AvoidExcessiveAnimationsRule rule = new GCI531AvoidExcessiveAnimationsRule();
        String violatingLine = "AnimationController(duration: Duration(seconds: 8), vsync: this);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Animation duration of 8 seconds is too long"));
    }
    
    @Test
    void testGCI531_DetectsInfiniteAnimation() {
        // Given
        GCI531AvoidExcessiveAnimationsRule rule = new GCI531AvoidExcessiveAnimationsRule();
        String violatingLine = "controller.repeat(reverse: true);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Avoid infinite animations"));
    }
    
    @Test
    void testGCI531_DetectsComplexAnimation() {
        // Given
        GCI531AvoidExcessiveAnimationsRule rule = new GCI531AvoidExcessiveAnimationsRule();
        String violatingLine = "Transform.rotate(angle: controller.value, child: widget);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Complex animation detected"));
    }
    
    @Test
    void testGCI531_AllowsShortAnimation() {
        // Given
        GCI531AvoidExcessiveAnimationsRule rule = new GCI531AvoidExcessiveAnimationsRule();
        String validLine = "AnimationController(duration: Duration(milliseconds: 300), vsync: this);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI536: Keep Screen On =====
    
    @Test
    void testGCI536_DetectsWakelockEnable() {
        // Given
        GCI536KeepScreenOnRule rule = new GCI536KeepScreenOnRule();
        String violatingLine = "Wakelock.enable();";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Enabling wakelock prevents the device from sleeping"));
    }
    
    @Test
    void testGCI536_DetectsScreenWakeKeepOn() {
        // Given
        GCI536KeepScreenOnRule rule = new GCI536KeepScreenOnRule();
        String violatingLine = "ScreenWake.keepOn(true);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Keeping the screen on"));
    }
    
    @Test
    void testGCI536_DetectsSystemUIManualMode() {
        // Given
        GCI536KeepScreenOnRule rule = new GCI536KeepScreenOnRule();
        String violatingLine = "SystemChrome.setEnabledSystemUIMode(SystemUiMode.manual);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("Manually controlling system UI mode"));
    }
    
    @Test
    void testGCI536_DetectsWakelockWithoutDisable() {
        // Given
        GCI536KeepScreenOnRule rule = new GCI536KeepScreenOnRule();
        String fileContent = "class MyApp extends StatelessWidget {\n" +
            "  void startWork() {\n" +
            "    Wakelock.enable();\n" +
            "    doLongRunningTask();\n" +
            "  }\n" +
            "}";
        
        // When
        rule.analyzeFile(sensorContext, inputFile, fileContent);
        
        // Then
        verifyIssueCreated();
        verify(newIssueLocation).message(contains("enables wakelock but never disables it"));
    }
    
    @Test
    void testGCI536_AllowsWakelockWithProperDisable() {
        // Given
        GCI536KeepScreenOnRule rule = new GCI536KeepScreenOnRule();
        String fileContent = "class MyApp extends StatelessWidget {\n" +
            "  void startWork() {\n" +
            "    Wakelock.enable();\n" +
            "    doLongRunningTask();\n" +
            "    Wakelock.disable();\n" +
            "  }\n" +
            "}";
        
        // When
        rule.analyzeFile(sensorContext, inputFile, fileContent);
        
        // Then
        verifyNoIssueCreated();
    }
    
    @Test
    void testGCI536_AllowsScreenWakeOff() {
        // Given
        GCI536KeepScreenOnRule rule = new GCI536KeepScreenOnRule();
        String validLine = "ScreenWake.keepOn(false);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
    
    // ===== Helper Methods =====
    
    private void resetMocks() {
        reset(newIssue);
        when(sensorContext.newIssue()).thenReturn(newIssue);
        when(newIssue.forRule(any(RuleKey.class))).thenReturn(newIssue);
        when(newIssue.newLocation()).thenReturn(newIssueLocation);
        when(newIssueLocation.on(any(InputFile.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.at(any(TextRange.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.message(anyString())).thenReturn(newIssueLocation);
        when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);
    }
}