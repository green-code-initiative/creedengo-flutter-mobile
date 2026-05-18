package io.creedengo.flutter.rules;

import io.creedengo.flutter.CreedengoDartRulesDefinition;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

/**
 * Base class for Creedengo Flutter rules.
 *
 * <p>A rule may inspect each source line independently via
 * {@link #analyzeLine(SensorContext, InputFile, String, int)} or look at the
 * full file content via
 * {@link #analyzeFile(SensorContext, InputFile, String)} (default: no-op).</p>
 */
public abstract class AbstractDartRule {

    protected final String ruleKey;

    protected AbstractDartRule(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    /**
     * Analyses a single source line. Implementations are expected to be
     * cheap as they are called for every line of every Dart file.
     */
    public abstract void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber);

    /**
     * Analyses the full file content. The default implementation does
     * nothing; override when a rule needs cross-line context.
     */
    public void analyzeFile(SensorContext context, InputFile inputFile, String content) {
        // no-op by default
    }

    /**
     * Reports an issue against the current rule on the given line.
     */
    protected void createIssue(SensorContext context, InputFile inputFile, int line, String message) {
        NewIssue newIssue = context.newIssue()
            .forRule(RuleKey.of(CreedengoDartRulesDefinition.REPOSITORY_KEY, ruleKey));

        NewIssueLocation location = newIssue.newLocation()
            .on(inputFile)
            .at(inputFile.selectLine(line))
            .message(message);

        newIssue.at(location).save();
    }
}
