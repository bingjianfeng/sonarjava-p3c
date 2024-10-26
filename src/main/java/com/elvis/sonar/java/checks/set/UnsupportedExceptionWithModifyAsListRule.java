package com.elvis.sonar.java.checks.set;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.TreeUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author fengbingjian
 * @description 使用工具类Arrays.asList()把数组转换成集合时，不能使用其修改集合相关的方法，它的add/remove/clear方法会抛出UnsupportedOperationException异常。
 * @since 2024/9/29 22:04
 */
@Rule(key = "UnsupportedExceptionWithModifyAsListRule")
public class UnsupportedExceptionWithModifyAsListRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "这里使用【%s】可能会导致UnsupportedOperationException";
    private static final String AS_LIST_METHOD_NAME = "asList";
    private static final String ARRAYS_PACKAGE_NAME = "java.util.Arrays";

    private static final List<String> MODIFY_METHODS = Arrays.asList("add", "remove", "clear");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        ExpressionTree initializer = variableTree.initializer();
        if (initializer == null || !isArraysAsList(initializer)) {
            return;
        }
        String variableName = variableTree.simpleName().name();
        // 访问变量作用域内的代码块
        BlockTree block = (BlockTree) TreeUtil.getParent(variableTree, Tree.Kind.BLOCK);
        if (block == null) {
            return;
        }
        block.accept(new BaseTreeVisitor() {

            @Override
            public void visitMethodInvocation(MethodInvocationTree mit) {
                if (isModificationMethod(mit, variableName)) {
                    reportIssue(mit, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getMethodName(mit)));
                }
                super.visitMethodInvocation(mit);
            }

            private boolean isModificationMethod(MethodInvocationTree mit, String variableName) {
                // 检查方法名是否为.add, .remove, 或 .clear
                String methodName = mit.symbol().name();
                if (!MODIFY_METHODS.contains(methodName)) {
                    return false;
                }
                // 检查方法调用的目标是否是变量
                ExpressionTree methodSelect = mit.methodSelect();
                if (!methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
                    return false;
                }
                MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
                ExpressionTree expression = memberSelect.expression();
                if (!expression.is(Tree.Kind.IDENTIFIER)) {
                    return false;
                }
                IdentifierTree identifier = (IdentifierTree) expression;
                if (identifier.name().equals(variableName)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 判断初始化器是否是 Arrays.asList 的调用
     *
     * @param initializer 初始化器
     * @return 是否是 Arrays.asList 的调用
     */
    private boolean isArraysAsList(ExpressionTree initializer) {
        if (initializer.is(Tree.Kind.METHOD_INVOCATION)) {
            MethodInvocationTree mit = (MethodInvocationTree) initializer;
            if (MethodInvocationTreeCheckUtil.isMethodCall(AS_LIST_METHOD_NAME, ARRAYS_PACKAGE_NAME, mit)) {
                return true;
            }
        }
        return false;
    }
}