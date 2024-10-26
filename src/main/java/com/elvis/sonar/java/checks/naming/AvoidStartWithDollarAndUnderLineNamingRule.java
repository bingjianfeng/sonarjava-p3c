package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author fengbingjian
 * @description 变量命名不能以_或$开始
 * @since 2024/9/26 17:35
 **/
@Rule(key = "AvoidStartWithDollarAndUnderLineNamingRule")
public class AvoidStartWithDollarAndUnderLineNamingRule extends IssuableSubscriptionVisitor {

    private static final String FORMAT = "^[^$_].*";
    private Pattern pattern = null;

    @Override
    public void setContext(JavaFileScannerContext context) {
        if (pattern == null) {
            pattern = Pattern.compile(FORMAT, Pattern.DOTALL);
        }
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
        if (!pattern.matcher(simpleName.name()).matches()) {
            reportIssue(simpleName, String.format("【%s】变量命名不能以_或$开始", simpleName.name()));
        }
    }
}