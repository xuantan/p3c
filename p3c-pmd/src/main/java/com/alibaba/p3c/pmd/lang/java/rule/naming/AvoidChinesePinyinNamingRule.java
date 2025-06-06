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
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;

/**
 * [Mandatory] Avoid using Chinese pinyin for naming. Use meaningful English words instead.
 * Exceptions: alibaba, taobao, youku, hangzhou and other proper nouns.
 *
 * @author caikang
 * @date 2024/01/15
 */
public class AvoidChinesePinyinNamingRule extends AbstractAliRule {

    // 常见的拼音模式（可以根据需要扩展）
    private static final Pattern PINYIN_PATTERN = Pattern.compile(
        ".*(shu|zhi|chi|shi|ri|zi|ci|si|yi|wu|yu|nu|lu|ju|qu|xu|" +
        "bing|ping|ming|ding|ting|ning|ling|jing|qing|xing|ying|" +
        "zhong|chong|shong|rong|zong|cong|song|yong|dong|tong|nong|long|gong|kong|hong|" +
        "zhang|chang|shang|rang|zang|cang|sang|yang|dang|tang|nang|lang|gang|kang|hang|fang|bang|pang|mang|wang|" +
        "zheng|cheng|sheng|reng|zeng|ceng|seng|yeng|deng|teng|neng|leng|geng|keng|heng|feng|beng|peng|meng|weng|" +
        "zhuang|chuang|shuang|guang|kuang|huang|" +
        "zhuan|chuan|shuan|ruan|zuan|cuan|suan|yuan|duan|tuan|nuan|luan|guan|kuan|huan|" +
        "zhui|chui|shui|rui|zui|cui|sui|dui|tui|gui|kui|hui|" +
        "zhai|chai|shai|dai|tai|nai|lai|gai|kai|hai|pai|mai|wai|" +
        "zhao|chao|shao|rao|zao|cao|sao|yao|dao|tao|nao|lao|gao|kao|hao|bao|pao|mao|" +
        "zhou|chou|shou|rou|zou|cou|sou|you|dou|tou|nou|lou|gou|kou|hou|fou|pou|mou|" +
        "zhan|chan|shan|ran|zan|can|san|yan|dan|tan|nan|lan|gan|kan|han|fan|ban|pan|man|wan|" +
        "zhen|chen|shen|ren|zen|cen|sen|yen|den|nen|len|gen|ken|hen|fen|ben|pen|men|wen|" +
        "zhi|shi|chi|ri|ci|si|di|ti|ni|li|ji|qi|xi|bi|pi|mi|fi|wei|dei|tei|nei|lei|gei|kei|hei|fei|bei|pei|mei).*",
        Pattern.CASE_INSENSITIVE
    );

    // 白名单：允许的拼音词汇
    private static final Set<String> PINYIN_WHITELIST = new HashSet<>(Arrays.asList(
        "alibaba", "taobao", "youku", "hangzhou", "beijing", "shanghai", 
        "shenzhen", "guangzhou", "chengdu", "wuhan", "xian", "nanjing",
        "alipay", "dingding", "dingtalk", "yunos", "amap", "xiami"
    ));

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        checkVariableName(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        checkVariableName(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String methodName = node.getMethodName();
        if (methodName != null && isPinyinName(methodName)) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                I18nResources.getMessage("java.naming.AvoidChinesePinyinNamingRule.violation.msg.method",
                    methodName));
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        ASTVariableDeclaratorId variableId = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        if (variableId != null) {
            String paramName = variableId.getImage();
            if (paramName != null && isPinyinName(paramName)) {
                ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    I18nResources.getMessage("java.naming.AvoidChinesePinyinNamingRule.violation.msg.parameter",
                        paramName));
            }
        }
        return super.visit(node, data);
    }

    private void checkVariableName(net.sourceforge.pmd.lang.ast.Node node, Object data) {
        ASTVariableDeclaratorId variableId = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        if (variableId != null) {
            String varName = variableId.getImage();
            if (varName != null && isPinyinName(varName)) {
                ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    I18nResources.getMessage("java.naming.AvoidChinesePinyinNamingRule.violation.msg.variable",
                        varName));
            }
        }
    }

    private boolean isPinyinName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        // 检查白名单
        String lowerName = name.toLowerCase();
        for (String whiteWord : PINYIN_WHITELIST) {
            if (lowerName.contains(whiteWord)) {
                return false;
            }
        }
        
        // 检查是否匹配拼音模式
        return PINYIN_PATTERN.matcher(name).matches();
    }
}