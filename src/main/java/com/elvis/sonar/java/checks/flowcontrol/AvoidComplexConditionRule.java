package com.elvis.sonar.java.checks.flowcontrol;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.ConditionalExpressionTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 请不要在条件中使用复杂的表达式
 * @since 2024/9/29 22:04
 */
@Rule(key = "AvoidComplexConditionRule")
public class AvoidComplexConditionRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "请不要在条件中使用复杂的表达式";

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.IF_STATEMENT, Kind.CONDITIONAL_EXPRESSION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.IF_STATEMENT)) {
            IfStatementTree ifStatement = (IfStatementTree) tree;
            checkCondition(tree, ifStatement.condition());
        } else if (tree.is(Kind.CONDITIONAL_EXPRESSION)) {
            ConditionalExpressionTree conditionalExpression = (ConditionalExpressionTree) tree;
            checkCondition(tree, conditionalExpression.condition());
        }
    }

    private void checkCondition(Tree tree, ExpressionTree condition) {
        int complexity = countLogicalOperators(condition);
        if (complexity > 1) {
            reportIssue(tree, MESSAGE);
        }
    }

    private int countLogicalOperators(ExpressionTree expression) {
        if (expression == null) {
            return 0;
        }

        int count = 0;
        if (expression.is(Kind.CONDITIONAL_AND, Kind.CONDITIONAL_OR)) {
            count++;
        }

        // Recursively count the logical operators in the left and right sub-expressions
        if (expression.is(Kind.CONDITIONAL_AND, Kind.CONDITIONAL_OR)) {
            BinaryExpressionTree andExpr = (BinaryExpressionTree) expression;
            count += countLogicalOperators(andExpr.leftOperand());
            count += countLogicalOperators(andExpr.rightOperand());
        } else if (expression.is(Kind.CONDITIONAL_AND)) {
            BinaryExpressionTree orExpr = (BinaryExpressionTree) expression;
            count += countLogicalOperators(orExpr.leftOperand());
            count += countLogicalOperators(orExpr.rightOperand());
        }

        return count;
    }
}