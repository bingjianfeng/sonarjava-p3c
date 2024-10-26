package com.elvis.sonar.java.checks.concurrent;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.SourceContextUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 锁必须紧跟try代码块，且unlock要放到finally第一行。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "LockShouldWithTryFinallyRule")
public class LockShouldWithTryFinallyRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "锁【%s】必须紧跟try代码块，且unlock要放到finally第一行。";

    private static final String LOCK_PACKAGE_NAME = "java.util.concurrent.locks.Lock";
    private static final String LOCK_METHOD_NAME = "lock";
    private static final String UNLOCK_METHOD_NAME = "unlock";

    // 寄存执行lock时的抽象树
    private MethodInvocationTree lockCodeTree;


    /**
     * 判断标志的初始化
     */
    private void initial() {
        lockCodeTree = null;
    }

    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        //只扫描方法、try-catch-finally块、表达式和声明
        return Arrays.asList(Tree.Kind.TRY_STATEMENT, Tree.Kind.EXPRESSION_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.TRY_STATEMENT)) {
            TryStatementTree tryStatement = (TryStatementTree) tree;
            //如果有finally块，则进入判断
            if (tryStatement.finallyBlock() != null) {
                if (lockCodeTree != null) {
                    checkUnlockInFinally(tryStatement.finallyBlock());
                }
            }
        } else if (tree.is(Tree.Kind.EXPRESSION_STATEMENT)) {
            //如果是表达式或声明，且参数存在CountDownLatch，则进入判断
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree) tree;
            if (expressionStatement.expression().is(Tree.Kind.METHOD_INVOCATION)) {
                MethodInvocationTree methodInvocation = (MethodInvocationTree) expressionStatement.expression();
                checkLockExistAndBeforeTry(methodInvocation);
            }
        }
    }

    /**
     * 扫描代码内是否有lock的调用
     * 如果有，则校验下一行是否try
     * 如果不是则报告问题，如果是则登记位置
     * @param methodInvocation
     */
    private void checkLockExistAndBeforeTry(MethodInvocationTree methodInvocation){
        if (MethodInvocationTreeCheckUtil.isMethodCallWithInterfaceImplement(LOCK_METHOD_NAME, LOCK_PACKAGE_NAME, methodInvocation)) {
            int lockInLine = methodInvocation.lastToken().line();
            if(!SourceContextUtil.keywordInNextLine(context,lockInLine,"try{",true,true)){
                reportIssue(methodInvocation, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getName(methodInvocation)));
            } else {
                lockCodeTree = methodInvocation;
            }
        }
    }

    /**
     * 扫描finally块的代码，查看是否有调用unlock方法
     * 如果没有则报告问题
     *
     * @param block
     * @return
     */
    private void checkUnlockInFinally(BlockTree block) {
        boolean isUnlockInFinallyBlock = false;
        if (block == null) {
            return;
        }
        for (StatementTree statement : block.body()) {
            if (statement.is(Tree.Kind.EXPRESSION_STATEMENT)) {
                ExpressionStatementTree expressionStatement = (ExpressionStatementTree) statement;
                if (expressionStatement.expression().is(Tree.Kind.METHOD_INVOCATION)) {
                    MethodInvocationTree methodInvocation = (MethodInvocationTree) expressionStatement.expression();
                    if (MethodInvocationTreeCheckUtil.isMethodCallWithInterfaceImplement(UNLOCK_METHOD_NAME, LOCK_PACKAGE_NAME, methodInvocation)
                        && SourceContextUtil.keywordInPreLine(context,methodInvocation.lastToken().line(),"finally{",true,true)) {
                        isUnlockInFinallyBlock = true;
                        break;
                    }
                }
            }
        }
        if (!isUnlockInFinallyBlock) {
            reportIssue(lockCodeTree, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getName(lockCodeTree)));
        }
    }

    /**
     * 退出节点时会执行
     *
     * @param tree
     */
    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD)) {
            initial();
        }
    }

}