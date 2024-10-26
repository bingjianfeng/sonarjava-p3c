package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description 布尔字段不要加is前缀
 * @since 2024/9/26 17:35
 **/
@Rule(key = "BooleanPropertyShouldNotStartWithIsRule")
public class BooleanPropertyShouldNotStartWithIsRule extends IssuableSubscriptionVisitor {

    private static final String IS = "is";
    private static final String BOOLEAN = "boolean";

    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        IdentifierTree simpleName = variableTree.simpleName();
        TypeTree type = variableTree.type();
        // 如果不是布尔类型，则中止
        if (!type.symbolType().name().toLowerCase().equals(BOOLEAN)) {
            return;
        }
        if (simpleName.name().toLowerCase().indexOf(IS) == 0) {
            reportIssue(simpleName, String.format("【%s】布尔字段不要加is前缀", simpleName.name()));
        }
    }
}