package com.elvis.sonar.java.checks.constant;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description long或者Long初始赋值时，必须使用大写的L，不能是小写的l，小写容易跟数字1混淆，造成误解。
 * @since 2024/10/08 16:30
 */
@Rule(key = "UpperEllRule")
public class UpperEllRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】应使用大写L";
    private static final String LOWERCASE_L = "l";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问长整型字面量节点
        return Arrays.asList(Tree.Kind.LONG_LITERAL);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.LONG_LITERAL)) {
            LiteralTree literal = (LiteralTree) tree;
            String value = literal.value();
            if (value != null && value.endsWith(LOWERCASE_L)) {
                reportIssue(literal, String.format(MESSAGE, value));
            }
        }
    }
}