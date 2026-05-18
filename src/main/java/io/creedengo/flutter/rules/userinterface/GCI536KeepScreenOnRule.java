package io.creedengo.flutter.rules.userinterface;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

/**
 * GCI536 - Avoid keeping the screen on unnecessarily.
 * Category: User Interface.
 *
 * <p>Flutter-specific extension inspired by the CNUMR "Keep Screen On"
 * best practice. Flags APIs that prevent the device from going to sleep
 * (wakelock variants, manual system UI mode, etc.).</p>
 */
public class GCI536KeepScreenOnRule extends AbstractDartRule {

    public static final String RULE_KEY = "GCI536";

    public GCI536KeepScreenOnRule() {
        super(RULE_KEY);
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        String trimmedLine = line.trim();

        // SystemChrome.setEnabledSystemUIMode in manual mode can keep the screen on.
        if (trimmedLine.contains("SystemChrome.setEnabledSystemUIMode")
            && trimmedLine.contains("SystemUiMode.manual")) {
            createIssue(context, inputFile, lineNumber,
                "Manually controlling system UI mode can prevent the device from entering sleep mode. "
                    + "Use this feature only when strictly necessary and restore the default behaviour when done.");
        }

        // Wakelock-style plugins.
        if (trimmedLine.contains("Wakelock.enable")
            || trimmedLine.contains("wakelock.enable")
            || trimmedLine.contains("WakelockPlus.enable")) {
            createIssue(context, inputFile, lineNumber,
                "Enabling wakelock prevents the device from sleeping and drains the battery rapidly. "
                    + "Disable it as soon as possible with Wakelock.disable().");
        }

        // screen_wake plugin.
        if (trimmedLine.contains("ScreenWake.keepOn(true)")
            || (trimmedLine.contains("screen_wake") && trimmedLine.contains("true"))) {
            createIssue(context, inputFile, lineNumber,
                "Keeping the screen on prevents power saving and drains the battery. "
                    + "Use this feature sparingly and turn it off when no longer needed.");
        }

        // Forced landscape orientation outside of an obvious media context.
        if (trimmedLine.contains("SystemChrome.setPreferredOrientations")
            && trimmedLine.contains("DeviceOrientation.landscape")
            && !trimmedLine.contains("video") && !trimmedLine.contains("player")) {
            createIssue(context, inputFile, lineNumber,
                "Forcing landscape orientation permanently can hurt user experience and battery life. "
                    + "Allow automatic orientation or restore portrait mode when appropriate.");
        }
    }

    @Override
    public void analyzeFile(SensorContext context, InputFile inputFile, String content) {
        if (content == null) {
            return;
        }

        // Wakelock enabled but never disabled.
        if ((content.contains("Wakelock.enable") || content.contains("wakelock.enable"))
            && !content.contains("Wakelock.disable") && !content.contains("wakelock.disable")) {
            createIssue(context, inputFile, 1,
                "File enables wakelock but never disables it. This causes continuous battery drain. "
                    + "Always pair enable() with disable().");
        }

        // ScreenWake kept on but never restored.
        if (content.contains("ScreenWake.keepOn(true)")
            && !content.contains("ScreenWake.keepOn(false)")) {
            createIssue(context, inputFile, 1,
                "File keeps the screen on but never restores normal sleep behaviour. "
                    + "Always restore the default once the feature is no longer needed.");
        }
    }
}
