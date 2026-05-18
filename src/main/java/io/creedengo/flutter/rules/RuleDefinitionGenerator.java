package io.creedengo.flutter.rules;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

/**
 * Generates Creedengo rule metadata for Dart/Flutter, grouped by CNUMR
 * category (Optimized API, Leakage, Bottleneck, Sobriety, Idleness).
 *
 * <p>Each rule entry registers a description, severity, type and tag set
 * that SonarQube exposes in the Rules and Quality Profiles UIs.</p>
 */
public final class RuleDefinitionGenerator {

    private RuleDefinitionGenerator() {
        // utility class
    }

    /** Registers every rule on the given repository. */
    public static void createAllRules(RulesDefinition.NewRepository repository) {
        createOptimizedAPIRules(repository);
        createLeakageRules(repository);
        createBottleneckRules(repository);
        createSobrietyRules(repository);
        createIdlenessRules(repository);
    }

    /** Optimized API category — power-aware APIs. */
    public static void createOptimizedAPIRules(RulesDefinition.NewRepository repository) {

        repository.createRule("GCI202")
            .setName("Use ListView.builder for large lists")
            .setHtmlDescription(buildHtmlDescription(
                "ListView() loads every child in memory, including those that are not visible, "
                    + "wasting RAM. ListView.builder() instantiates children lazily, only for the "
                    + "items that need to be displayed.",
                "Eagerly loading every list item allocates more memory than needed and slows down "
                    + "the first paint.",
                "Use ListView.builder() so children are created on demand.",
                "ListView(children: items.map((item) => ItemWidget(item)).toList())",
                "ListView.builder(itemCount: items.length, itemBuilder: (context, i) => ItemWidget(items[i]))"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "memory", "performance", "lazy-loading");
    }

    /** Leakage category — resource leaks. */
    public static void createLeakageRules(RulesDefinition.NewRepository repository) {

        repository.createRule("GCI79")
            .setName("Free resources")
            .setHtmlDescription(buildHtmlDescription(
                "Resources that implement a disposal pattern (controllers, streams, timers, ...) "
                    + "must be freed to release memory and stop background processing.",
                "Resources that are never released keep consuming memory and energy in the background.",
                "Override dispose() in your State and release every long-lived resource you opened.",
                "class _MyWidgetState extends State<MyWidget> { /* no dispose() */ }",
                "@override\nvoid dispose() {\n  controller.dispose();\n  super.dispose();\n}"
            ))
            .setSeverity(Severity.CRITICAL)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.BUG)
            .addTags("creedengo", "eco-design", "memory", "lifecycle", "leak");

        repository.createRule("GCI534")
            .setName("Avoid camera resource leaks")
            .setHtmlDescription(buildHtmlDescription(
                "Camera controllers must be disposed to release the camera hardware and stop "
                    + "background streams.",
                "An undisposed CameraController keeps the camera busy, drains the battery and "
                    + "prevents other apps from using it.",
                "Call controller.dispose() from the widget's dispose() method.",
                "// CameraController instantiated but never disposed",
                "@override\nvoid dispose() {\n  _cameraController?.dispose();\n  super.dispose();\n}"
            ))
            .setSeverity(Severity.CRITICAL)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.BUG)
            .addTags("creedengo", "eco-design", "camera", "leak", "resources");
    }

    /** Bottleneck category — inefficient or repeated operations. */
    public static void createBottleneckRules(RulesDefinition.NewRepository repository) {

        repository.createRule("GCI3")
            .setName("Getting the size of the collection in the loop")
            .setHtmlDescription(buildHtmlDescription(
                "Reading the size of a collection on every iteration burns CPU cycles for nothing. "
                    + "Read it once before the loop.",
                "Calling .length on each iteration costs CPU and battery.",
                "Cache the size in a local variable, or iterate directly over the collection.",
                "for (int i = 0; i < list.length; i++) { ... }",
                "final length = list.length;\nfor (int i = 0; i < length; i++) { ... }"
            ))
            .setSeverity(Severity.MINOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "performance", "loop");

        repository.createRule("GCI72")
            .setName("Perform an SQL query inside a loop")
            .setHtmlDescription(buildHtmlDescription(
                "Database engines are optimised for batch operations. Issuing one query per "
                    + "iteration multiplies CPU, RAM and bandwidth usage.",
                "Per-row queries inside a loop cause N+1 patterns and stress the database server.",
                "Group queries with a batch operation or a single bulk statement.",
                "for (final user in users) {\n  await database.insert('users', user.toMap());\n}",
                "await database.batch((batch) {\n  for (final user in users) batch.insert('users', user.toMap());\n});"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "database", "performance");

        repository.createRule("GCI532")
            .setName("Optimize network usage on mobile")
            .setHtmlDescription(buildHtmlDescription(
                "Unoptimised network usage on mobile drains the battery through radio activity "
                    + "and CPU work spent decoding the response.",
                "Oversized images, frequent polling and uncompressed payloads waste battery and data.",
                "Resize images server-side, cache responses, increase polling intervals, enable compression.",
                "Image.network(url) // no cacheWidth/cacheHeight",
                "Image.network(url, cacheWidth: 200, cacheHeight: 200)"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "mobile", "network", "battery", "cache");

        repository.createRule("GCI535")
            .setName("Avoid network requests in loops")
            .setHtmlDescription(buildHtmlDescription(
                "Sequential HTTP requests inside a loop multiply latency and battery consumption "
                    + "since network operations are among the most expensive on mobile.",
                "Issuing one request per item drains the radio and the battery.",
                "Use a batch endpoint, parallelise the requests with Future.wait, or stream the data.",
                "for (final id in ids) {\n  await http.get('/api/item/$id');\n}",
                "await http.post('/api/items/batch', body: jsonEncode(ids));"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "network", "loop", "battery");
    }

    /** Sobriety category — energy sobriety. */
    public static void createSobrietyRules(RulesDefinition.NewRepository repository) {

        repository.createRule("GCI31")
            .setName("Lighter formats should be used for image files")
            .setHtmlDescription(buildHtmlDescription(
                "Heavy image formats such as BMP or TIFF use significantly more memory and "
                    + "bandwidth than modern formats. WebP for instance gives ~30% better "
                    + "compression than JPEG with no quality loss.",
                "Heavy formats inflate APK size, memory usage and download time.",
                "Prefer WebP, AVIF or properly compressed PNG/JPEG assets.",
                "Image.asset('image.bmp')",
                "Image.asset('image.webp')"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "memory", "network", "images", "webp");

        repository.createRule("GCI522")
            .setName("Sobriety: Brightness Override")
            .setHtmlDescription(buildHtmlDescription(
                "iOS and Android adapt screen brightness to ambient light to preserve battery. "
                    + "Overriding this behaviour disables an automatic optimisation.",
                "Forcing the brightness defeats the auto-brightness algorithm and drains the battery.",
                "Let the system manage brightness automatically.",
                "Screen.setBrightness(1.0);",
                "// rely on system-managed brightness"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "mobile", "battery", "brightness");

        repository.createRule("GCI523")
            .setName("Sobriety: Thrifty Geolocation (minTime)")
            .setHtmlDescription(buildHtmlDescription(
                "High-precision geolocation pushes the GPS hardware harder than necessary for "
                    + "most use cases. Picking a coarser accuracy and a sensible distance filter "
                    + "saves battery.",
                "Best/bestForNavigation accuracy with a small distance filter wakes the GPS chip "
                    + "constantly.",
                "Use LocationAccuracy.high (or coarser) and a distance filter ≥ 10 m.",
                "LocationAccuracy.bestForNavigation",
                "LocationAccuracy.high"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "mobile", "gps", "battery", "location");

        repository.createRule("GCI530")
            .setName("Sobriety: Torch Free")
            .setHtmlDescription(buildHtmlDescription(
                "Programmatically turning the torch on must be avoided because the flashlight "
                    + "is one of the most energy-intensive components on a mobile device.",
                "An always-on torch drains the battery very quickly.",
                "Avoid enabling the torch programmatically unless strictly required.",
                "controller.setTorchMode(true);",
                "// avoid programmatic torch usage"
            ))
            .setSeverity(Severity.CRITICAL)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "mobile", "torch", "battery");

        repository.createRule("GCI531")
            .setName("Avoid excessive animations on mobile")
            .setHtmlDescription(buildHtmlDescription(
                "Long or infinite animations keep the GPU busy and drain the battery on mobile "
                    + "devices. Decorative animations should be kept short.",
                "Animations longer than five seconds, or that loop forever, waste GPU energy.",
                "Cap animation duration to ~5 s and avoid infinite repeats outside of essential UI.",
                "duration: Duration(seconds: 10)",
                "duration: Duration(milliseconds: 300)"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "mobile", "animation", "gpu", "battery");
    }

    /** Idleness category — wake / idle management. */
    public static void createIdlenessRules(RulesDefinition.NewRepository repository) {

        repository.createRule("GCI203")
            .setName("Avoid high-frequency timers")
            .setHtmlDescription(buildHtmlDescription(
                "Timers with very short intervals (< 100 ms) keep the CPU active and prevent the "
                    + "device from entering low-power states.",
                "Tight Timer.periodic loops disable CPU sleep optimisations and burn battery.",
                "Use intervals ≥ 100 ms, or rely on Flutter's optimised AnimationController for UI updates.",
                "Timer.periodic(Duration(milliseconds: 10), callback);",
                "Timer.periodic(Duration(milliseconds: 500), callback);"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "performance", "energy", "timer");

        repository.createRule("GCI201")
            .setName("Avoid unnecessary widget rebuilds")
            .setHtmlDescription(buildHtmlDescription(
                "Calling setState() inside a loop forces the framework to rebuild the widget "
                    + "subtree on every iteration, wasting CPU and GPU.",
                "Repeated rebuilds during a single logical update are pure overhead.",
                "Batch the work then call setState() once, or use ValueNotifier/Provider/Bloc.",
                "for (final item in items) { setState(() => processItem(item)); }",
                "setState(() { for (final item in items) processItem(item); });"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "performance", "flutter", "rebuild");

        repository.createRule("GCI536")
            .setName("Avoid keeping the screen on unnecessarily")
            .setHtmlDescription(buildHtmlDescription(
                "Keeping the screen on prevents the device from entering sleep mode and drains "
                    + "the battery rapidly.",
                "Wakelocks and similar APIs that keep the display on are expensive.",
                "Only keep the screen on while strictly required, and release the wakelock as soon "
                    + "as the user-facing operation is finished.",
                "Wakelock.enable(); // never disabled",
                "Wakelock.enable(); ... Wakelock.disable();"
            ))
            .setSeverity(Severity.MAJOR)
            .setStatus(RuleStatus.READY)
            .setType(RuleType.CODE_SMELL)
            .addTags("creedengo", "eco-design", "mobile", "screen", "battery", "sleep");
    }

    private static String buildHtmlDescription(String description, String problem, String solution,
                                               String badExample, String goodExample) {
        return "<p>" + description + "</p>"
            + "<h2>Problem</h2><p>" + problem + "</p>"
            + "<h2>Solution</h2><p>" + solution + "</p>"
            + "<h2>Non-compliant example</h2><pre>" + badExample + "</pre>"
            + "<h2>Compliant example</h2><pre>" + goodExample + "</pre>";
    }
}
