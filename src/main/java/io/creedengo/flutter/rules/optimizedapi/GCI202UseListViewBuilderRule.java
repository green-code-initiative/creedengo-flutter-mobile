package io.creedengo.flutter.rules.optimizedapi;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI202 - Use {@code ListView.builder} for large lists.
 * Category: Optimized API.
 *
 * <p>Flutter-specific extension. Flags the eager {@code ListView(children: [...])}
 * constructor and recommends the lazy {@code ListView.builder} variant.</p>
 */
public class GCI202UseListViewBuilderRule extends AbstractDartRule {

    private static final Pattern LISTVIEW_CONSTRUCTOR_PATTERN =
        Pattern.compile("ListView\\s*\\(\\s*children:");

    public GCI202UseListViewBuilderRule() {
        super("GCI202");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (LISTVIEW_CONSTRUCTOR_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Use ListView.builder() for large lists instead of the ListView() constructor. "
                    + "This avoids loading every item in memory and saves resources.");
        }
    }
}
