package io.creedengo.flutter.rules.leakage;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI79 - Free resources.
 * Category: Leakage.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo rule. Detects
 * {@code State} subclasses that do not override {@code dispose()} to free
 * the resources they hold (controllers, timers, streams, ...).</p>
 */
public class GCI79FreeResourcesRule extends AbstractDartRule {

    private static final Pattern STATE_CLASS_PATTERN =
        Pattern.compile("class\\s+(\\w+)\\s+extends\\s+State");

    private static final Pattern DISPOSE_METHOD_PATTERN =
        Pattern.compile("@override\\s+void\\s+dispose\\s*\\(\\s*\\)");

    private boolean hasStateClass = false;
    private boolean hasDisposeMethod = false;

    public GCI79FreeResourcesRule() {
        super("GCI79");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (STATE_CLASS_PATTERN.matcher(line).find()) {
            hasStateClass = true;
        }

        if (DISPOSE_METHOD_PATTERN.matcher(line).find()) {
            hasDisposeMethod = true;
        }
    }

    @Override
    public void analyzeFile(SensorContext context, InputFile inputFile, String content) {
        if (content == null) {
            return;
        }

        // Reset state for this file
        hasStateClass = false;
        hasDisposeMethod = false;

        if (STATE_CLASS_PATTERN.matcher(content).find()) {
            hasStateClass = true;
        }

        if (DISPOSE_METHOD_PATTERN.matcher(content).find()
            || content.contains("void dispose()")
            || content.contains("dispose()")) {
            hasDisposeMethod = true;
        }

        if (hasStateClass && !hasDisposeMethod) {
            createIssue(context, inputFile, 1,
                "State class without dispose() method. Free resources (controllers, timers, streams) "
                    + "to avoid memory leaks and continuous energy consumption.");
        }
    }
}
