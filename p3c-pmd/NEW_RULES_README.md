# 新增检测规则说明

本次在 p3c-pmd 项目中新增了两个代码检测规则，分别针对命名规范和注释规范。

## 1. 避免拼音命名规则 (AvoidChinesePinyinNamingRule)

### 规则说明
- **级别**: 重要 (Priority 2)
- **类别**: 命名规范
- **描述**: 避免使用拼音命名，应使用有意义的英文词汇

### 检测范围
- 变量名（字段和局部变量）
- 方法名
- 参数名

### 白名单
以下拼音词汇被视为国际通用名称，允许使用：
- alibaba, taobao, youku, hangzhou
- beijing, shanghai, shenzhen, guangzhou
- chengdu, wuhan, xian, nanjing
- alipay, dingding, dingtalk, yunos, amap, xiami

### 示例
```java
// 错误示例
String shuju = "data";        // 使用了拼音 "shuju"
int zongshu = 100;           // 使用了拼音 "zongshu"
public void chuli() {}       // 使用了拼音 "chuli"

// 正确示例
String data = "data";
int totalCount = 100;
public void process() {}

// 白名单示例（允许）
String taobaoUrl = "https://www.taobao.com";
AlibabaConfig alibabaConfig = new AlibabaConfig();
```

## 2. 有意义注释规则 (MeaningfulCommentRule)

### 规则说明
- **级别**: 重要 (Priority 2)
- **类别**: 注释规范
- **描述**: 公共方法必须有有意义的注释，说明方法的功能、用途和注意事项

### 检测范围
- 所有公共方法（public methods）
- 自动忽略 getter/setter 方法

### 无意义注释模式
以下类型的注释被认为是无意义的：
- 过于简短的注释（少于10个字符）
- 仅包含单词：方法、函数、function、method、接口、interface
- 仅包含：TODO、FIXME、XXX、待实现、未实现
- 仅包含：注释、comment、描述、description
- 仅包含：无、空、null、none、暂无、略

### 示例
```java
// 错误示例 - 无意义注释
/**
 * 方法
 */
public void processData() {
    // ...
}

/**
 * TODO
 */
public void handleRequest() {
    // ...
}

// 错误示例 - 缺少注释
public void calculateDiscount(Order order) {
    // ...
}

// 正确示例 - 有意义的注释
/**
 * 处理用户提交的订单数据，包括验证、计算折扣和生成订单号
 * 
 * @param orderData 用户提交的订单信息
 * @return 处理后的订单对象
 * @throws OrderException 当订单数据无效时抛出
 */
public Order processOrder(OrderData orderData) throws OrderException {
    // ...
}

// 自动忽略 getter/setter
public String getName() {  // 不需要注释
    return name;
}

public void setName(String name) {  // 不需要注释
    this.name = name;
}
```

## 使用方法

这些规则已经集成到 p3c-pmd 中，可以通过以下方式使用：

1. **Maven 插件**：规则会自动包含在 p3c-maven-plugin 的检查中
2. **IDE 插件**：在 IntelliJ IDEA 或 Eclipse 的 P3C 插件中自动生效
3. **命令行**：使用 PMD 命令行工具运行检查

## 配置文件位置

- 规则实现：
  - `/src/main/java/com/alibaba/p3c/pmd/lang/java/rule/naming/AvoidChinesePinyinNamingRule.java`
  - `/src/main/java/com/alibaba/p3c/pmd/lang/java/rule/comment/MeaningfulCommentRule.java`
- 规则配置：
  - `/src/main/resources/rulesets/java/ali-naming.xml`
  - `/src/main/resources/rulesets/java/ali-comment.xml`
- 消息配置：
  - `/src/main/resources/messages.xml`（中文）
  - `/src/main/resources/messages_en.xml`（英文）