/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.p3c.pmd.lang.java.rule.comment;

import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRuleTest;

/**
 * Test cases for MethodComplexityMustHaveCommentRule
 *
 * @author p3c-assistant
 * @date 2024/01/01
 */
public class MethodComplexityMustHaveCommentRuleTest extends AbstractAliRuleTest {

    @Override
    protected AbstractJavaRule getRule() {
        return new MethodComplexityMustHaveCommentRule();
    }
}

class ComplexMethodTestExample {
    
    // Positive example: Complex method with proper documentation
    /**
     * Process user data with complex business logic.
     * This method handles user authentication, data validation,
     * and business rule application for multiple user types.
     * 
     * @param userId the unique identifier of the user
     * @param userData the user data to be processed
     * @param options processing options and configurations
     * @param callback callback function for async processing
     * @return processed result containing status and data
     * @throws IllegalArgumentException if user data is invalid
     */
    public ProcessResult processComplexUserData(Long userId, UserData userData, 
                                               ProcessOptions options, ProcessCallback callback) 
                                               throws IllegalArgumentException {
        // Complex method implementation with many lines
        if (userId == null || userData == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        
        // Authenticate user
        authenticateUser(userId);
        
        // Validate data
        validateUserData(userData);
        
        // Apply business rules
        applyBusinessRules(userData, options);
        
        // Process data
        ProcessResult result = new ProcessResult();
        result.setStatus("SUCCESS");
        result.setData(userData);
        
        // Execute callback
        if (callback != null) {
            callback.onComplete(result);
        }
        
        return result;
    }
    
    // Negative examples: Complex methods without proper documentation
    
    // Method with many parameters but no comment - should trigger violation
    public String processData(String data, String format, String encoding, 
                             String output, boolean validate) {
        return data;
    }
    
    // Method with simple comment but complex implementation - should trigger violation
    /**
     * processLongMethod
     */
    public void processLongMethod(String input) {
        // This method has many lines but insufficient documentation
        String step1 = input.trim();
        String step2 = step1.toLowerCase();
        String step3 = step2.replace(" ", "_");
        String step4 = step3.substring(0, Math.min(step3.length(), 10));
        String step5 = step4 + "_processed";
        String step6 = step5.toUpperCase();
        String step7 = step6.replace("_", "-");
        String step8 = step7 + "_final";
        String step9 = step8.trim();
        String step10 = step9.toLowerCase();
        String step11 = step10.replace("-", " ");
        String step12 = step11.trim();
        String step13 = step12.toUpperCase();
        String step14 = step13.replace(" ", "_");
        String step15 = step14 + "_done";
        System.out.println(step15);
    }
    
    // Simple methods should not trigger violations
    public String getName() {
        return "name";
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // Override methods should be ignored
    @Override
    public String toString() {
        return "ComplexMethodTestExample";
    }
    
    private String name;
}