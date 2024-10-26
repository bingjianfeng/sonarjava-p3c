package com.elvis.sonar.java.checks.set;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author fengbingjian
 * @description 不要在foreach循环里进行元素的remove/add操作，remove元素请使用Iterator方式。
 * @since 2024/9/29 22:04
 */
@Rule(key = "DontModifyInForeachCircleRule")
public class DontModifyInForeachCircleRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "不要在foreach遍历中使用【%s】";

    private static final List<String> MODIFY_METHODS = Arrays.asList("add", "remove", "clear");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.FOR_EACH_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        ForEachStatement forEachStatement = (ForEachStatement) tree;

        // 访问foreach循环体
        forEachStatement.statement().accept(new BaseTreeVisitor() {

            @Override
            public void visitMethodInvocation(MethodInvocationTree mit) {
                if(!forEachStatement.expression().is(Tree.Kind.IDENTIFIER)){
                    return;
                }
                IdentifierTree forEachElmIdentifierTree = (IdentifierTree) forEachStatement.expression();
                if (isModificationMethod(mit, forEachElmIdentifierTree.symbol().name())) {
                    reportIssue(mit, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getMethodName(mit)));
                }
                super.visitMethodInvocation(mit);
            }

            /**
             * 检查是否是修改方法
             * @param methodInvocationTree
             * @param forEachListName
             * @return
             */
            private boolean isModificationMethod(MethodInvocationTree methodInvocationTree, String forEachListName) {
                // 检查方法名是否为.add, .remove, 或 .clear
                String methodName = MethodInvocationTreeCheckUtil.getMethodName(methodInvocationTree);
                if (MODIFY_METHODS.contains(methodName)) {
                    // 检查方法调用的目标是否是foreach循环的迭代变量
                    ExpressionTree methodSelect = methodInvocationTree.methodSelect();
                    if (!methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
                        return false;
                    }
                    MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
                    ExpressionTree expression = memberSelect.expression();
                    if (!expression.is(Tree.Kind.IDENTIFIER)) {
                        return false;
                    }
                    // 获取修改方法的变量名称，与foreach循环的迭代变量名称进行比较，如果相同则返回true
                    IdentifierTree identifier = (IdentifierTree) expression;
                    if (identifier.symbol().name().equals(forEachListName)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
}