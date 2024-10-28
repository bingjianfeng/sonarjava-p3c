package com.elvis.sonar.java.checks.oop;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.utils.StringUtils;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description Object的equals方法容易抛空指针异常，应使用常量或确定有值的对象来调用equals。
 * @since 2024/10/08 16:30
 */
@Rule(key = "EqualsAvoidNullRule")
public class EqualsAvoidNullRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】应该作为equals的参数，而不是调用方";
    private static final String METHOD_EQUALS = "equals";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问方法调用表达式节点
        return Arrays.asList(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            if (METHOD_EQUALS.equals(MethodInvocationTreeCheckUtil.getMethodName(methodInvocation))) {
                checkEqualsInvocation(methodInvocation);
            }
        }
    }

    private void checkEqualsInvocation(MethodInvocationTree methodInvocation) {
        ExpressionTree receiver = MethodInvocationTreeCheckUtil.getReceiver(methodInvocation);
        // 如果参数是字面量（如字符串字面量），则不需要进一步检查
        if (isLiteral(receiver)) {
            return;
        }

        // 检查参数是否为常量或确定不为null的对象
        if (!isConstantOrNonNullObject(receiver)) {
            String methodName = MethodInvocationTreeCheckUtil.getName(methodInvocation);
            if(StringUtils.isBlankOrNull(methodName)){
                methodName = "Object";
            }
            reportIssue(receiver, String.format(MESSAGE, methodName));
        }
    }

    private boolean isLiteral(ExpressionTree expression) {
        return expression.is(Tree.Kind.STRING_LITERAL, Tree.Kind.CHAR_LITERAL, Tree.Kind.INT_LITERAL,
                Tree.Kind.LONG_LITERAL, Tree.Kind.FLOAT_LITERAL, Tree.Kind.DOUBLE_LITERAL, Tree.Kind.BOOLEAN_LITERAL);
    }

    private boolean isConstantOrNonNullObject(ExpressionTree expression) {
        // 检查是否为字面量
        if (isLiteral(expression)) {
            return true;
        }

        // 检查是否为final变量
        if (expression.is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree identifier = (IdentifierTree) expression;
            Symbol symbol = identifier.symbol();
            if (symbol.isVariableSymbol() && symbol.isFinal()) {
                return true;
            }
        }

        // 检查是否为静态final字段
        if (expression.is(Tree.Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) expression;
            Symbol symbol = memberSelect.identifier().symbol();
            if (symbol.isVariableSymbol() && symbol.isFinal() && symbol.isStatic()) {
                return true;
            }
        }

        return false;
    }
}