package io.creedengo.flutter.rules.optimizedapi;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GCI523 - Sobriety: Thrifty Geolocation (minTime).
 * Category: Optimized API.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo mobile rule.
 * High-precision geolocation typically draws more power from the device
 * GPS hardware than is needed for most use cases.</p>
 */
public class GCI523ThriftyGeolocationRule extends AbstractDartRule {

    private static final Pattern HIGH_ACCURACY_PATTERN =
        Pattern.compile("LocationAccuracy\\.(best|bestForNavigation)");

    private static final Pattern FREQUENT_LOCATION_PATTERN =
        Pattern.compile("distanceFilter\\s*:\\s*([0-9]+)");

    private static final Pattern LOCATION_SETTINGS_PATTERN =
        Pattern.compile("LocationSettings\\s*\\([^)]*accuracy\\s*:\\s*LocationAccuracy\\.(best|bestForNavigation)");

    private static final int MIN_DISTANCE_FILTER_METERS = 10;

    public GCI523ThriftyGeolocationRule() {
        super("GCI523");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (HIGH_ACCURACY_PATTERN.matcher(line).find()
            || LOCATION_SETTINGS_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Avoid high precision geolocation (best / bestForNavigation) unless required. "
                    + "Use LocationAccuracy.high or medium to save battery.");
        }

        Matcher distanceMatcher = FREQUENT_LOCATION_PATTERN.matcher(line);
        if (distanceMatcher.find()) {
            int distance = Integer.parseInt(distanceMatcher.group(1));
            if (distance < MIN_DISTANCE_FILTER_METERS) {
                createIssue(context, inputFile, lineNumber,
                    "Distance filter too low (" + distance + " m). Use at least "
                        + MIN_DISTANCE_FILTER_METERS + " m to avoid overly frequent updates and save battery.");
            }
        }
    }
}
