package com.elvis.sonar.java.checks.set;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.TreeUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 在subList场景中，请注意对原列表的修改，容易触发ConcurrentModificationException异常。
 * @since 2024/9/29 22:04
 */
@Rule(key = "ConcurrentExceptionWithModifyOriginSubListRule")
public class ConcurrentExceptionWithModifyOriginSubListRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】在这里可能会导致ConcurrentModificationException";

    private static final List<String> MODIFY_METHODS = Arrays.asList("add", "remove", "clear");

    private static final String SUB_LIST_METHOD_NAME = "subList";

    // 标记是否在块内找到 subList 方法
    private boolean findSubListInBlock;

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.METHOD_INVOCATION)) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            checkSubListModification(methodInvocation);
        }
    }

    /**
     * 检查 subList 方法的修改
     *
     * @param methodInvocation
     */
    private void checkSubListModification(MethodInvocationTree methodInvocation) {
        // 检查方法调用是否是 subList 方法
        ExpressionTree methodSelect = methodInvocation.methodSelect();
        if (methodSelect.is(Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
            if (SUB_LIST_METHOD_NAME.equals(memberSelect.identifier().name())) {
                // 获取 subList 的接收者
                ExpressionTree receiver = memberSelect.expression();
                Symbol symbol = receiver.symbolType().symbol();
                if (symbol == null) {
                    return;
                }

                // 获取 subList 调用所在的块
                BlockTree block = (BlockTree) TreeUtil.getParent(methodInvocation, Tree.Kind.BLOCK);
                if (block == null) {
                    return;
                }

                // 检查块内的所有语句
                findSubListInBlock = false;
                for (StatementTree statement : block.body()) {
                    checkForModifyingOriginalList(symbol, statement);
                }
            }
        }
    }

    /**
     * 检查是否修改了原始列表
     *
     * @param originalListSymbol
     * @param statement
     */
    private void checkForModifyingOriginalList(Symbol originalListSymbol, StatementTree statement) {
        if (statement.is(Kind.VARIABLE)) {
            VariableTree variableTree = (VariableTree) statement;
            // 检查是否是 subList 方法的调用
            if (SUB_LIST_METHOD_NAME.equals(variableTree.simpleName().name())) {
                findSubListInBlock = true;
                return;
            }
        }
        if (statement.is(Kind.EXPRESSION_STATEMENT)) {
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree) statement;
            ExpressionTree expression = expressionStatement.expression();
            if (!expression.is(Kind.METHOD_INVOCATION)) {
                return;
            }
            MethodInvocationTree methodInvocation = (MethodInvocationTree) expression;
            String methodName = MethodInvocationTreeCheckUtil.getMethodName(methodInvocation);
            if (!MODIFY_METHODS.contains(methodName)) {
                return;
            }
            // 获取方法调用的接收者
            ExpressionTree receiver = MethodInvocationTreeCheckUtil.getReceiver(methodInvocation);

            // 检查是否在 subList 方法调用后修改了原始列表
            boolean hitRule = findSubListInBlock && receiver != null && receiver.symbolType().symbol() == originalListSymbol;
            if (hitRule) {
                reportIssue(methodInvocation, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getMethodName(methodInvocation)));
            }
        }
    }
}