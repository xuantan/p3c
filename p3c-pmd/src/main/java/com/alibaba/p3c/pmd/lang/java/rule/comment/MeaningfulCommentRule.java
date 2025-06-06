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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.util.NodeSortUtils;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.AccessNode;

/**
 * [Mandatory] Public methods must have meaningful comments, not meaningless descriptions.
 *
 * @author caikang
 * @date 2024/01/15
 */
public class MeaningfulCommentRule extends AbstractAliCommentRule {

    // 无意义的注释模式
    private static final Set<String> MEANINGLESS_PATTERNS = new HashSet<>(Arrays.asList(
        "方法", "函数", "function", "method", "接口", "interface",
        "构造方法", "构造函数", "constructor",
        "getter", "setter", "get方法", "set方法",
        "TODO", "FIXME", "XXX", "待实现", "未实现",
        "注释", "comment", "描述", "description",
        "无", "空", "null", "none", "暂无", "略"
    ));

    // 过于简短的注释长度阈值
    private static final int MIN_COMMENT_LENGTH = 10;

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // 只检查公共方法
        if (!node.isPublic()) {
            return super.visit(node, data);
        }

        // 忽略 getter/setter 方法
        String methodName = node.getMethodName();
        if (isGetterOrSetter(node, methodName)) {
            return super.visit(node, data);
        }

        // 获取方法前的注释
        Comment comment = getCommentBefore(node);
        
        if (comment == null) {
            // 公共方法必须有注释
            addViolationWithMessage(data, node,
                I18nResources.getMessage("java.comment.MeaningfulCommentRule.violation.msg.missing",
                    methodName));
        } else if (comment instanceof FormalComment) {
            // 检查注释是否有意义
            String commentText = comment.getImage();
            if (isMeaninglessComment(commentText)) {
                addViolationWithMessage(data, node,
                    I18nResources.getMessage("java.comment.MeaningfulCommentRule.violation.msg.meaningless",
                        methodName));
            }
        }

        return super.visit(node, data);
    }

    private boolean isGetterOrSetter(ASTMethodDeclaration node, String methodName) {
        // 检查是否是 getter 方法
        if ((methodName.startsWith("get") || methodName.startsWith("is")) && 
            node.getArity() == 0 && 
            node.getResultType() != null && 
            !node.getResultType().isVoid()) {
            return true;
        }
        
        // 检查是否是 setter 方法
        if (methodName.startsWith("set") && 
            node.getArity() == 1 && 
            node.getResultType() != null && 
            node.getResultType().isVoid()) {
            return true;
        }
        
        return false;
    }

    private Comment getCommentBefore(ASTMethodDeclaration node) {
        // 获取方法声明的行号
        int methodLine = node.getBeginLine();
        
        // 查找方法前最近的注释
        if (node.getParentsOfType(net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit.class).isEmpty()) {
            return null;
        }
        
        net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit compilationUnit = 
            node.getParentsOfType(net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit.class).get(0);
        
        Comment closestComment = null;
        int closestLine = -1;
        
        for (Comment comment : compilationUnit.getComments()) {
            int commentLine = comment.getBeginLine();
            // 注释必须在方法之前，且尽可能接近方法
            if (commentLine < methodLine && commentLine > closestLine) {
                // 检查注释和方法之间是否有其他代码
                if (methodLine - comment.getEndLine() <= 2) { // 允许最多2行空行
                    closestComment = comment;
                    closestLine = commentLine;
                }
            }
        }
        
        return closestComment;
    }

    private boolean isMeaninglessComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return true;
        }

        // 移除 Javadoc 标记
        String cleanComment = comment
            .replaceAll("/\\*\\*", "")
            .replaceAll("\\*/", "")
            .replaceAll("\\* ", "")
            .replaceAll("\\*", "")
            .replaceAll("@[a-zA-Z]+.*", "") // 移除 @param, @return 等标记
            .trim();

        // 检查是否过短
        if (cleanComment.length() < MIN_COMMENT_LENGTH) {
            return true;
        }

        // 转换为小写进行比较
        String lowerComment = cleanComment.toLowerCase();

        // 检查是否包含无意义的词汇
        for (String pattern : MEANINGLESS_PATTERNS) {
            // 如果注释主要内容就是这些无意义的词，则认为是无意义注释
            if (lowerComment.equals(pattern) || 
                lowerComment.equals(pattern + "。") ||
                lowerComment.equals(pattern + ".")) {
                return true;
            }
        }

        // 检查是否只是重复了方法名
        String methodNamePattern = "\\b[a-zA-Z]+\\b";
        if (cleanComment.matches(methodNamePattern) && cleanComment.length() < 20) {
            return true;
        }

        return false;
    }
}