package com.elvis.sonar.java.checks.oop;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 应使用equals方法代替==
 * @since 2024/10/08 16:30
 */
@Rule(key = "WrapperTypeEqualityRule")
public class WrapperTypeEqualityRule extends IssuableSubscriptionVisitor {

    private static final List<String> EQUALS_TYPE_LIST = Arrays.asList("java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double",
            "java.lang.Boolean", "java.lang.Character", "java.lang.String");

    private static final String MESSAGE = "【%s】应该作为equals的参数，而不是调用方";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问相等性表达式节点
        return Arrays.asList(Tree.Kind.EQUAL_TO, Tree.Kind.NOT_EQUAL_TO);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.EQUAL_TO, Tree.Kind.NOT_EQUAL_TO)) {
            BinaryExpressionTree binaryExpression = (BinaryExpressionTree) tree;
            ExpressionTree leftOperand = binaryExpression.leftOperand();
            ExpressionTree rightOperand = binaryExpression.rightOperand();
            // 检查是否为包装类型
            if (isWrapperType(leftOperand) && isWrapperType(rightOperand)) {
                reportIssue(binaryExpression,String.format(MESSAGE,binaryExpression.leftOperand().toString()));
            }
        }
    }

    /**
     * 判断是否为包装类型
     * @param expression
     * @return
     */
    private boolean isWrapperType(ExpressionTree expression) {
        // 检查是否为包装类型的字面量
        if (expression.is(Tree.Kind.INT_LITERAL, Tree.Kind.LONG_LITERAL, Tree.Kind.FLOAT_LITERAL, Tree.Kind.DOUBLE_LITERAL,
                Tree.Kind.BOOLEAN_LITERAL, Tree.Kind.CHAR_LITERAL, Tree.Kind.STRING_LITERAL)) {
            return true;
        }

        // 检查是否为包装类型的变量或表达式
        if (expression.is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree identifier = (IdentifierTree) expression;
            Symbol symbol = identifier.symbol();
            if (EQUALS_TYPE_LIST.contains(symbol.type().fullyQualifiedName())) {
                return true;
            }
        }

        // 检查是否为包装类型的静态字段
        if (expression.is(Tree.Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) expression;
            Symbol symbol = memberSelect.identifier().symbol();
            if (EQUALS_TYPE_LIST.contains(symbol.type().name())) {
                return true;
            }
        }

        return false;
    }
}