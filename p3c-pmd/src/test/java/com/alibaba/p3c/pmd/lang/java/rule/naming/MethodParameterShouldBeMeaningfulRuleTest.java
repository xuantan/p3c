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
package com.alibaba.p3c.pmd.lang.java.rule.naming;

import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRuleTest;

/**
 * Test cases for MethodParameterShouldBeMeaningfulRule
 *
 * @author p3c-assistant
 * @date 2024/01/01
 */
public class MethodParameterShouldBeMeaningfulRuleTest extends AbstractAliRuleTest {

    @Override
    protected AbstractJavaRule getRule() {
        return new MethodParameterShouldBeMeaningfulRule();
    }
}

class TestExample {
    
    // Positive examples (good naming)
    public void processUser(String userName, int userId) {
        // Good parameter names
    }
    
    public void calculateDistance(double startLatitude, double startLongitude, 
                                double endLatitude, double endLongitude) {
        // Meaningful parameter names
    }
    
    public String formatJson(String jsonData) {
        // Clear parameter name
    }
    
    // Negative examples (bad naming - should trigger violations)
    public void badMethod(String a, int b) {
        // Single letter parameters - violation
    }
    
    public void anotherBadMethod(String temp, Object obj, int var) {
        // Meaningless parameter names - violation
    }
    
    public void processData(String param1, String arg2) {
        // Numbered generic names - violation
    }
    
    // Allowed cases (should not trigger violations)
    public String getUserById(Long id) {
        // 'id' is allowed short name
        return "user";
    }
    
    public void connectToDb(String url, String db) {
        // Technical abbreviations are allowed
    }
    
    // Getters/setters should be ignored
    public String getName() {
        return "name";
    }
    
    public void setName(String n) {
        // Single letter in setter should be ignored
    }
    
    // Override methods should be ignored
    @Override
    public boolean equals(Object o) {
        // Single letter in override method should be ignored
        return false;
    }
}