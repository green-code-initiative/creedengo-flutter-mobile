package io.creedengo.flutter.rules.bottleneck;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI3 - Getting the size of the collection in the loop.
 * Category: Bottleneck.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo rule. Detects
 * {@code .length} accesses in the condition of a {@code for} loop.</p>
 */
public class GCI3CollectionSizeInLoopRule extends AbstractDartRule {

    private static final Pattern COLLECTION_SIZE_IN_LOOP_PATTERN =
        Pattern.compile("for\\s*\\([^)]*<\\s*\\w+\\.length");

    public GCI3CollectionSizeInLoopRule() {
        super("GCI3");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (COLLECTION_SIZE_IN_LOOP_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Avoid calling .length in loop condition. Store the size in a variable to save CPU cycles.");
        }
    }
}
