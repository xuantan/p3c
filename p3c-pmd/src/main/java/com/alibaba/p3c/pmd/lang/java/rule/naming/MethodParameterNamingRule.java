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

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;

/**
 * [Recommended] Method parameters should use meaningful names, avoid single character names except for common loop variables (i, j, k).
 *
 * @author ai-assistant
 * @date 2024/01/01
 */
public class MethodParameterNamingRule extends AbstractAliRule {

    private static final Pattern SINGLE_CHAR_PATTERN = Pattern.compile("^[a-zA-Z]$");
    private static final Set<String> ALLOWED_SINGLE_CHARS = new HashSet<>();
    
    static {
        // Common loop variables that are acceptable as single characters
        ALLOWED_SINGLE_CHARS.add("i");
        ALLOWED_SINGLE_CHARS.add("j");
        ALLOWED_SINGLE_CHARS.add("k");
        ALLOWED_SINGLE_CHARS.add("x");
        ALLOWED_SINGLE_CHARS.add("y");
        ALLOWED_SINGLE_CHARS.add("z");
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        List<ASTFormalParameter> parameters = node.findDescendantsOfType(ASTFormalParameter.class);
        
        for (ASTFormalParameter parameter : parameters) {
            String paramName = parameter.getVariableDeclaratorId().getImage();
            
            // Check if parameter name is a single character
            if (SINGLE_CHAR_PATTERN.matcher(paramName).matches()) {
                // Allow common loop variables
                if (!ALLOWED_SINGLE_CHARS.contains(paramName.toLowerCase())) {
                    ViolationUtils.addViolationWithPrecisePosition(this, parameter, data,
                        I18nResources.getMessage("java.naming.MethodParameterNamingRule.violation.msg.single",
                            paramName));
                }
            }
            
            // Check if parameter name is too short (less than 2 characters and not in allowed list)
            if (paramName.length() < 2 && !ALLOWED_SINGLE_CHARS.contains(paramName.toLowerCase())) {
                ViolationUtils.addViolationWithPrecisePosition(this, parameter, data,
                    I18nResources.getMessage("java.naming.MethodParameterNamingRule.violation.msg.short",
                        paramName));
            }
            
            // Check if parameter name contains numbers at the beginning (like 1st, 2nd)
            if (paramName.matches("^\\d+.*")) {
                ViolationUtils.addViolationWithPrecisePosition(this, parameter, data,
                    I18nResources.getMessage("java.naming.MethodParameterNamingRule.violation.msg.number",
                        paramName));
            }
        }
        
        return super.visit(node, data);
    }
}