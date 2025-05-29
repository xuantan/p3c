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

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import java.util.List;
import java.util.regex.Pattern;

/**
 * [Mandatory] All method parameters must be documented with Javadoc @param tags.
 * Each parameter should have a clear description of its purpose and usage.
 *
 * @author YourName
 * @date 2024/03/21
 */
public class MethodParameterMustHaveJavadocRule extends AbstractAliRule {

    private static final Pattern PARAM_PATTERN = Pattern.compile("@param\\s+(\\w+)\\s+.*");
    private static final String MESSAGE_KEY_PREFIX = "java.comment.MethodParameterMustHaveJavadocRule.violation.msg";

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // Skip methods without parameters
        if (node.getFormalParameters().getParameterCount() == 0) {
            return super.visit(node, data);
        }

        Comment comment = node.comment();
        if (!(comment instanceof FormalComment)) {
            // Report violation if method has parameters but no Javadoc
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".missing",
                    node.getMethodName()));
            return super.visit(node, data);
        }

        String commentContent = comment.getImage();
        List<ASTFormalParameter> parameters = node.getFormalParameters()
            .findChildrenOfType(ASTFormalParameter.class);

        // Check each parameter has a corresponding @param tag
        for (ASTFormalParameter param : parameters) {
            String paramName = param.getVariableDeclaratorId().getName();
            if (!hasParamTag(commentContent, paramName)) {
                ViolationUtils.addViolationWithPrecisePosition(this, param, data,
                    I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".param",
                        paramName, node.getMethodName()));
            }
        }

        return super.visit(node, data);
    }

    private boolean hasParamTag(String comment, String paramName) {
        return PARAM_PATTERN.matcher(comment)
            .results()
            .map(result -> result.group(1))
            .anyMatch(param -> param.equals(paramName));
    }
}