package io.creedengo.flutter.rules.userinterface;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI522 - Sobriety: Brightness Override.
 * Category: User Interface.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo mobile rule.
 * iOS and Android already adapt screen brightness to ambient light to
 * preserve battery; overriding it manually wastes energy.</p>
 */
public class GCI522BrightnessOverrideRule extends AbstractDartRule {
    
    private static final Pattern BRIGHTNESS_OVERRIDE_PATTERN = 
        Pattern.compile("(Screen\\.setBrightness|brightness\\s*:|SystemChrome\\.setSystemUIOverlayStyle.*brightness)");
    
    private static final Pattern BRIGHTNESS_MANUAL_PATTERN = 
        Pattern.compile("brightness\\s*:\\s*[0-9.]+");
    
    public GCI522BrightnessOverrideRule() {
        super("GCI522");
    }
    
    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }
        
        if (BRIGHTNESS_OVERRIDE_PATTERN.matcher(line).find() || BRIGHTNESS_MANUAL_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber, 
                "Avoid forcing screen brightness. iOS and Android devices automatically " +
                "adapt brightness according to ambient light to save battery.");
        }
    }
}