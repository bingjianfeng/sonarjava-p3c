package com.elvis.sonar.java.checks.other;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

/**
 * @author fengbingjian
 * @description 浮点数之间的等值判断，基本数据类型不能用==来比较，包装数据类型不能用equals来判断。
 * @since 2024/9/29 15:46
 **/
@Rule(key = "AvoidDoubleOrFloatEqualCompareRule")
public class AvoidDoubleOrFloatEqualCompareRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String MESSAGE = "浮点数之间的等值判断，基本数据类型不能用==来比较，包装数据类型不能用equals来判断";
    private JavaFileScannerContext context;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitBinaryExpression(BinaryExpressionTree tree) {
        ExpressionTree leftOperand = tree.leftOperand();
        ExpressionTree rightOperand = tree.rightOperand();

        // 检查是否是 == 或 != 比较
        if (tree.is(Tree.Kind.EQUAL_TO, Tree.Kind.NOT_EQUAL_TO)) {
            checkFloatingPointComparison(tree, leftOperand, rightOperand);
        }

        super.visitBinaryExpression(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        Symbol methodSymbol = tree.symbol();
        if (methodSymbol != null && "equals".equals(methodSymbol.name())) {
            ExpressionTree methodSelect = tree.methodSelect();
            if (methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
                ExpressionTree receiver = ((org.sonar.plugins.java.api.tree.MemberSelectExpressionTree) methodSelect).expression();
                if (isFloatingPointType(receiver)) {
                    context.reportIssue(this, tree, MESSAGE);
                }
            }
        }

        super.visitMethodInvocation(tree);
    }

    private void checkFloatingPointComparison(BinaryExpressionTree tree, ExpressionTree left, ExpressionTree right) {
        if (isFloatingPointType(left) && isFloatingPointType(right)) {
            context.reportIssue(this, tree, MESSAGE);
        }
    }

    private boolean isFloatingPointType(ExpressionTree expression) {
        return isFloatingPointType(expression.symbolType());
    }

    private boolean isFloatingPointType(Type type) {
        if (type == null) {
            return false;
        }
        return "float".equals(type.fullyQualifiedName()) || "double".equals(type.fullyQualifiedName())
                || "java.lang.Float".equals(type.fullyQualifiedName()) || "java.lang.Double".equals(type.fullyQualifiedName());
    }
}