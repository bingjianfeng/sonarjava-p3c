package com.elvis.sonar.java.checks.set;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 应使用大小一致类型一致的数组参数
 * @since 2024/9/29 22:04
 */
@Rule(key = "ClassCastExceptionWithToArrayRule")
public class ClassCastExceptionWithToArrayRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】应使用大小一致类型一致的数组参数";
    private static final String TO_ARRAY_NAME = "toArray";

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.METHOD_INVOCATION, Kind.TYPE_CAST);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.METHOD_INVOCATION)) {
            checkToArrayMethod((MethodInvocationTree) tree);
        }
    }

    private void checkToArrayMethod(MethodInvocationTree methodInvocation) {
        // 检查方法名是否是 toArray 并且没有参数
        if (MethodInvocationTreeCheckUtil.isMethodCall(TO_ARRAY_NAME, methodInvocation) && methodInvocation.arguments().isEmpty()) {
            reportIssue(methodInvocation, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getName(methodInvocation)));
        }
    }
}