package io.creedengo.flutter.rules.optimizedapi;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI532 - Optimize network usage on mobile.
 * Category: Optimized API.
 *
 * <p>Flutter-specific extension. Flags a few common patterns of
 * unoptimized network usage: oversized network images, frequent polling,
 * disabled compression, background sync.</p>
 */
public class GCI532OptimizeNetworkUsageRule extends AbstractDartRule {

    private static final Pattern LARGE_IMAGE_DOWNLOAD_PATTERN =
        Pattern.compile("Image\\.network\\([^)]*\\)(?![^;]*cache(Width|Height))");

    private static final Pattern FREQUENT_POLLING_PATTERN =
        Pattern.compile("Timer\\.periodic\\(\\s*Duration\\([^)]*seconds:\\s*[1-5]\\s*\\)[^}]*http\\.");

    private static final Pattern NO_COMPRESSION_PATTERN =
        Pattern.compile("(gzip\\s*:\\s*false|compress\\s*:\\s*false)");

    private static final Pattern BACKGROUND_SYNC_PATTERN =
        Pattern.compile("(BackgroundTask|WorkManager).*http\\.");

    public GCI532OptimizeNetworkUsageRule() {
        super("GCI532");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (LARGE_IMAGE_DOWNLOAD_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Network image without size optimisation. Use cacheWidth/cacheHeight "
                    + "to avoid downloading oversized images on mobile.");
        }

        if (FREQUENT_POLLING_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Network polling looks too frequent. Increase the interval to save battery and data.");
        }

        if (NO_COMPRESSION_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Compression disabled for network requests. Enable compression "
                    + "to reduce data usage and battery drain on mobile.");
        }

        if (BACKGROUND_SYNC_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Background network sync detected. Limit such operations to preserve battery on mobile.");
        }
    }
}
