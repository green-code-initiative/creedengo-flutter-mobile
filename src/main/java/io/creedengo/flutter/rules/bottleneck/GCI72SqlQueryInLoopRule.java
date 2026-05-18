package io.creedengo.flutter.rules.bottleneck;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI72 - Perform an SQL query inside a loop.
 * Category: Bottleneck.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo rule. Detects SQL
 * or database calls inside a {@code for} loop or a {@code .forEach} callback.</p>
 */
public class GCI72SqlQueryInLoopRule extends AbstractDartRule {

    private static final Pattern SQL_IN_LOOP_PATTERN =
        Pattern.compile("for\\s*\\([^)]*\\).*(?:database\\.|query\\(|insert\\(|update\\(|delete\\()");

    private static final Pattern SQL_IN_FOREACH_PATTERN =
        Pattern.compile("\\.forEach\\s*\\([^)]*\\).*(?:database\\.|query\\(|insert\\(|update\\(|delete\\()");

    public GCI72SqlQueryInLoopRule() {
        super("GCI72");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (SQL_IN_LOOP_PATTERN.matcher(line).find() || SQL_IN_FOREACH_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "SQL/database query detected inside a loop. Group queries or use batch operations "
                    + "to reduce CPU, RAM and bandwidth consumption.");
        }
    }
}
