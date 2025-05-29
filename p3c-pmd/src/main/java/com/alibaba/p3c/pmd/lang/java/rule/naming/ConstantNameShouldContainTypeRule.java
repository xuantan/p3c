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

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;

import java.util.HashMap;
import java.util.Map;

/**
 * Constant names should include their data type as a suffix.
 * For example:
 * <pre>
 * public static final int MAX_COUNT_INT = 100;
 * public static final double MIN_VALUE_DOUBLE = 0.001;
 * public static final String DEFAULT_NAME_STRING = "unknown";
 * </pre>
 *
 * @author yourname
 * @date 2024/03/xx
 */
public class ConstantNameShouldContainTypeRule extends AbstractAliRule {
    private static final Map<String, String> TYPE_SUFFIX_MAP = new HashMap<>();

    static {
        TYPE_SUFFIX_MAP.put("int", "_INT");
        TYPE_SUFFIX_MAP.put("long", "_LONG");
        TYPE_SUFFIX_MAP.put("float", "_FLOAT");
        TYPE_SUFFIX_MAP.put("double", "_DOUBLE");
        TYPE_SUFFIX_MAP.put("boolean", "_BOOL");
        TYPE_SUFFIX_MAP.put("String", "_STRING");
        TYPE_SUFFIX_MAP.put("byte", "_BYTE");
        TYPE_SUFFIX_MAP.put("char", "_CHAR");
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        // Check if it's a constant (static final)
        if (!node.isStatic() || !node.isFinal()) {
            return super.visit(node, data);
        }

        // Get the field type
        String fieldType = node.getType().getSimpleName();
        String expectedSuffix = TYPE_SUFFIX_MAP.get(fieldType);

        // If we don't have a defined suffix for this type, skip it
        if (expectedSuffix == null) {
            return super.visit(node, data);
        }

        // Check each variable declarator in the field declaration
        for (ASTVariableDeclaratorId var : node.findDescendantsOfType(ASTVariableDeclaratorId.class)) {
            String constantName = var.getImage();
            if (!constantName.endsWith(expectedSuffix)) {
                ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    "java.naming.ConstantNameShouldContainTypeRule.violation.msg",
                    new Object[]{constantName, expectedSuffix});
            }
        }

        return super.visit(node, data);
    }
}