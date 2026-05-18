package io.creedengo.flutter.rules.bottleneck;

import io.creedengo.flutter.rules.AbstractDartRuleTest;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour les règles de la catégorie Bottleneck (Goulots d'étranglement)
 * 
 * Catégorie CNUMR : Bottleneck
 * Règles testées :
 * - GCI3: Collection size in loop
 * - GCI72: SQL query in loop  
 * - GCI535: Network requests in loop
 */
public class BottleneckRulesTest extends AbstractDartRuleTest {

    // ===== GCI3: Collection Size In Loop =====
    
    @Test
    void testGCI3_DetectsCollectionSizeInLoop() {
        // Given
        GCI3CollectionSizeInLoopRule rule = new GCI3CollectionSizeInLoopRule();
        String violatingLine = "for (int i = 0; i < items.length; i++) {";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI3_AllowsOptimizedLoop() {
        // Given
        GCI3CollectionSizeInLoopRule rule = new GCI3CollectionSizeInLoopRule();
        String validLine = "int len = items.length; for (int i = 0; i < len; i++) {";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
    
    @Test
    void testGCI3_AllowsFixedSizeLoop() {
        // Given
        GCI3CollectionSizeInLoopRule rule = new GCI3CollectionSizeInLoopRule();
        String validLine = "for (int i = 0; i < 10; i++) {";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI72: SQL Query In Loop =====
    
    @Test
    void testGCI72_DetectsSqlQueryInForLoop() {
        // Given
        GCI72SqlQueryInLoopRule rule = new GCI72SqlQueryInLoopRule();
        String violatingLine = "for (var item in items) { database.query('SELECT * FROM table'); }";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI72_DetectsDatabaseInsertInLoop() {
        // Given
        GCI72SqlQueryInLoopRule rule = new GCI72SqlQueryInLoopRule();
        String violatingLine = "for (int i = 0; i < data.length; i++) { database.insert('users', data[i]); }";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI72_AllowsSingleQuery() {
        // Given
        GCI72SqlQueryInLoopRule rule = new GCI72SqlQueryInLoopRule();
        String validLine = "var result = database.query('SELECT * FROM users');";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }

    // ===== GCI535: Network Requests In Loop =====
    
    @Test
    void testGCI535_DetectsHttpGetInLoop() {
        // Given
        GCI535NetworkRequestsInLoopRule rule = new GCI535NetworkRequestsInLoopRule();
        String violatingLine = "for (var url in urls) { http.get(Uri.parse(url)); }";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI535_DetectsHttpPostInForEach() {
        // Given
        GCI535NetworkRequestsInLoopRule rule = new GCI535NetworkRequestsInLoopRule();
        String violatingLine = "items.forEach((item) => http.post(Uri.parse(apiUrl)));";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, violatingLine, 1);
        
        // Then
        verifyIssueCreated();
    }
    
    @Test
    void testGCI535_AllowsSingleNetworkCall() {
        // Given
        GCI535NetworkRequestsInLoopRule rule = new GCI535NetworkRequestsInLoopRule();
        String validLine = "var response = await http.get(Uri.parse(apiUrl));";
        
        // When
        rule.analyzeLine(sensorContext, inputFile, validLine, 1);
        
        // Then
        verifyNoIssueCreated();
    }
}