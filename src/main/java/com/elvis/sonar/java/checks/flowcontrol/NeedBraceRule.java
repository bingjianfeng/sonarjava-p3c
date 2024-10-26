package com.elvis.sonar.java.checks.flowcontrol;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 语句缺少大括号
 * @since 2024/9/29 22:04
 */
@Rule(key = "NeedBraceRule")
public class NeedBraceRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "%s语句缺少大括号";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(
                Tree.Kind.IF_STATEMENT,
                Tree.Kind.FOR_STATEMENT,
                Tree.Kind.WHILE_STATEMENT,
                Tree.Kind.DO_STATEMENT
        );
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.IF_STATEMENT)) {
            checkIfStatement((IfStatementTree) tree);
        } else if (tree.is(Tree.Kind.FOR_STATEMENT)) {
            checkForStatement((ForStatementTree) tree);
        } else if (tree.is(Tree.Kind.WHILE_STATEMENT)) {
            checkWhileStatement((WhileStatementTree) tree);
        } else if (tree.is(Tree.Kind.DO_STATEMENT)) {
            checkDoWhileStatement((DoWhileStatementTree) tree);
        }
    }

    /**
     * 检查if语句的语句是否缺少大括号
     *
     * @param ifStatement
     */
    private void checkIfStatement(IfStatementTree ifStatement) {
        checkSingleStatement(ifStatement.thenStatement());
        if (ifStatement.elseStatement() != null) {
            checkElseStatement(ifStatement.elseStatement());
        }
    }

    /**
     * 递归检查else语句及其嵌套的else if语句
     *
     * @param statement
     */
    private void checkElseStatement(StatementTree statement) {
        if (statement.is(Tree.Kind.IF_STATEMENT)) {
            IfStatementTree elseIfStatement = (IfStatementTree) statement;
            checkSingleStatement(elseIfStatement.thenStatement());
            if (elseIfStatement.elseStatement() != null) {
                checkElseStatement(elseIfStatement.elseStatement());
            }
        } else {
            checkSingleStatement(statement);
        }
    }

    /**
     * 检查单个语句是否缺少大括号
     *
     * @param statement
     */
    private void checkSingleStatement(StatementTree statement) {
        if (!statement.is(Tree.Kind.BLOCK)) {
            reportIssue(statement, String.format(MESSAGE, getControlStructureName(statement)));
        }
    }

    /**
     * 获取控制结构的名称
     *
     * @param statement
     * @return 控制结构的名称
     */
    private String getControlStructureName(StatementTree statement) {
        Tree parent = statement.parent();
        if (parent.is(Tree.Kind.IF_STATEMENT)) {
            return "if";
        } else if (parent.is(Tree.Kind.FOR_STATEMENT)) {
            return "for";
        } else if (parent.is(Tree.Kind.WHILE_STATEMENT)) {
            return "while";
        } else if (parent.is(Tree.Kind.DO_STATEMENT)) {
            return "do-while";
        }
        return "unknown";
    }

    /**
     * 检查for语句的语句是否缺少大括号
     *
     * @param statement
     */
    private void checkForStatement(ForStatementTree statement) {
        checkSingleStatement(statement.statement());
    }

    /**
     * 检查while语句的语句是否缺少大括号
     *
     * @param statement
     */
    private void checkWhileStatement(WhileStatementTree statement) {
        checkSingleStatement(statement.statement());
    }

    /**
     * 检查do-while语句的语句是否缺少大括号
     *
     * @param statement
     */
    private void checkDoWhileStatement(DoWhileStatementTree statement) {
        checkSingleStatement(statement.statement());
    }
}