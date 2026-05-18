package io.creedengo.flutter.rules.leakage;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

/**
 * GCI534 - Avoid camera resource leaks.
 * Category: Leakage.
 *
 * <p>Detects {@code CameraController} usages that are not paired with a
 * {@code dispose()} call. Inspired by the CNUMR best practices around
 * camera resource handling. Flutter-specific extension.</p>
 */
public class GCI534CameraResourceLeakRule extends AbstractDartRule {

    public static final String RULE_KEY = "GCI534";

    public GCI534CameraResourceLeakRule() {
        super(RULE_KEY);
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        String trimmedLine = line.trim();

        // CameraController instantiation not necessarily paired with dispose()
        if (trimmedLine.contains("CameraController(") || trimmedLine.contains("new CameraController(")) {
            createIssue(context, inputFile, lineNumber,
                "Camera controller should be properly disposed in dispose() method to prevent resource leaks.");
        }

        // Image/video streams that need explicit teardown
        if (trimmedLine.contains("camera.")
            && (trimmedLine.contains("startImageStream") || trimmedLine.contains("startVideoRecording"))) {
            createIssue(context, inputFile, lineNumber,
                "Camera operations should be properly managed and resources freed when no longer needed.");
        }
    }

    @Override
    public void analyzeFile(SensorContext context, InputFile inputFile, String content) {
        if (content == null) {
            return;
        }

        // Global check: file uses CameraController but does not call dispose() anywhere.
        if (content.contains("CameraController") && !content.contains("dispose()")) {
            createIssue(context, inputFile, 1,
                "File contains CameraController usage but no dispose() method found. "
                    + "Camera resources must be properly freed to prevent battery drain.");
        }
    }
}
