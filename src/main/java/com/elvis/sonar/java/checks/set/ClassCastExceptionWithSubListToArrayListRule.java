package com.elvis.sonar.java.checks.set;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TypeCastTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description ArrayList的subList结果不可强转成ArrayList，否则会抛出ClassCastException异常。
 * @since 2024/9/29 22:04
 */
@Rule(key = "ClassCastExceptionWithSubListToArrayListRule")
public class ClassCastExceptionWithSubListToArrayListRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】的结果不可强转成ArrayList";
    private static final String ARRAY_LIST_PACKAGE_NAME = "java.util.ArrayList";
    private static final String SUB_LIST_NAME = "subList";

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.TYPE_CAST);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.TYPE_CAST)) {
            TypeCastTree typeCast = (TypeCastTree) tree;
            checkForSubListCast(typeCast);
        }
    }

    private void checkForSubListCast(TypeCastTree typeCast) {
        // 检查目标类型是否是 ArrayList
        if (ARRAY_LIST_PACKAGE_NAME.equals(typeCast.type().symbolType().fullyQualifiedName())) {
            ExpressionTree expression = typeCast.expression();
            if (expression.is(Kind.METHOD_INVOCATION)) {
                MethodInvocationTree methodInvocation = (MethodInvocationTree) expression;
                // 检查方法调用是否是 subList
                if (methodInvocation.methodSelect().is(Kind.MEMBER_SELECT)) {
                    MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodInvocation.methodSelect();
                    if (SUB_LIST_NAME.equals(memberSelect.identifier().name())) {
                        String name = memberSelect.expression().symbolType().name();
                        if(memberSelect.expression().is(Kind.IDENTIFIER)){
                            IdentifierTree idTree = (IdentifierTree) memberSelect.expression();
                            name = idTree.name();
                        }
                        reportIssue(typeCast, String.format(MESSAGE, name));
                    }
                }
            }
        }
    }
}