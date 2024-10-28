### SonarQube Java自定义规则开发说明文档

------



#### 概述

------

本项目的代码质量规则基于阿里巴巴的 P3C 规则集进行二次开发。P3C 规则原本依赖于 PMD 框架，但由于 SonarLint 对 PMD 框架不兼容，我们决定使用 SonarJava API 重新实现这些规则，以确保与 SonarQube 和 SonarLint 的无缝集成。

通过采用 SonarJava API，我们不仅能够利用其强大的静态代码分析能力，还能够确保规则在不同的开发环境和工具中保持一致性和可靠性。这样，开发者可以在 IDE 中通过 SonarLint 实时获取反馈，并且在持续集成流程中通过 SonarQube 进行更全面的代码质量检查。



| 依赖项名称    | 版本         | 备注 |
| ------------- | ------------ | ---- |
| JDK           | 1.8或以上    |      |
| SonarQube     | 8.9.5        |      |
| SonarJava API | 6.14.0.25463 |      |
| Jacoco        | 0.8.6        |      |





#### 项目架构介绍

------

![image-20241028102929998](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016095357045.png?token=GHSAT0AAAAAACZTIZV6MFYORLXEMMQ6A2XSZY67TYQ)

项目结构如上图，核心代码的包路径提示如下：

| 包路径                                             | 描述                                                         |
| -------------------------------------------------- | ------------------------------------------------------------ |
| src/main/java/com.elvis.sonar.java.checks.*        | 规则实现源代码                                               |
| src/main/resources/org.sonar.l10n.javarules.java.* | 规则配置文件存放目录。.json的是SonarQube规则配置文件，.html是规则的描述文件 |
| src/test/files/*                                   | 规则单元测试的用例                                           |
| src/test/java/com.elvis.sonar.java.checks.*        | 规则单元测试代码                                             |



规则源代码，及单元测试源码的包路径下，以规则类型进行了包划分，新开发的规则，请自行判断规则类型，存放到不同的包路径下：

| 规则类型    | 描述                         |
| ----------- | ---------------------------- |
| comment     | 注释                         |
| concurrent  | 并发相关                     |
| constant    | 常量                         |
| exception   | 异常处理                     |
| flowcontrol | 流程控制（如循环、条件语句） |
| naming      | 命名规范                     |
| oop         | 面向对象编程                 |
| orm         | ORM框架使用相关              |
| other       | 其他                         |
| set         | 集合                         |



项目树状结构图：

```
sonarjava-p3c
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─elvis
│  │  │          └─sonar
│  │  │              └─java
│  │  │                  ├─checks  # 包含各种代码检查规则的实现
│  │  │                  │  ├─comment  # 注释相关的代码检查规则
│  │  │                  │  ├─concurrent  # 并发相关的代码检查规则
│  │  │                  │  ├─constant  # 常量相关的代码检查规则
│  │  │                  │  ├─exception  # 异常处理相关的代码检查规则
│  │  │                  │  ├─flowcontrol  # 流程控制（如循环、条件语句）相关的代码检查规则
│  │  │                  │  ├─naming  # 命名规范相关的代码检查规则
│  │  │                  │  ├─oop  # 面向对象编程相关的代码检查规则
│  │  │                  │  ├─orm  # ORM框架使用相关的代码检查规则
│  │  │                  │  ├─other  # 其他未分类的代码检查规则
│  │  │                  │  ├─set  # 集合相关的代码检查规则
│  │  │                  │  ├─utils  # 检查规则中使用的工具类
│  │  │                  │  └─enums  # 检查规则中使用的枚举类
│  │  │                  ├─log
│  │  │                  │  ├─formatter  # 日志格式化相关的工具类
│  │  │                  │  └─utils  # 日志相关的工具类
│  │  │                  ├─pojo  # POJO（Plain Old Java Object）相关的工具类或模型
│  │  │                  └─utils  # 通用工具类
│  │  └─resources
│  │      └─org
│  │          └─sonar
│  │              └─l10n
│  │                  └─java
│  │                      └─rules
│  │                          └─java  # 规则的本地化资源文件
│  │                              ├─comment  # 注释相关的规则描述
│  │                              ├─concurrent  # 并发相关的规则描述
│  │                              ├─constant  # 常量相关的规则描述
│  │                              ├─exception  # 异常处理相关的规则描述
│  │                              ├─flowcontrol  # 流程控制相关的规则描述
│  │                              ├─naming  # 命名规范相关的规则描述
│  │                              ├─oop  # 面向对象编程相关的规则描述
│  │                              ├─orm  # ORM框架使用相关的规则描述
│  │                              ├─other  # 其他未分类的规则描述
│  │                              └─set  # 集合相关的规则描述
│  └─test
│      ├─files  # 测试用例文件
│      │  ├─comment  # 注释相关的测试用例
│      │  ├─concurrent  # 并发相关的测试用例
│      │  ├─constant  # 常量相关的测试用例
│      │  ├─exception  # 异常处理相关的测试用例
│      │  ├─flowcontrol  # 流程控制相关的测试用例
│      │  ├─naming  # 命名规范相关的测试用例
│      │  ├─oop  # 面向对象编程相关的测试用例
│      │  ├─orm  # ORM框架使用相关的测试用例
│      │  ├─other  # 其他未分类的测试用例
│      │  └─set  # 集合相关的测试用例
│      └─java
│          └─com
│              └─elvis
│                  └─sonar
│                      └─java
│                          ├─checks  # 各种代码检查规则的测试类
│                          │  ├─comment  # 注释相关的测试类
│                          │  ├─concurrent  # 并发相关的测试类
│                          │  ├─constant  # 常量相关的测试类
│                          │  ├─exception  # 异常处理相关的测试类
│                          │  ├─flowcontrol  # 流程控制相关的测试类
│                          │  ├─naming  # 命名规范相关的测试类
│                          │  ├─oop  # 面向对象编程相关的测试类
│                          │  ├─orm  # ORM框架使用相关的测试类
│                          │  ├─other  # 其他未分类的测试类
│                          │  └─set  # 集合相关的测试类
│                          └─utils  # 工具类的测试类
```

checks 目录包含了所有的代码检查规则，而 resources 目录则包含了这些规则的本地化描述。test 目录用于存放测试用例和测试类，确保代码质量规则的正确性和可靠性。





#### 抽象语法树

------

SonarJava API 与抽象语法树（Abstract Syntax Tree, AST）之间有着紧密的关系。

**SonarJava API 提供了一套工具和接口，用于访问和遍历 Java 源代码的 AST。**

**AST 是源代码的一种结构化表示**，其中**每个节点代表代码中的一个构造，如类、方法或语句**。通过 SonarJava API，开发者可以编写自定义规则来检查这些节点，识别潜在的代码质量问题。API 中的访问器（Visitor）模式允许用户定义特定的行为，在遍历 AST 时执行相应的检查逻辑。这种机制使得 SonarJava 能够深入分析代码结构，提供详细的静态代码分析结果，从而帮助提升代码质量和可维护性。 



SonarJava 使用的AST节点涵盖了 Java 语言的各种结构。这些节点以接口形式，定义在 `org.sonar.plugins.java.api.tree` 和其子类中：

| 接口名称                     | 枚举值                               | 描述                                          |
| ---------------------------- | ------------------------------------ | --------------------------------------------- |
| `AnnotationTree`             | `Tree.Kind.ANNOTATION`               | 表示注解。                                    |
| `ArrayAccessExpressionTree`  | `Tree.Kind.ARRAY_ACCESS_EXPRESSION`  | 表示数组访问表达式。                          |
| `ArrayDimensionTree`         | `Tree.Kind.ARRAY_DIMENSION`          | 表示数组维度。                                |
| `ArrayTypeTree`              | `Tree.Kind.ARRAY_TYPE`               | 表示数组类型。                                |
| `AssertStatementTree`        | `Tree.Kind.ASSERT_STATEMENT`         | 表示断言语句。                                |
| `AssignmentExpressionTree`   | `Tree.Kind.ASSIGNMENT`               | 表示赋值表达式。                              |
| `BinaryExpressionTree`       | `Tree.Kind.BINARY_EXPRESSION`        | 表示二元表达式（如 `+`, `-`, `*`, `/` 等）。  |
| `BlockTree`                  | `Tree.Kind.BLOCK`                    | 表示代码块（一组语句）。                      |
| `BreakStatementTree`         | `Tree.Kind.BREAK_STATEMENT`          | 表示 `break` 语句。                           |
| `CaseGroupTree`              | `Tree.Kind.CASE_GROUP`               | 表示 `switch` 语句中的 `case` 组。            |
| `CaseLabelTree`              | `Tree.Kind.CASE_LABEL`               | 表示 `switch` 语句中的 `case` 标签。          |
| `CatchTree`                  | `Tree.Kind.CATCH`                    | 表示 `catch` 块。                             |
| `ClassTree`                  | `Tree.Kind.CLASS`                    | 表示类声明。                                  |
| `CompilationUnitTree`        | `Tree.Kind.COMPILATION_UNIT`         | 表示整个编译单元（通常是单个 Java 文件）。    |
| `ConditionalExpressionTree`  | `Tree.Kind.CONDITIONAL_EXPRESSION`   | 表示条件表达式（三元运算符）。                |
| `ContinueStatementTree`      | `Tree.Kind.CONTINUE_STATEMENT`       | 表示 `continue` 语句。                        |
| `DoWhileStatementTree`       | `Tree.Kind.DO_WHILE_STATEMENT`       | 表示 `do-while` 循环语句。                    |
| `EmptyStatementTree`         | `Tree.Kind.EMPTY_STATEMENT`          | 表示空语句（`;`）。                           |
| `EnhancedForLoopTree`        | `Tree.Kind.ENHANCED_FOR_LOOP`        | 表示增强型 `for` 循环（`for-each` 循环）。    |
| `ExpressionStatementTree`    | `Tree.Kind.EXPRESSION_STATEMENT`     | 表示表达式语句。                              |
| `ForStatementTree`           | `Tree.Kind.FOR_STATEMENT`            | 表示 `for` 循环语句。                         |
| `IfStatementTree`            | `Tree.Kind.IF_STATEMENT`             | 表示 `if` 语句。                              |
| `IdentifierTree`             | `Tree.Kind.IDENTIFIER`               | 表示标识符（变量名、方法名等）。              |
| `ImportClauseTree`           | `Tree.Kind.IMPORT_CLAUSE`            | 表示导入声明中的导入子句。                    |
| `ImportDeclarationTree`      | `Tree.Kind.IMPORT_DECLARATION`       | 表示导入声明。                                |
| `InstanceOfExpressionTree`   | `Tree.Kind.INSTANCE_OF_EXPRESSION`   | 表示 `instanceof` 表达式。                    |
| `LabeledStatementTree`       | `Tree.Kind.LABELED_STATEMENT`        | 表示带标签的语句。                            |
| `LiteralTree`                | `Tree.Kind.LITERAL`                  | 表示字面量（如字符串、数字等）。              |
| `MemberSelectExpressionTree` | `Tree.Kind.MEMBER_SELECT`            | 表示成员选择表达式（如 `obj.field`）。        |
| `MethodInvocationTree`       | `Tree.Kind.METHOD_INVOCATION`        | 表示方法调用。                                |
| `MethodReferenceTree`        | `Tree.Kind.METHOD_REFERENCE`         | 表示方法引用。                                |
| `MethodTree`                 | `Tree.Kind.METHOD`                   | 表示方法声明。                                |
| `ModifierListTree`           | `Tree.Kind.MODIFIER_LIST`            | 表示修饰符列表（如 `public`, `private` 等）。 |
| `NewArrayTree`               | `Tree.Kind.NEW_ARRAY`                | 表示数组实例化。                              |
| `NewClassTree`               | `Tree.Kind.NEW_CLASS`                | 表示类实例化（`new` 关键字）。                |
| `PackageDeclarationTree`     | `Tree.Kind.PACKAGE_DECLARATION`      | 表示包声明。                                  |
| `ParameterizedTypeTree`      | `Tree.Kind.PARAMETERIZED_TYPE`       | 表示参数化类型（泛型）。                      |
| `ParenthesizedTree`          | `Tree.Kind.PARENTHESISED_EXPRESSION` | 表示括号内的表达式或语句。                    |
| `ReturnStatementTree`        | `Tree.Kind.RETURN_STATEMENT`         | 表示 `return` 语句。                          |
| `SwitchStatementTree`        | `Tree.Kind.SWITCH_STATEMENT`         | 表示 `switch` 语句。                          |
| `SynchronizedStatementTree`  | `Tree.Kind.SYNCHRONIZED_STATEMENT`   | 表示 `synchronized` 语句。                    |
| `ThrowStatementTree`         | `Tree.Kind.THROW_STATEMENT`          | 表示 `throw` 语句。                           |
| `TryStatementTree`           | `Tree.Kind.TRY_STATEMENT`            | 表示 `try` 语句。                             |
| `TypeArgumentListTree`       | `Tree.Kind.TYPE_ARGUMENT_LIST`       | 表示类型参数列表。                            |
| `TypeCastTree`               | `Tree.Kind.TYPE_CAST`                | 表示类型转换。                                |
| `TypeParameterTree`          | `Tree.Kind.TYPE_PARAMETER`           | 表示类型参数。                                |
| `UnaryExpressionTree`        | `Tree.Kind.UNARY_EXPRESSION`         | 表示一元表达式（如 `++`, `--` 等）。          |
| `UnionTypeTree`              | `Tree.Kind.UNION_TYPE`               | 表示联合类型（Java 15 及以上版本）。          |
| `VariableTree`               | `Tree.Kind.VARIABLE`                 | 表示变量声明。                                |
| `WhileStatementTree`         | `Tree.Kind.WHILE_STATEMENT`          | 表示 `while` 循环语句。                       |

这些 AST 节点涵盖了 Java 语言中的各种结构，从简单的表达式到复杂的控制流语句。通过这些节点，你可以编写自定义规则来检查和分析 Java 代码。



#### 开发过程

------

下面以一个例子去说明，如果开发一个自定义的java扫描规则。



**场景描述：**

在一个switch块内，每个case都要break/return来终止；且必须包含default语句。



通过以上描述，我们可以知道规则主要是扫描switch语句，是属于流程控制类的，所以规则源码应该放到com.elvis.sonar.java.checks.flowcontrol 包里面。



规则通常继承 IssuableSubscriptionVisitor 即可，因为可以在 nodesToVisit 方法中，配置需要访问的AST节点，使用起来相对灵活。

当然，如果只想单独访问某些节点，也可以选择继承其他Visitor，如BaseTreeVisitor，PrinterVisitor等。



**编写规则代码：**

```java
package com.elvis.sonar.java.checks.flowcontrol;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 在一个switch块内，每个case都要break/return来终止；且必须包含default语句。
 * @since 2024/9/29 22:04
 */
// Rule注解里面的Key，就是规则名称，后续的规则json配置文件，及html文件，也必须严格按照此字段命名，否则会报找不到规则的异常
@Rule(key = "SwitchStatementRule")
public class SwitchStatementRule extends IssuableSubscriptionVisitor {

    private static final String DEFAULT_TEXT = "default";
    private static final String SWITCH_MUST_HAVE_DEFAULT_MESSAGE = "switch块缺少default语句";
    private static final String CASE_MUST_HAVE_BREAK_OR_RETURN_MESSAGE = "switch中每个case需要通过break/return等来终止";
    
    /**
     * 配置需要访问的AST节点，枚举详见抽象语法树章节
     * @return
     */
    @Override
    public List<Tree.Kind> nodesToVisit() {
        /*
         * 可配置多个需要访问的语法树
         */
        return Arrays.asList(Tree.Kind.SWITCH_STATEMENT,Tree.Kind.IF_STATEMENT);
    }

    /**
     * 进入对应语法树时，需要做什么
     * 通常在这里实现你的规则
     * @return
     */
    @Override
    public void visitNode(Tree tree) {
        // 如果要访问多个抽象树时，必须要判断抽象树类型，再进行实际操作
        if (tree.is(Kind.SWITCH_STATEMENT)) {
            // 抽象树基础接口，强转为SWITCH语句抽象树
            SwitchStatementTree switchStatement = (SwitchStatementTree) tree;
            // 检查是否有default分支
            checkDefault(switchStatement);
            // 检查每个case是否有有break/return来终止
            checkFallThrough(switchStatement);
        } else if (tree.is(Kind.IF_STATEMENT)) {
            // IF语句抽象树的规则代码实现
            // 这个规则里面其实没必要进入IF语句抽象树，只是为了让大家明白要访问多个抽象树时怎么写
            ......
        }
    }
    
    /**
     * 离开对应语法树时，需要做什么
     * 例如离开对应的switch块，或者if块后，需要释放什么资源，就在这里写
     * @return
     */
    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Kind.SWITCH_STATEMENT)) {
            ....
        } else if (tree.is(Kind.IF_STATEMENT)) {
            ....
        }
    }

    /**
     * 检查switch语句中是否包含default分支
     * @param switchStatement
     */
    private void checkDefault(SwitchStatementTree switchStatement) {
        boolean hasDefault = false;
        List<CaseGroupTree> caseGroupTrees = switchStatement.cases();
        for (CaseGroupTree caseGroup : caseGroupTrees) {
            String caseName = getNameByCaseLabel(caseGroup);
            if (DEFAULT_TEXT.equals(caseName)) {
                hasDefault = true;
                break;
            }
        }
        if (!hasDefault) {
            /*
             * 如果不符合规则要求，则汇报问题
             * reportIssue 第一个参数，就是问题出现的AST节点，这个直接决定了问题报告内，问题代码的范围
             * 这个可以是一个代码块，也可以是代码行内的一个片段（因为AST节点都是基于Tree去实现）
             * 例如这里，如果switch语句内没有包含default，则汇报的问题应该是整块switch语句
             */
            reportIssue(switchStatement, SWITCH_MUST_HAVE_DEFAULT_MESSAGE);
        }
    }

    /**
     * 检查switch语句中是否存在case分支的fall-through情况
     * @param switchStatement
     */
    private void checkFallThrough(SwitchStatementTree switchStatement) {
        for (CaseGroupTree caseGroup : switchStatement.cases()) {
            String caseName = getNameByCaseLabel(caseGroup);
            if (DEFAULT_TEXT.equals(caseName)) {
                continue;
            }
            List<StatementTree> statements = caseGroup.body();
            if (statements.isEmpty() || !isTerminating(statements.get(statements.size() - 1))) {
                /*
                 * 如果不符合规则要求，则汇报问题
                 * 如果switch语句内没有包含return或break，则汇报的问题应该是对应的case语句块
                 */
                reportIssue(caseGroup, CASE_MUST_HAVE_BREAK_OR_RETURN_MESSAGE);
            }
        }
    }

    /**
     * 获取case分支的名称
     * @param caseGroup
     * @return
     */
    private String getNameByCaseLabel(CaseGroupTree caseGroup) {
        if (caseGroup == null || caseGroup.labels() == null
                || caseGroup.labels() == null || caseGroup.labels().size() == 0) {
            return null;
        }
        return caseGroup.labels().get(0).caseOrDefaultKeyword().text();
    }

    /**
     * 检查语句是否是终止语句
     * @param statement
     * @return
     */
    private boolean isTerminating(Tree statement) {
        return statement.is(Tree.Kind.BREAK_STATEMENT, Tree.Kind.RETURN_STATEMENT, Tree.Kind.THROW_STATEMENT, Tree.Kind.CONTINUE_STATEMENT)
                || (statement.is(Tree.Kind.IF_STATEMENT) && isIfWithTerminatingThenOrElse((IfStatementTree) statement));
    }

    /**
     * 检查if语句是否包含终止的then或else分支
     * @param ifStatement
     * @return
     */
    private boolean isIfWithTerminatingThenOrElse(IfStatementTree ifStatement) {
        // 检查 then 分支是否终止
        boolean thenTerminates = ifStatement.thenStatement() != null && isTerminating(ifStatement.thenStatement());

        /* 检查 else 分支是否终止
         * 如果没有 else 分支，则认为它不违反规则
         */
        boolean elseTerminates = (ifStatement.elseStatement() instanceof StatementTree)
                ? isTerminating(ifStatement.elseStatement())
                : true;

        return thenTerminates || elseTerminates;
    }

}
```



**规则的绑定：**

编写完规则以后，需要绑定规则到RulesList.java，已按规则类型对集合进行了划分，下面是绑定 SwitchStatementRule 规则的例子：

![image-20241016112512701](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016112512701.png?token=GHSAT0AAAAAACZTIZV7XG3UERV4K3UD3VJCZY67UGA)





**编写规则描述文件：**

绑定规则以后，到 src/main/resources/org.sonar.l10n.javarules.java.*，编写规则描述文件

![image-20241016112742611](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016112742611.png?token=GHSAT0AAAAAACZTIZV7F3UIP4Y7PGYD3GRCZY67UQA)



SwitchStatementRule.json 用于对 SonarQube 提供的规则配置信息，文件名称必须与规则Rule注解的key一致。

如需查看枚举值的含义，请查阅**父级项目的README.md**。

```json
{
  "title": "在一个switch块内，每个case都要break/return等来终止；且必须包含default语句。", // 规则的描述标题
  "type": "CODE_SMELL", // 规则类型，可以是：BUG，VULNERABILITY，CODE_SMELL，SECURITY_HOTSPOT
  "status": "ready", // 规则的状态，可以是：ready, deprecated, beta
  "remediation": { // 修复该问题所需的时间估计
    "func": "Constant\/Issue", // 表示修复这个问题是固定时间成本，一般都是选择固定时间成本·		·
    "constantCost": "5min" // 常量成本，表示修复这个问题大约需要5分钟
  },
  "tags": [ // 与规则相关的标签，用于分类和搜索
    "elvis-java" // 自定义标签，可能是公司内部使用的特定标签，elvis-java必须写
  ],
  "defaultSeverity": "CRITICAL", // 默认严重性级别，可以是：BLOCKER, CRITICAL, MAJOR, MINOR, INFO
  "sqKey": "SwitchStatementRule", // SonarQube规则的唯一标识符，与规则Rule注解的key一致
  "scope": "Main" // 规则的作用范围，可以是：Main, Test
}
```



SwitchStatementRule.html 是对规则的详细描述，包括为什么命中这个规则，需要注意的事项，正例、错例等。文件名称必须与规则Rule注解的key一致。

```html
<p>在一个switch块内，每个case都需要通过break/return等来终止；在一个switch块内，都必须包含一个default语句并且放在最后，即使它什么代码也没有。</p>
<p>示例：</p>
<pre>
    switch (x) {
        case 1:
            break;
        case 2:
            break;
        default:
    }
</pre>
```



**编写单元测试方法：**

根据规则类型，到 src/test/java/com.elvis.sonar.java.checks.*，编写单元测试方法

![image-20241028103344646](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241028103344646.png?token=GHSAT0AAAAAACZTIZV6BB34VKPWU7P7ZW3CZY67UZQ)



```java
package com.elvis.sonar.java.checks.flowcontrol;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 在一个switch块内，每个case都要break/return来终止，或注释说明执行到哪一个case为止；且必须包含default语句。
 * @since 2024/9/26 17:35
 **/
public class SwitchStatementRuleTest {

    @Test
    void check() {

        /*
         * onFile 就是规则的测试用例
         * withCheck 就是需要测试的规则类
         */
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/flowcontrol/SwitchStatementRule.java")
                .withCheck(new SwitchStatementRule())
                .verifyIssues();
    }
}
```



**编写测试用例：**

根据规则类型，到 src/test/files/*，编写测试用例

![image-20241016144926957](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016144926957.png?token=GHSAT0AAAAAACZTIZV6L5MLVXU7VGEBLIOSZY67VFQ)



```java
package flowcontrol;

class SwitchStatementRule {
    
    public int check(int number) {
        switch (number) { // Compliant
            case 1:
                number = number + 10;
                break;
            case 2:
                number = number + 100;
                break;
            default:
        }
        return number;
    }

    public int check2(int number) {
        switch (number) { // Noncompliant {{switch块缺少default语句}}
            case 1:
                number = number + 10;
                break;
            case 2:
                number = number + 100;
                break;
        }
        return number;
    }

    public int check3(int number) {
        switch (number) {
            case 1:
                number = number + 10;
                break;
            case 2: // Noncompliant {{switch中每个case需要通过break/return等来终止}}
                number = number + 100;
            default:
        }
        return number;
    }
}
```

如上所示，编写了3个用例，问题行分别以 Compliant 及 Noncompliant 注释，**注意英文字母的左右都是有空格的**，否则程序无法理解为是判断标记。



**Compliant** 代表是 **预期会通过扫描** 的行

**Noncompliant** 代表是 **预期会被报告问题** 的行，被 {{}} 包裹的内容为 预期的提示信息，预期的提示信息可有可无，但加上了标记，就会认为是需要判断。



一个测试案例文件，**至少需要有一项预期会被报告的问题**，否则单元测试会报错。



如果单元测试运行结果出现问题，如下图所示，则代表测试用例的第19行不符合预期。

例如此行应该会汇报问题的，但注释了 Compliant。如果确定标记没问题，则建议在规则代码内下断点进行调试。

![image-20241016150007791](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016150007791.png?token=GHSAT0AAAAAACZTIZV6BXXYEYMP24QWW7PEZY67VVQ)



另一种情况，如下图所示，则代表测试用例的 Noncompliant 提示信息，不符合实际预期。可自行判断是用例问题还是规则代码问题。

![image-20241016150459853](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016150459853.png?token=GHSAT0AAAAAACZTIZV6BHVYNZGANQD6MHGEZY67WCQ)



如果单元测试通过，会返回成功，如下图所示：

![image-20241016145902403](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016145902403.png?token=GHSAT0AAAAAACZTIZV6CHDHPSTQCWHBQRRUZY67WNA)





#### 规则的发布

------

在IDEA中，对项目执行 clean 和 install

![image-20241028103710091](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241028103710091.png?token=GHSAT0AAAAAACZTIZV7LGERLM5UEBOYQK54ZY67WZQ)



install 执行完以后，会提示 Build Success

![image-20241028104101098](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241028104101098.png?token=GHSAT0AAAAAACZTIZV7NCB4NABRWRAA6FYAZY67XNA)



会在项目target目录下，找到需要发布的jar包

![image-20241028104143118](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241028104143118.png?token=GHSAT0AAAAAACZTIZV7G6Y5LIKU36WCSGEEZY67XXA)



发布完成后，进入SonarQube控制台，在 质量配置 -> Java -> elvis Java Rules 的规则界面中，你能找到自己开发的自定义规则。

![image-20241016152520407](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016152520407.png?token=GHSAT0AAAAAACZTIZV6S3WK4VEA22VMJBXWZY67YSA)



![image-20241016152553449](https://raw.githubusercontent.com/elvisfung/sonarjava-p3c/refs/heads/main/images/image-20241016152553449.png?token=GHSAT0AAAAAACZTIZV6RSJS2UDUHUDA7HKYZY67Y4Q)
