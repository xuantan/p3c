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

import java.util.List;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;

/**
 * [Recommended] Complex methods with many parameters or long implementation must have detailed Javadoc comments.
 *
 * @author p3c-assistant
 * @date 2024/01/01
 */
public class MethodComplexityMustHaveCommentRule extends AbstractAliCommentRule {

    private static final int MAX_PARAMETERS_WITHOUT_COMMENT = 3;
    private static final int MAX_LINES_WITHOUT_COMMENT = 20;
    
    private static final Pattern PARAM_PATTERN = Pattern.compile(".*@param\\s+\\w+.*", 
        Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern RETURN_PATTERN = Pattern.compile(".*@return.*", 
        Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // Skip constructors, getters, setters, and overridden methods
        if (node.isConstructor() || isGetterOrSetter(node) || hasOverrideAnnotation(node)) {
            return super.visit(node, data);
        }

        // Check if method is complex
        boolean isComplex = isComplexMethod(node);
        
        if (isComplex) {
            checkComplexMethodComment(node, data);
        }
        
        return super.visit(node, data);
    }
    
    private boolean isComplexMethod(ASTMethodDeclaration node) {
        List<ASTFormalParameter> parameters = node.findDescendantsOfType(ASTFormalParameter.class);
        int paramCount = parameters.size();
        
        int methodLines = node.getEndLine() - node.getBeginLine() + 1;
        
        return paramCount > MAX_PARAMETERS_WITHOUT_COMMENT || methodLines > MAX_LINES_WITHOUT_COMMENT;
    }
    
    private void checkComplexMethodComment(ASTMethodDeclaration node, Object data) {
        Comment comment = node.comment();
        String methodName = node.getMethodName();
        
        if (comment == null) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                I18nResources.getMessage("java.comment.MethodComplexityMustHaveCommentRule.violation.msg.missing",
                    methodName));
            return;
        }
        
        String commentContent = comment.getImage();
        
        // Check if it's a proper Javadoc comment
        if (!commentContent.startsWith("/**")) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                I18nResources.getMessage("java.comment.MethodComplexityMustHaveCommentRule.violation.msg.format",
                    methodName));
            return;
        }
        
        // Check for detailed description (more than just method name repetition)
        if (isCommentTooSimple(commentContent, methodName)) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                I18nResources.getMessage("java.comment.MethodComplexityMustHaveCommentRule.violation.msg.detail",
                    methodName));
        }
        
        // Check for parameter documentation
        List<ASTFormalParameter> parameters = node.findDescendantsOfType(ASTFormalParameter.class);
        if (parameters.size() > MAX_PARAMETERS_WITHOUT_COMMENT) {
            boolean hasParamDoc = PARAM_PATTERN.matcher(commentContent).matches();
            if (!hasParamDoc) {
                ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    I18nResources.getMessage("java.comment.MethodComplexityMustHaveCommentRule.violation.msg.param",
                        methodName));
            }
        }
        
        // Check for return documentation (if method has return value)
        if (!node.getResultType().isVoid()) {
            boolean hasReturnDoc = RETURN_PATTERN.matcher(commentContent).matches();
            if (!hasReturnDoc) {
                ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    I18nResources.getMessage("java.comment.MethodComplexityMustHaveCommentRule.violation.msg.return",
                        methodName));
            }
        }
    }
    
    private boolean isCommentTooSimple(String commentContent, String methodName) {
        // Remove comment markers and whitespace
        String cleanComment = commentContent.replaceAll("/\\*\\*|\\*/|\\*|\\s+", " ").trim().toLowerCase();
        String cleanMethodName = methodName.toLowerCase();
        
        // Check if comment is just the method name or very short
        if (cleanComment.length() < 10) {
            return true;
        }
        
        // Check if comment only contains the method name
        if (cleanComment.equals(cleanMethodName) || 
            cleanComment.startsWith(cleanMethodName + " ") ||
            cleanComment.contains(cleanMethodName) && cleanComment.length() < cleanMethodName.length() + 20) {
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