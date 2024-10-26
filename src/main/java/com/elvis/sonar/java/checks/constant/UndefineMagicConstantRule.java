package com.elvis.sonar.java.checks.constant;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 不允许任何魔法值（即未经定义的常量）直接出现在代码中。
 * @since 2024/10/08 16:30
 */
@Rule(key = "UndefineMagicConstantRule")
public class UndefineMagicConstantRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "魔法值【%s】";
    private static final List<String> LITERAL_WHITE_LIST = Arrays.asList("0", "1", "-1", "true", "false");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问字面量节点
        return Arrays.asList(Tree.Kind.INT_LITERAL, Tree.Kind.LONG_LITERAL, Tree.Kind.FLOAT_LITERAL,
                Tree.Kind.DOUBLE_LITERAL, Tree.Kind.CHAR_LITERAL, Tree.Kind.STRING_LITERAL, Tree.Kind.BOOLEAN_LITERAL);
    }

    @Override
    public void visitNode(Tree tree) {
        LiteralTree literal = (LiteralTree) tree;
        if (isInBlackList(literal)) {
            reportIssue(literal, String.format(MESSAGE, literal.value()));
        }
    }

    /**
     * 检查是否魔法值
     * @param literal
     * @return
     */
    private boolean isInBlackList(LiteralTree literal) {
        String value = literal.value();
        // 如果在白名单内则忽略
        if (value == null || LITERAL_WHITE_LIST.contains(value)) {
            return false;
        }

        Tree parent = literal.parent();
        while (parent != null) {
            if (parent.is(Tree.Kind.IF_STATEMENT)) {
                IfStatementTree ifStatement = (IfStatementTree) parent;
                if (!isPartOfLoop(ifStatement)) {
                    return true;
                }
            } else if (parent.is(Tree.Kind.FOR_STATEMENT) || parent.is(Tree.Kind.WHILE_STATEMENT)) {
                return true;
            }
            parent = parent.parent();
        }
        return false;
    }

    /**
     * 检查是否是循环语句的一部分
     * @param ifStatement
     * @return
     */
    private boolean isPartOfLoop(IfStatementTree ifStatement) {
        Tree parent = ifStatement.parent();
        while (parent != null) {
            if (parent.is(Tree.Kind.FOR_STATEMENT) || parent.is(Tree.Kind.WHILE_STATEMENT)) {
                return true;
            }
            parent = parent.parent();
        }
        return false;
    }
}