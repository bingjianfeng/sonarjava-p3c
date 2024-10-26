package com.elvis.sonar.java.checks.oop;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 请不要在循环体内使用"+"连接字符串
 * @since 2024/10/08 16:30
 */
@Rule(key = "StringConcatRule")
public class StringConcatRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "请不要在循环体内使用\"+\"连接字符串";
    private static final String STRING_PACKAGE_NAME = "java.lang.String";


    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问for、while和do-while循环节点
        return Arrays.asList(Tree.Kind.FOR_EACH_STATEMENT, Tree.Kind.FOR_STATEMENT, Tree.Kind.WHILE_STATEMENT, Tree.Kind.DO_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        StatementTree statementTree = null;
        if (tree.is(Tree.Kind.FOR_STATEMENT, Tree.Kind.WHILE_STATEMENT, Tree.Kind.DO_STATEMENT)) {
            statementTree = (StatementTree) tree;
        }else if (tree.is(Tree.Kind.FOR_EACH_STATEMENT)) {
            statementTree = ((ForEachStatement) tree).statement();
        }
        checkStringConcat(statementTree);
    }

    private void checkStringConcat(StatementTree statementTree) {
        // 检查循环体中的加法表达式
        if(!statementTree.is(Tree.Kind.BLOCK)){
            return;
        }
        for (StatementTree child : ((BlockTree) statementTree).body()) {
            if (!child.is(Tree.Kind.EXPRESSION_STATEMENT)) {
                continue;
            }
            ExpressionStatementTree expressionStatement = (ExpressionStatementTree) child;
            if (!expressionStatement.expression().is(Tree.Kind.ASSIGNMENT)) {
                continue;
            }
            AssignmentExpressionTree assignment = (AssignmentExpressionTree) expressionStatement.expression();
            boolean identifier = assignment.variable().is(Tree.Kind.IDENTIFIER);
            boolean stringConcatenation = isStringConcatenation(assignment.expression());
            if (identifier && stringConcatenation) {
                reportIssue(assignment, MESSAGE);
            }
        }
    }

    private boolean isStringConcatenation(ExpressionTree expression) {
        // 检查表达式是否是字符串连接操作
        if (expression.is(Tree.Kind.PLUS)) {
            BinaryExpressionTree binaryExpr = (BinaryExpressionTree) expression;
            return isString(binaryExpr.leftOperand()) || isString(binaryExpr.rightOperand());
        }
        return false;
    }

    /**
     * 判断是否字符串
     * @param expr
     * @return
     */
    private boolean isString(ExpressionTree expr) {
        return STRING_PACKAGE_NAME.equals(expr.symbolType().fullyQualifiedName());
    }
}