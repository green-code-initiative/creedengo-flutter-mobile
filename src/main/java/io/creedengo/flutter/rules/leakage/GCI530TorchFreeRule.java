package io.creedengo.flutter.rules.leakage;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import java.util.regex.Pattern;

/**
 * GCI530 - Sobriety: Torch Free.
 * Category: Leakage.
 *
 * <p>Adapted for Dart/Flutter from the official Creedengo mobile rule.
 * Turning the torch on programmatically should be avoided since the
 * flashlight is one of the most energy-intensive components on a phone.</p>
 */
public class GCI530TorchFreeRule extends AbstractDartRule {

    private static final Pattern TORCH_USAGE_PATTERN =
        Pattern.compile("(torch|flashlight|setTorchMode|TorchMode\\.on|flash.*on)");

    private static final Pattern CAMERA_FLASH_PATTERN =
        Pattern.compile("(FlashMode\\.(torch|on|always)|flash.*Mode.*torch)");

    public GCI530TorchFreeRule() {
        super("GCI530");
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        if (TORCH_USAGE_PATTERN.matcher(line).find() || CAMERA_FLASH_PATTERN.matcher(line).find()) {
            createIssue(context, inputFile, lineNumber,
                "Avoid turning the torch or flash on programmatically. The torch is one of the most "
                    + "energy-intensive components on a device and drains the battery quickly.");
        }
    }
}
