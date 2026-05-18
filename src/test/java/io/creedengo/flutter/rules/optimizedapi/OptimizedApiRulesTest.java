package io.creedengo.flutter.rules.optimizedapi;

import io.creedengo.flutter.rules.AbstractDartRuleTest;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour les règles de la catégorie Optimized API (API optimisées)
 * 
 * Catégorie CNUMR : Optimized API
 * Règles testées :
 * - GCI201: Avoid unnecessary widget rebuilds
 * - GCI202: Use ListView.builder for large lists (Lazy Loading)
 * - GCI523: Thrifty Geolocation (API économe)
 * - GCI532: Optimize network usage
 */
public class OptimizedApiRulesTest extends AbstractDartRuleTest {

    // ===== GCI201: Avoid Unnecessary Rebuilds =====
    
    @Test
    void testGCI201_DetectsSetStateInForLoop() {
        // Given
        GCI201AvoidUnnecessaryRebuildsRule rule = new GCI201AvoidUnnecessaryRebuildsRule();
        String violatingLine = "for (int i = 0; i < items.length; i++) { setState(() => counter++); }";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI201_DetectsSetStateInForEach() {
        // Given
        GCI201AvoidUnnecessaryRebuildsRule rule = new GCI201AvoidUnnecessaryRebuildsRule();
        String violatingLine = "items.forEach((item) => setState(() => processItem(item)));";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI201_AllowsSingleSetState() {
        // Given
        GCI201AvoidUnnecessaryRebuildsRule rule = new GCI201AvoidUnnecessaryRebuildsRule();
        String validLine = "setState(() => counter++);";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI202: Use ListView.builder =====
    
    @Test
    void testGCI202_DetectsListViewWithChildren() {
        // Given
        GCI202UseListViewBuilderRule rule = new GCI202UseListViewBuilderRule();
        String violatingLine = "ListView(children: widgets)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI202_DetectsListViewWithChildrenArray() {
        // Given
        GCI202UseListViewBuilderRule rule = new GCI202UseListViewBuilderRule();
        String violatingLine = "return ListView(children: [";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI202_AllowsListViewBuilder() {
        // Given
        GCI202UseListViewBuilderRule rule = new GCI202UseListViewBuilderRule();
        String validLine = "ListView.builder(itemCount: items.length)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI523: Thrifty Geolocation =====
    
    @Test
    void testGCI523_DetectsHighAccuracyLocation() {
        // Given
        GCI523ThriftyGeolocationRule rule = new GCI523ThriftyGeolocationRule();
        String violatingLine = "LocationSettings(accuracy: LocationAccuracy.best)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI523_DetectsBestForNavigationAccuracy() {
        // Given
        GCI523ThriftyGeolocationRule rule = new GCI523ThriftyGeolocationRule();
        String violatingLine = "Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.bestForNavigation)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI523_DetectsLowDistanceFilter() {
        // Given
        GCI523ThriftyGeolocationRule rule = new GCI523ThriftyGeolocationRule();
        String violatingLine = "LocationSettings(distanceFilter: 5)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI523_AllowsHighAccuracy() {
        // Given
        GCI523ThriftyGeolocationRule rule = new GCI523ThriftyGeolocationRule();
        String validLine = "LocationSettings(accuracy: LocationAccuracy.high)";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI532: Optimize Network Usage =====
    
    @Test
    void testGCI532_DetectsUnoptimizedNetworkImage() {
        // Given
        GCI532OptimizeNetworkUsageRule rule = new GCI532OptimizeNetworkUsageRule();
        String violatingLine = "Image.network('https://example.com/large-image.jpg')";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI532_DetectsFrequentPolling() {
        // Given
        GCI532OptimizeNetworkUsageRule rule = new GCI532OptimizeNetworkUsageRule();
        String violatingLine = "Timer.periodic(Duration(seconds: 2), (timer) => http.get(Uri.parse(apiUrl)));";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI532_AllowsReasonablePolling() {
        // Given
        GCI532OptimizeNetworkUsageRule rule = new GCI532OptimizeNetworkUsageRule();
        String validLine = "Timer.periodic(Duration(minutes: 5), (timer) => syncData());";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
}