package com.elvis.sonar.java.checks.exception;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ReturnStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 请不要在finally中使用return
 * @since 2024/9/29 22:04
 **/
@Rule(key = "AvoidReturnInFinallyRule")
public class AvoidReturnInFinallyRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "请不要在finally中使用return";


    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        //只扫描方法、try-catch-finally块、表达式和声明
        return Arrays.asList(Tree.Kind.TRY_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.TRY_STATEMENT)) {
            //如果是try-catch-finally，且参数存在CountDownLatch，则进入判断
            TryStatementTree tryStatement = (TryStatementTree) tree;
            //如果有finally块，则进入判断
            if (tryStatement.finallyBlock() != null) {
                checkReturnInFinally(tryStatement.finallyBlock());
            }
        }
    }

    /**
     * 扫描finally块的代码
     * 如果有出现return则报告问题
     *
     * @param block
     * @return
     */
    private void checkReturnInFinally(BlockTree block) {
        if (block == null) {
            return;
        }
        for (StatementTree statement : block.body()) {
            if (statement.is(Tree.Kind.RETURN_STATEMENT)) {
                ReturnStatementTree returnStatement = (ReturnStatementTree) statement;
                reportIssue(returnStatement, MESSAGE);
            }
        }
    }

}