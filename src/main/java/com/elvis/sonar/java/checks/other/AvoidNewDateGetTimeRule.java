package com.elvis.sonar.java.checks.other;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description 请使用System.currentTimeMillis()代替new Date().getTime()
 * @since 2024/9/29 15:46
 **/
@Rule(key = "AvoidNewDateGetTimeRule")
public class AvoidNewDateGetTimeRule extends IssuableSubscriptionVisitor {

    private static final String DATE_PACKAGE_PATH = "java.util.Date";
    private static final String GET_TIME_METHOD_NAME = "getTime";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
        Symbol methodSymbol = methodInvocation.symbol();
        if (methodSymbol != null && GET_TIME_METHOD_NAME.equals(methodSymbol.name())) {
            ExpressionTree methodSelect = methodInvocation.methodSelect();
            if (methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
                ExpressionTree expression = ((org.sonar.plugins.java.api.tree.MemberSelectExpressionTree) methodSelect).expression();
                if (expression.is(Tree.Kind.NEW_CLASS)) {
                    TypeTree type = ((org.sonar.plugins.java.api.tree.NewClassTree) expression).identifier();
                    if (DATE_PACKAGE_PATH.equals(type.symbolType().fullyQualifiedName())) {
                        reportIssue(methodInvocation, "请使用System.currentTimeMillis()代替new Date().getTime()");
                    }
                }
            }
        }
    }
}