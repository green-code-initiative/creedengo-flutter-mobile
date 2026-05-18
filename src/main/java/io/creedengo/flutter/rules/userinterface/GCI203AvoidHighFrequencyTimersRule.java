package io.creedengo.flutter.rules.userinterface;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GCI203 - Avoid high-frequency timers.
 * Category: User Interface.
 *
 * <p>Flutter-specific extension. Flags {@code Timer.periodic} calls with
 * an interval shorter than {@value #MIN_INTERVAL_MS} ms.</p>
 */
public class GCI203AvoidHighFrequencyTimersRule extends AbstractDartRule {
    
    private static final Pattern TIMER_PATTERN = 
        Pattern.compile("Timer\\.periodic\\(\\s*Duration\\(milliseconds:\\s*([0-9]+)\\)");
    
    private static final int MIN_INTERVAL_MS = 100;
    
    public GCI203AvoidHighFrequencyTimersRule() {
        super("GCI203");
    }
    
    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }
        
        Matcher matcher = TIMER_PATTERN.matcher(line);
        if (matcher.find()) {
            int interval = Integer.parseInt(matcher.group(1));
            if (interval < MIN_INTERVAL_MS) {
                createIssue(context, inputFile, lineNumber, 
                    "Timer with " + interval + "ms interval too frequent (< " + MIN_INTERVAL_MS + "ms). " +
                    "This consumes a lot of energy and overloads the processor.");
            }
        }
    }
}