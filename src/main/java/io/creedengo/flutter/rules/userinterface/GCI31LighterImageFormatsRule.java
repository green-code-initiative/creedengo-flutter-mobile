package io.creedengo.flutter.rules.userinterface;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI31 - Lighter formats should be used for image files.
 * Category: User Interface.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo rule. Detects
 * heavy image formats such as BMP or TIFF in code, asset paths and URLs.</p>
 */
public class GCI31LighterImageFormatsRule extends AbstractDartRule {
    
    private static final Pattern HEAVY_IMAGE_FORMAT_PATTERN = 
        Pattern.compile("\\.(bmp|tiff?)\\b", Pattern.CASE_INSENSITIVE);
    
    public GCI31LighterImageFormatsRule() {
        super("GCI31");
    }
    
    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }
        
        if (HEAVY_IMAGE_FORMAT_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber, 
                "Heavy image format detected (BMP/TIFF). Use lighter formats like WebP, AVIF " +
                "or optimized PNG/JPEG to reduce memory and network consumption.");
        }
    }
}