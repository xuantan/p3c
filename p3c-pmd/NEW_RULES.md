# 新增的P3C-PMD检测规则

本文档描述了在 p3c-pmd 项目中新增的两个检测规则。

## 1. MethodParameterNamingRule（方法参数命名规则）

### 规则描述
检测方法参数命名是否符合规范，避免使用单字符参数名或无意义的命名。

### 位置
- **规则文件**: `src/main/java/com/alibaba/p3c/pmd/lang/java/rule/naming/MethodParameterNamingRule.java`
- **规则集配置**: `src/main/resources/rulesets/java/ali-naming.xml`
- **测试文件**: `src/test/resources/com/alibaba/p3c/pmd/lang/java/rule/naming/xml/MethodParameterNamingRule.xml`

### 检测内容
1. **单字符参数名**: 检测是否使用单字符作为参数名（如 `a`, `b`, `c`）
2. **允许的例外**: 常见的循环变量（`i`, `j`, `k`, `x`, `y`, `z`）被允许
3. **数字开头**: 检测参数名是否以数字开头（如 `1stParam`）
4. **过短命名**: 检测参数名长度是否过短且无意义

### 示例

#### ❌ 不符合规范的代码
```java
public void processData(String a, int b) {
    // 实现逻辑
}

public void calculate(double 1stValue, double 2ndValue) {
    // 实现逻辑
}
```

#### ✅ 符合规范的代码
```java
public void processUserData(String userName, int userAge) {
    // 实现逻辑
}

// 允许的循环变量
public void processMatrix(int[][] matrix, int i, int j) {
    for (int x = 0; x < matrix.length; x++) {
        // 循环逻辑
    }
}
```

## 2. AvoidTodoCommentRule（避免TODO注释规则）

### 规则描述
检测代码中未清理的TODO、FIXME、XXX等标记注释，这些应在代码提交前处理完成。

### 位置
- **规则文件**: `src/main/java/com/alibaba/p3c/pmd/lang/java/rule/comment/AvoidTodoCommentRule.java`
- **规则集配置**: `src/main/resources/rulesets/java/ali-comment.xml`
- **测试文件**: `src/test/resources/com/alibaba/p3c/pmd/lang/java/rule/comment/xml/AvoidTodoCommentRule.xml`

### 检测内容
检测以下类型的标记注释（不区分大小写）：
1. **TODO**: 待完成的任务
2. **FIXME**: 需要修复的问题
3. **XXX**: 需要注意的问题
4. **HACK**: 临时的解决方案
5. **BUG**: 已知的错误

### 示例

#### ❌ 不符合规范的代码
```java
public void processData() {
    // TODO: implement this method later
    // FIXME: handle edge cases
    // XXX: this is a hack
    validateInput();
}
```

#### ✅ 符合规范的代码
```java
public void processData() {
    // Process user data with comprehensive validation
    validateUserInput();
    // Handle successful processing with error recovery
    processValidatedData();
}
```

## 规则优先级

- **MethodParameterNamingRule**: 优先级 3（建议级别）
- **AvoidTodoCommentRule**: 优先级 3（建议级别）

## 国际化支持

两个规则都支持中英文消息：
- 中文消息：`src/main/resources/messages.xml`
- 英文消息：`src/main/resources/messages_en.xml`

## 测试

每个规则都包含完整的测试用例，覆盖正面和负面场景，确保规则的正确性和可靠性。

运行测试：
```bash
mvn test
```

## 使用方式

这些规则会自动包含在对应的规则集中：
- `MethodParameterNamingRule` 包含在 `ali-naming` 规则集
- `AvoidTodoCommentRule` 包含在 `ali-comment` 规则集

在IDE插件中使用时，这些规则会自动生效并提供相应的检测提示。