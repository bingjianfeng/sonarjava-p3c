package com.elvis.sonar.java.checks.other;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description 避免用Apache Beanutils进行属性的copy
 * @since 2024/9/29 15:46
 **/
@Rule(key = "AvoidApacheBeanUtilsCopyRule")
public class AvoidApacheBeanUtilsCopyRule extends IssuableSubscriptionVisitor {

    private static final String BEAN_UTILS_PACKAGE_PATH = "org.apache.commons.beanutils.BeanUtils";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
        Symbol methodSymbol = methodInvocation.symbol();
        if (methodSymbol != null) {
            Type ownerType = methodSymbol.owner().type();
            if (BEAN_UTILS_PACKAGE_PATH.equals(ownerType.fullyQualifiedName())) {
                reportIssue(methodInvocation, "避免用Apache Beanutils进行属性的copy。");
            }
        }
    }
}