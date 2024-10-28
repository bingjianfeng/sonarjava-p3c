package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 测试类命名应以Test结尾
 * @since 2024/9/26 9:28
 **/
@Rule(key = "TestClassShouldEndWithTestNamingRule")
public class TestClassShouldEndWithTestNamingRule extends IssuableSubscriptionVisitor {

    public static final String TEST_ANNO = "Test";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;
        Symbol.TypeSymbol classSymbol = classTree.symbol();
        Boolean hasJUnitTestMethods = hasJUnitTestMethods(classTree);
        if (hasJUnitTestMethods && !classSymbol.name().endsWith("Test")) {
            context.reportIssue(this, classTree.simpleName(), String.format("测试类【%s】命名应以Test结尾", classSymbol.name()));
        }
    }

    private boolean hasJUnitTestMethods(ClassTree classTree) {
        for (Tree tree : classTree.members()) {
            if (!tree.is(Tree.Kind.METHOD)) {
                continue;
            }
            MethodTree methodTree = (MethodTree) tree;
            if (methodTree.symbol() == null || methodTree.symbol().metadata() == null) {
                continue;
            }
            SymbolMetadata metadata = methodTree.symbol().metadata();
            if (metadata.annotations() == null || metadata.annotations().size() == 0) {
                continue;
            }
            for (SymbolMetadata.AnnotationInstance annotation : metadata.annotations()) {
                if (TEST_ANNO.equals(annotation.symbol().name())) {
                    return true;
                }
            }
        }
        return false;
    }
}