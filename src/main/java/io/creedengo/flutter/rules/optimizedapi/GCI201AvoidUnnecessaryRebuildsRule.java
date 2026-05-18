package io.creedengo.flutter.rules.optimizedapi;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI201 - Avoid unnecessary widget rebuilds.
 * Category: Optimized API.
 *
 * <p>Flutter-specific extension. Detects {@code setState(...)} called from
 * inside a loop or a {@code .forEach} callback, which triggers redundant
 * widget rebuilds.</p>
 */
public class GCI201AvoidUnnecessaryRebuildsRule extends AbstractDartRule {

    private static final Pattern SETSTATE_IN_LOOP_PATTERN =
        Pattern.compile("for\\s*\\([^}]*setState\\s*\\(");

    private static final Pattern SETSTATE_IN_FOREACH_PATTERN =
        Pattern.compile("\\.forEach\\s*\\([^}]*setState\\s*\\(");

    public GCI201AvoidUnnecessaryRebuildsRule() {
        super("GCI201");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (SETSTATE_IN_LOOP_PATTERN.matcher(line).find()
            || SETSTATE_IN_FOREACH_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "setState() called inside a loop can cause excessive rebuilds. "
                    + "Group updates or use a ValueNotifier-like pattern to save GPU energy.");
        }
    }
}
