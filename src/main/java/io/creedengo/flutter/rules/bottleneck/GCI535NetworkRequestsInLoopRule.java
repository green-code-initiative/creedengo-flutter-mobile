package io.creedengo.flutter.rules.bottleneck;

import io.creedengo.flutter.rules.AbstractDartRule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

/**
 * GCI535 - Avoid network requests in loops.
 * Category: Bottleneck.
 *
 * <p>Detects HTTP/network calls executed inside a loop, which drain the
 * battery quickly. Inspired by the CNUMR "Internet In The Loop" best
 * practice. Flutter-specific extension.</p>
 */
public class GCI535NetworkRequestsInLoopRule extends AbstractDartRule {

    public static final String RULE_KEY = "GCI535";

    public GCI535NetworkRequestsInLoopRule() {
        super(RULE_KEY);
    }

    @Override
    public void analyzeLine(SensorContext context, InputFile inputFile, String line, int lineNumber) {
        if (line == null) {
            return;
        }

        String trimmedLine = line.trim();

        if (isInLoop(trimmedLine) && containsNetworkCall(trimmedLine)) {
            createIssue(context, inputFile, lineNumber,
                "Network requests inside loops are extremely battery-inefficient. "
                    + "Consider batching requests or using bulk APIs.");
        }
    }

    @Override
    public void analyzeFile(SensorContext context, InputFile inputFile, String content) {
        if (content == null) {
            return;
        }

        String[] lines = content.split("\n");
        boolean inLoop = false;
        int loopStartLine = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Detect the start of a loop (approximate)
            if (line.matches(".*\\b(for|while|do)\\s*\\(.*")
                || line.contains("for (") || line.contains("while (")
                || line.contains(".forEach(") || line.contains(".map(")) {
                inLoop = true;
                loopStartLine = i + 1;
            }

            // Detect the end of a loop (approximate)
            if (inLoop && line.contains("}") && !line.contains("{")) {
                inLoop = false;
            }

            if (inLoop && containsNetworkCall(line)) {
                createIssue(context, inputFile, i + 1,
                    "Network request found inside loop (started at line " + loopStartLine + "). "
                        + "This pattern drains the battery rapidly. Consider using batch APIs "
                        + "or collecting data first.");
            }
        }
    }

    private boolean isInLoop(String line) {
        return line.matches(".*\\b(for|while|do)\\s*\\(.*")
            || line.contains("for (") || line.contains("while (")
            || line.contains(".forEach") || line.contains(".map");
    }

    private boolean containsNetworkCall(String line) {
        return line.contains("http.get(") || line.contains("http.post(")
            || line.contains("http.put(") || line.contains("http.delete(")
            || line.contains("dio.get(") || line.contains("dio.post(")
            || line.contains("client.get(") || line.contains("client.post(")
            || line.contains("await get(") || line.contains("await post(")
            || line.contains("fetch(") || line.contains("request(")
            || line.contains("HttpClient") || line.contains("XMLHttpRequest");
    }
}
