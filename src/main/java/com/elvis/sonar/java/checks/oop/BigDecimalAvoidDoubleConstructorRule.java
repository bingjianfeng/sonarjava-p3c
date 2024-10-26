package com.elvis.sonar.java.checks.oop;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 禁止使用构造方法BigDecimal(double)的方式把double值转化为BigDecimal对象
 * @since 2024/10/08 16:30
 */
@Rule(key = "BigDecimalAvoidDoubleConstructorRule")
public class BigDecimalAvoidDoubleConstructorRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "使用了new BigDecimal(double)构造函数";
    private static final String DOUBLE_PACKAGE_NAME = "java.lang.Double";
    private static final String BIGDECIMAL_PACKAGE_NAME = "java.math.BigDecimal";
    private static final String DOUBLE_STR = "double";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.NEW_CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.NEW_CLASS)) {
            NewClassTree newClass = (NewClassTree) tree;
            checkBigDecimalConstructor(newClass);
        }
    }

    /**
     * 检查是否使用了BigDecimal(double)构造函数
     *
     * @param newClass
     */
    private void checkBigDecimalConstructor(NewClassTree newClass) {
        if (!BIGDECIMAL_PACKAGE_NAME.equals(newClass.identifier().symbolType().fullyQualifiedName())) {
            return;
        }
        // 检查构造函数参数
        List<ExpressionTree> arguments = newClass.arguments();
        if (arguments.size() != 1) {
            return;
        }
        ExpressionTree argument = arguments.get(0);
        if (argument.is(Tree.Kind.DOUBLE_LITERAL) || isDoubleVariable(argument)) {
            reportIssue(newClass, MESSAGE);
        }
    }

    /**
     * 检查表达式是否是double类型的变量
     *
     * @param expression
     * @return
     */
    private boolean isDoubleVariable(ExpressionTree expression) {
        if (!expression.is(Tree.Kind.IDENTIFIER)) {
            return false;
        }
        IdentifierTree identifier = (IdentifierTree) expression;
        Symbol symbol = identifier.symbol();
        if (symbol != null
                && (DOUBLE_PACKAGE_NAME.equals(symbol.type().fullyQualifiedName())
                || DOUBLE_STR.equals(symbol.type().fullyQualifiedName()))) {
            return true;
        }
        return false;
    }

}