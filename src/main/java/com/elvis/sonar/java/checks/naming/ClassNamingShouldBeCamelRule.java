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
 * @description 类命名不符合UpperCamelCase命名风格
 * @since 2024/9/26 17:35
 **/
@Rule(key = "ClassNamingShouldBeCamelRule")
public class ClassNamingShouldBeCamelRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String FORMAT = "^I?([A-Z][a-z0-9]+)+(([A-Z])|(DO|DTO|VO|DAO|BO|DAOImpl|YunOS|AO|PO))?$";
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
            context.reportIssue(this, tree.simpleName(), String.format("【%s】不符合UpperCamelCase命名风格", tree.simpleName().name()));
        }
        super.visitClass(tree);
    }
}