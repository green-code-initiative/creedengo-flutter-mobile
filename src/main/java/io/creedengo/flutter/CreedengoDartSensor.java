package io.creedengo.flutter;

import io.creedengo.flutter.rules.RuleManager;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;

/**
 * SonarQube sensor that scans Dart source files and delegates the actual
 * analysis to {@link RuleManager}. The sensor only runs on files registered
 * under the {@code dart} language.
 */
public class CreedengoDartSensor implements Sensor {

    private static final Logger LOG = Loggers.get(CreedengoDartSensor.class);

    private final RuleManager ruleManager;

    public CreedengoDartSensor() {
        this.ruleManager = new RuleManager();
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .onlyOnLanguage(DartLanguage.KEY)
            .name("Creedengo Dart Sensor")
            .onlyOnFileType(InputFile.Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fileSystem = context.fileSystem();

        for (InputFile inputFile : fileSystem.inputFiles(fileSystem.predicates().hasLanguage(DartLanguage.KEY))) {
            try {
                analyzeFile(context, inputFile);
            } catch (IOException e) {
                LOG.error("Failed to analyse file: " + inputFile.filename(), e);
            }
        }
    }

    private void analyzeFile(SensorContext context, InputFile inputFile) throws IOException {
        String content = inputFile.contents();
        String[] lines = content.split("\n");

        for (int index = 0; index < lines.length; index++) {
            String line = lines[index].trim();
            int lineNumber = index + 1;
            ruleManager.analyzeLine(context, inputFile, line, lineNumber);
        }

        ruleManager.analyzeFile(context, inputFile, content);
    }
}
