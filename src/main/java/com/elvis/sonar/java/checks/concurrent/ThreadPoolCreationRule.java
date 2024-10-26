package com.elvis.sonar.java.checks.concurrent;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengbingjian
 * @description 线程池应该手工创建，避免使用Executors自动创建
 * @since 2024/9/29 22:04
 **/
@Rule(key = "ThreadPoolCreationRule")
public class ThreadPoolCreationRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "线程池应该手工创建，避免使用Executors自动创建";
    private static final String EXECUTORS_CLASS_NAME = "java.util.concurrent.Executors";
    private static final Set<String> ALLOW_METHODS = new HashSet<>();

    static {
        ALLOW_METHODS.add("newScheduledThreadPool");
        ALLOW_METHODS.add("newSingleThreadScheduledExecutor");
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
        ExpressionTree methodSelect = methodInvocation.methodSelect();
        if (methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
            ExpressionTree expression = memberSelect.expression();
            if (expression.is(Tree.Kind.IDENTIFIER)) {
                IdentifierTree identifier = (IdentifierTree) expression;
                String methodName = memberSelect.identifier().name();
                if (EXECUTORS_CLASS_NAME.equals(identifier.symbolType().fullyQualifiedName()) && !ALLOW_METHODS.contains(methodName)) {
                    reportIssue(tree, MESSAGE);
                }
            }
        }
    }
}