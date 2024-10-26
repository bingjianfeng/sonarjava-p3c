package com.elvis.sonar.java.checks.other;

import com.elvis.sonar.java.checks.utils.VariableTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author fengbingjian
 * @description Pattern变量应定义为常量或者字段
 * @since 2024/9/29 15:46
 **/
@Rule(key = "AvoidPatternCompileInMethodRule")
public class AvoidPatternCompileInMethodRule extends IssuableSubscriptionVisitor {

    private static final String PATTERN_PACKAGE_PATH = "java.util.regex.Pattern";
    private static final String COMPILE_METHOD_NAME = "compile";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        TypeTree type = variableTree.type();
        if (PATTERN_PACKAGE_PATH.equals(type.symbolType().fullyQualifiedName())
                && !VariableTreeCheckUtil.isStatic(variableTree)) {
            ExpressionTree initializer = variableTree.initializer();
            if (initializer != null && isPatternCompileCall(initializer)) {
                reportIssue(variableTree.simpleName(), String.format("【%s】变量应定义为常量或者字段", variableTree.simpleName().name()));
            }
        }
    }

    /**
     * 判斷是否調用了compile方法
     *
     * @param expression
     * @return
     */
    private boolean isPatternCompileCall(ExpressionTree expression) {
        if (expression.is(Tree.Kind.METHOD_INVOCATION)) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) expression;
            Symbol methodSymbol = methodInvocation.symbol();
            if (methodSymbol != null) {
                return COMPILE_METHOD_NAME.equals(methodSymbol.name());
            }
        }
        return false;
    }
}