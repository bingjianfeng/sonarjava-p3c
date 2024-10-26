package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TypeTree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description Dao或Service接口的实现，必须以Impl结尾
 * @since 2024/9/26 9:28
 **/
@Rule(key = "ServiceOrDaoClassShouldEndWithImplRule")
public class ServiceOrDaoClassShouldEndWithImplRule extends IssuableSubscriptionVisitor {

    private static final MethodMatchers CLONE_MATCHER = MethodMatchers.create()
            .ofAnyType()
            .names("clone")
            .addWithoutParametersMatcher()
            .build();

    @Override
    public List<Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;
        Symbol.TypeSymbol classSymbol = classTree.symbol();
        if (interfaceNameEndOfDaoOrService(classTree) && !classSymbol.name().endsWith("Impl")) {
            context.reportIssue(this, classTree.simpleName(), String.format("【%s】是Dao或Service接口的实现，类名应以Impl结尾", classSymbol.name()));
        }
    }

    private static boolean interfaceNameEndOfDaoOrService(ClassTree classTree) {
        return classTree.superInterfaces().stream().map(TypeTree::symbolType).anyMatch(t -> t.name().toLowerCase().endsWith("dao") || t.name().toLowerCase().endsWith("service"));
    }
}