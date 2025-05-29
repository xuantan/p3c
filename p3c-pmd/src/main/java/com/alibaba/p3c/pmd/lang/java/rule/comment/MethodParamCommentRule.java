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

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.ast.Comment;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Method parameter comments must include parameter type information.
 * For example:
 * <pre>
 * /**
 *  * @param name (String) The user's name
 *  * @param age (int) The user's age
 *  * /
 * public void setUserInfo(String name, int age) {
 *     // ...
 * }
 * </pre>
 *
 * @author yourname
 * @date 2024/03/xx
 */
public class MethodParamCommentRule extends AbstractAliRule {
    private static final Pattern PARAM_TYPE_PATTERN = Pattern.compile("@param\\s+\\w+\\s*\\([^)]+\\).*");

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // Skip methods without parameters
        if (node.getParameterCount() == 0) {
            return super.visit(node, data);
        }

        Comment comment = node.getComment();
        if (comment == null) {
            return super.visit(node, data);
        }

        String commentContent = comment.getImage();
        List<ASTFormalParameter> parameters = node.findDescendantsOfType(ASTFormalParameter.class);

        for (ASTFormalParameter param : parameters) {
            String paramName = param.getParameterName();
            // Check if the parameter has a properly formatted comment
            if (!hasProperParamComment(commentContent, paramName)) {
                ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    "java.comment.MethodParamCommentRule.violation.msg",
                    new Object[]{paramName});
            }
        }

        return super.visit(node, data);
    }

    private boolean hasProperParamComment(String commentContent, String paramName) {
        if (commentContent == null || paramName == null) {
            return false;
        }

        // Split comment into lines and look for @param tag
        String[] lines = commentContent.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            // Check if the line contains @param tag for this parameter and includes type information
            if (line.contains("@param") && line.contains(paramName) && PARAM_TYPE_PATTERN.matcher(line).matches()) {
                return true;
            }
        }
        return false;
    }
}