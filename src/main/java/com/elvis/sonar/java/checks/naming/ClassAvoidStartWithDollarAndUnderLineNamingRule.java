package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.regex.Pattern;

/**
 * @author fengbingjian
 * @description 类命名不能以_或$开始
 * @since 2024/9/26 17:35
 **/
@Rule(key = "ClassAvoidStartWithDollarAndUnderLineNamingRule")
public class ClassAvoidStartWithDollarAndUnderLineNamingRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String FORMAT = "^[^$_].*";
    private Pattern pattern = null;
    private JavaFileScannerContext context;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        if (pattern == null) {
            pattern = Pattern.compile(FORMAT, Pattern.DOTALL);
        }
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitClass(ClassTree tree) {
        if (tree.is(Tree.Kind.CLASS) && tree.simpleName() != null && !pattern.matcher(tree.simpleName().name()).matches()) {
            context.reportIssue(this, tree.simpleName(), String.format("【%s】类命名不能以_或$开始", tree.simpleName().name()));
        }
        super.visitClass(tree);
    }
}