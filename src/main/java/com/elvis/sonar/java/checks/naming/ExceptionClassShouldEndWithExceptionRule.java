package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description 异常类命名应以Exception结尾
 * @since 2024/9/26 9:28
 **/
@Rule(key = "ExceptionClassShouldEndWithExceptionRule")
public class ExceptionClassShouldEndWithExceptionRule extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;
        Symbol.TypeSymbol symbol = classTree.symbol();
        IdentifierTree className = classTree.simpleName();
        if (isSubtypeOfException(symbol) && !endsWithException(className.name())) {
            context.reportIssue(this, className, String.format("【%s】命名应以Exception结尾", className.name()));
        }
    }

    /**
     * 检查类名称是否以exception结尾
     *
     * @param className
     * @return
     */
    private static boolean endsWithException(String className) {
        return className.toLowerCase().endsWith("exception");
    }

    /**
     * 检查是否继承异常类
     *
     * @param symbol
     * @return
     */
    private static boolean isSubtypeOfException(Symbol symbol) {
        return symbol.type().isSubtypeOf("java.lang.Exception");
    }

}