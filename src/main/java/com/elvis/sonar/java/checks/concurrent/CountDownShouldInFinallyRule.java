package com.elvis.sonar.java.checks.concurrent;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description CountDownLatch使用中，每个线程退出前必须调用countDown方法。
 * 规则主要检查以下情况：
 * 1.如果方法参数内没有出现 CountDownLatch，则跳过；
 * 2.如果有CountDownLatch，一定要有countDown方法的调用，如果没有则报告问题；
 * 3. countDown方法的调用，一定要在finally块内，在其他位置则报告问题。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "CountDownShouldInFinallyRule")
public class CountDownShouldInFinallyRule extends IssuableSubscriptionVisitor {

    private static final String COUNT_DOWN_MUST_IN_FINALLY_MESSAGE = "countDown()应该在finally块中调用";
    private static final String COUNT_DOWN_LATCH_MUST_COUNT_DOWN_MESSAGE = "使用CountDownLatch时，每个线程退出前必须在finally块中执行countDown()";

    private static final String COUNT_DOWN_LATCH_PACKAGE_NAME = "java.util.concurrent.CountDownLatch";
    private static final String COUNT_DOWN_METHOD_NAME = "countDown";

    // 是否在方法入参里有CountDownLatch
    private boolean hasCountDownLatchAsParameter;

    // 是否在finally块里有执行countDown
    private boolean countDownCalledInFinally;

    //是否在finally以外发现countDown
    private boolean findCountDownOutSideFinally;

    private boolean inFinallyBlock;


    /**
     * 判断标志的初始化
     */
    private void initial(){
        hasCountDownLatchAsParameter = false;
        countDownCalledInFinally = false;
        findCountDownOutSideFinally = false;
        inFinallyBlock = false;
    }

    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        //只扫描方法、try-catch-finally块、表达式和声明
        return Arrays.asList(Tree.Kind.METHOD, Tree.Kind.TRY_STATEMENT, Tree.Kind.EXPRESSION_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD)) {
            //如果是方法，则进入判断
            initial();
            MethodTree methodTree = (MethodTree) tree;
            //检查是否存在CountDownLatch在方法参数
            checkForCountDownLatchInParameters(methodTree.parameters());
        } else if (tree.is(Tree.Kind.TRY_STATEMENT) && hasCountDownLatchAsParameter) {
            //如果是try-catch-finally，且参数存在CountDownLatch，则进入判断
            TryStatementTree tryStatement = (TryStatementTree) tree;
            //如果有finally块，则进入判断
            if (tryStatement.finallyBlock() != null) {
                //进入finally块前，要将在finally内设置为是
                inFinallyBlock = true;
                //判断finally块里面是否有countDown
                countDownCalledInFinally = hasCountDownCallInFinally(tryStatement.finallyBlock());
            }
        } else if (tree.is(Tree.Kind.EXPRESSION_STATEMENT) && hasCountDownLatchAsParameter) {
            //如果是表达式或声明，且参数存在CountDownLatch，则进入判断
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree) tree;
            if (expressionStatement.expression().is(Tree.Kind.METHOD_INVOCATION)) {
                MethodInvocationTree methodInvocation = (MethodInvocationTree) expressionStatement.expression();
                //判断除finally块以外，否有countDown
                if (MethodInvocationTreeCheckUtil.isMethodCall(COUNT_DOWN_METHOD_NAME, COUNT_DOWN_LATCH_PACKAGE_NAME, methodInvocation) && !inFinallyBlock) {
                    findCountDownOutSideFinally = true;
                    reportIssue(expressionStatement, COUNT_DOWN_MUST_IN_FINALLY_MESSAGE);
                }
            }
        }
    }

    /**
     * 根据入参检查是否有CountDownLatch
     * @param parameters
     */
    private void checkForCountDownLatchInParameters(List<VariableTree> parameters) {
        for (VariableTree parameter : parameters) {
            Symbol typeSymbol = parameter.symbol().type().symbol();
            if (typeSymbol != null && COUNT_DOWN_LATCH_PACKAGE_NAME.equals(typeSymbol.type().fullyQualifiedName())) {
                hasCountDownLatchAsParameter = true;
                break;
            }
        }
    }

    /**
     * 扫描finally块的代码，查看是否有调用countDown方法
     * @param block
     * @return
     */
    private boolean hasCountDownCallInFinally(BlockTree block) {
        if (block == null) {
            return false;
        }
        for (StatementTree statement : block.body()) {
            if (statement.is(Tree.Kind.EXPRESSION_STATEMENT)) {
                ExpressionStatementTree expressionStatement = (ExpressionStatementTree) statement;
                if (expressionStatement.expression().is(Tree.Kind.METHOD_INVOCATION)) {
                    MethodInvocationTree methodInvocation = (MethodInvocationTree) expressionStatement.expression();
                    if (MethodInvocationTreeCheckUtil.isMethodCall(COUNT_DOWN_METHOD_NAME, COUNT_DOWN_LATCH_PACKAGE_NAME, methodInvocation)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 退出节点时会执行
     * @param tree
     */
    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD)) {
            /**
             * 如果是退出方法节点，则判断是否出现过countDown的调用，如果没有，则不存在countDOwn的报告问题
             * 非finally内调用countDown的报告，在前面已经发起，并设置了 findCountDownOutSideFinally 为 true
             * 所以不存在报告冲突的情况
             */
            if (hasCountDownLatchAsParameter && !countDownCalledInFinally && !findCountDownOutSideFinally) {
                reportIssue(tree, COUNT_DOWN_LATCH_MUST_COUNT_DOWN_MESSAGE);
            }
            initial();
        }else if(tree.is(Tree.Kind.TRY_STATEMENT)){
            //退出try-catch-finally时，要将在finally内设置为否
            inFinallyBlock = false;
        }
    }


}