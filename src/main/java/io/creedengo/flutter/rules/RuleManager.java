package io.creedengo.flutter.rules;

import io.creedengo.flutter.rules.bottleneck.*;
import io.creedengo.flutter.rules.leakage.*;
import io.creedengo.flutter.rules.optimizedapi.*;
import io.creedengo.flutter.rules.userinterface.*;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Central registry of the Creedengo rules implemented for Dart/Flutter.
 *
 * <p>Rules are grouped by CNUMR category: Leakage, Bottleneck, Optimized API,
 * User Interface. See {@code https://github.com/cnumr/best-practices-mobile}.</p>
 */
public class RuleManager {

    private final List<AbstractDartRule> rules;

    public RuleManager() {
        this.rules = Collections.unmodifiableList(Arrays.asList(
            // Leakage — resource leaks
            new GCI79FreeResourcesRule(),
            new GCI534CameraResourceLeakRule(),
            new GCI530TorchFreeRule(),

            // Bottleneck — inefficient/repetitive operations
            new GCI3CollectionSizeInLoopRule(),
            new GCI72SqlQueryInLoopRule(),
            new GCI535NetworkRequestsInLoopRule(),

            // Optimized API — power-aware APIs
            new GCI201AvoidUnnecessaryRebuildsRule(),
            new GCI202UseListViewBuilderRule(),
            new GCI523ThriftyGeolocationRule(),
            new GCI532OptimizeNetworkUsageRule(),

            // User Interface — UI optimisations
            new GCI31LighterImageFormatsRule(),
            new GCI203AvoidHighFrequencyTimersRule(),
            new GCI522BrightnessOverrideRule(),
            new GCI531AvoidExcessiveAnimationsRule(),
            new GCI536KeepScreenOnRule()
        ));
    }

    /**
     * Runs every rule against a single source line.
     */
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        for (AbstractDartRule rule : rules) {
            rule.analyzeLine(context, inputFile, line, lineNumber);
        }
    }

    /**
     * Runs every rule against the full file content.
     */
    public void analyzeFile(SensorContext context, InputFile inputFile, String content) {
        for (AbstractDartRule rule : rules) {
            rule.analyzeFile(context, inputFile, content);
        }
    }

    /**
     * @return an unmodifiable view of the registered rules.
     */
    public List<AbstractDartRule> getRules() {
        return rules;
    }
}
