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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;

/**
 * [Recommended] Avoid leaving TODO, FIXME, or XXX comments in production code. 
 * These should be resolved before code submission.
 *
 * @author ai-assistant
 * @date 2024/01/01
 */
public class AvoidTodoCommentRule extends AbstractAliCommentRule {

    private static final Pattern TODO_PATTERN = Pattern.compile(
        ".*(TODO|FIXME|XXX|HACK|BUG)\\s*:?.*", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final String MESSAGE_KEY_PREFIX = "java.comment.AvoidTodoCommentRule.violation.msg";

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        assignCommentsToDeclarations(cUnit);
        
        // Check all comments for TODO markers
        List<Comment> comments = cUnit.getComments();
        for (Comment comment : comments) {
            checkTodoComment(comment, data);
        }
        
        return super.visit(cUnit, data);
    }

    @Override
    protected SortedMap<Integer, Node> orderedCommentsAndDeclarations(ASTCompilationUnit cUnit) {
        SortedMap<Integer, Node> itemsByLineNumber = new TreeMap<>();
        
        // Add all comments to the map
        List<Comment> comments = cUnit.getComments();
        for (Comment comment : comments) {
            itemsByLineNumber.put((comment.getBeginLine() << 16) + comment.getBeginColumn(), comment);
        }
        
        return itemsByLineNumber;
    }

    /**
     * Check if comment contains TODO, FIXME, XXX or similar markers
     *
     * @param comment comment node
     * @param data ruleContext
     */
    private void checkTodoComment(Comment comment, Object data) {
        String commentContent = comment.getImage();
        
        if (TODO_PATTERN.matcher(commentContent).matches()) {
            // Extract the specific marker (TODO, FIXME, etc.)
            String marker = extractMarker(commentContent);
            ViolationUtils.addViolationWithPrecisePosition(this, comment, data,
                I18nResources.getMessage(MESSAGE_KEY_PREFIX, marker));
        }
    }

    /**
     * Extract the specific marker from the comment content
     *
     * @param commentContent the comment text
     * @return the marker found (TODO, FIXME, etc.)
     */
    private String extractMarker(String commentContent) {
        String upperContent = commentContent.toUpperCase();
        
        if (upperContent.contains("TODO")) {
            return "TODO";
        } else if (upperContent.contains("FIXME")) {
            return "FIXME";
        } else if (upperContent.contains("XXX")) {
            return "XXX";
        } else if (upperContent.contains("HACK")) {
            return "HACK";
        } else if (upperContent.contains("BUG")) {
            return "BUG";
        }
        
        return "TODO"; // default fallback
    }
}