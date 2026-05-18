package io.creedengo.flutter.rules.userinterface;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GCI531 - Avoid excessive animations on mobile.
 * Category: User Interface.
 *
 * <p>Flutter-specific extension. Flags long animations
 * (more than {@value #MAX_ANIMATION_SECONDS} s), infinite loops via
 * {@code repeat()} and complex transforms tied to a controller.</p>
 */
public class GCI531AvoidExcessiveAnimationsRule extends AbstractDartRule {
    
    private static final Pattern LONG_ANIMATION_PATTERN = 
        Pattern.compile("duration:\\s*Duration\\([^)]*\\bseconds:\\s*([5-9]|[1-9][0-9]+)");
    
    private static final Pattern INFINITE_ANIMATION_PATTERN = 
        Pattern.compile("repeat\\s*\\(\\s*reverse\\s*:\\s*true\\s*\\)|AnimationController\\([^)]*repeat");
    
    private static final Pattern COMPLEX_ANIMATION_PATTERN = 
        Pattern.compile("Transform\\.(rotate|scale)\\s*\\([^)]*controller");
    
    private static final int MAX_ANIMATION_SECONDS = 5;
    
    public GCI531AvoidExcessiveAnimationsRule() {
        super("GCI531");
    }
    
    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }
        
        // Long animations
        Matcher longAnimationMatcher = LONG_ANIMATION_PATTERN.matcher(line);        if (longAnimationMatcher.find()) {
            int seconds = Integer.parseInt(longAnimationMatcher.group(1));
            createIssue(context, inputFile, lineNumber, 
                "Animation duration of " + seconds + " seconds is too long for mobile. " + 
                "Limit to " + MAX_ANIMATION_SECONDS + " seconds maximum to preserve battery life.");
        }
        
        // Infinite animations
        if (INFINITE_ANIMATION_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber, 
                "Avoid infinite animations on mobile. They continuously consume " +
                "GPU energy and drain the battery.");
        }
        
        // Complex animations
        if (COMPLEX_ANIMATION_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber, 
                "Complex animation detected. On mobile, prefer simple animations " +
                "to reduce GPU load and save battery.");
        }
    }
}