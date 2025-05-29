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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * [Recommended] Method parameters should have meaningful names, avoid using single letters or meaningless abbreviations.
 *
 * @author p3c-assistant
 * @date 2024/01/01
 */
public class MethodParameterShouldBeMeaningfulRule extends AbstractAliRule {

    private static final Pattern SINGLE_LETTER_PATTERN = Pattern.compile("^[a-zA-Z]$");
    
    private static final Set<String> MEANINGLESS_NAMES = new HashSet<>(Arrays.asList(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "tmp", "temp", "obj", "data", "val", "var", "param", "arg"
    ));
    
    private static final Set<String> ALLOWED_SHORT_NAMES = new HashSet<>(Arrays.asList(
        "id", "ip", "os", "io", "ui", "db", "url", "uri", "api", "xml", "json", "sql",
        "dto", "dao", "vo", "bo", "po", "do"
    ));

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // Skip getters, setters, and overridden methods
        if (isGetterOrSetter(node) || hasOverrideAnnotation(node)) {
            return super.visit(node, data);
        }

        List<ASTFormalParameter> parameters = node.findDescendantsOfType(ASTFormalParameter.class);
        
        for (ASTFormalParameter parameter : parameters) {
            String paramName = parameter.getVariableName();
            
            if (paramName != null && isMeaninglessParameterName(paramName)) {
                ViolationUtils.addViolationWithPrecisePosition(this, parameter, data,
                    I18nResources.getMessage("java.naming.MethodParameterShouldBeMeaningfulRule.violation.msg",
                        paramName, node.getMethodName()));
            }
        }
        
        return super.visit(node, data);
    }
    
    private boolean isMeaninglessParameterName(String paramName) {
        String lowerName = paramName.toLowerCase();
        
        // Allow well-known short names
        if (ALLOWED_SHORT_NAMES.contains(lowerName)) {
            return false;
        }
        
        // Check for single letters
        if (SINGLE_LETTER_PATTERN.matcher(paramName).matches()) {
            return true;
        }
        
        // Check for meaningless names
        if (MEANINGLESS_NAMES.contains(lowerName)) {
            return true;
        }
        
        // Check for names with numbers only (like param1, arg2)
        if (paramName.matches("^(param|arg|temp|tmp|var|val|data|obj)\\d+$")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isGetterOrSetter(ASTMethodDeclaration method) {
        String methodName = method.getMethodName();
        return methodName != null && 
               (methodName.startsWith("get") || methodName.startsWith("set") || 
                methodName.startsWith("is"));
    }
    
    private boolean hasOverrideAnnotation(ASTMethodDeclaration method) {
        return method.isAnnotationPresent("Override");
    }
}