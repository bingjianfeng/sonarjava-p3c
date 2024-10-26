package com.elvis.sonar.java.checks.naming;

import com.elvis.sonar.java.checks.utils.VariableTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.regex.Pattern;

/**
 * @author fengbingjian
 * @description 常量命名应全部大写并以下划线分隔
 * @since 2024/9/26 9:28
 **/
@Rule(key = "ConstantFieldShouldBeUpperCaseRule")
public class ConstantFieldShouldBeUpperCaseRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String FORMAT = "^[A-Z][A-Z0-9_]*$";

    private Pattern pattern = null;
    private JavaFileScannerContext context;

    @Override
    public void scanFile(final JavaFileScannerContext context) {
        if (pattern == null) {
            pattern = Pattern.compile(FORMAT, Pattern.DOTALL);
        }
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitClass(ClassTree tree) {
        if (tree.is(Tree.Kind.CLASS) || tree.is(Tree.Kind.ENUM)) {
            for (Tree member : tree.members()) {
                if(!member.is(Tree.Kind.VARIABLE)){
                    continue;
                }
                VariableTree variableTree = (VariableTree) member;
                IdentifierTree simpleName = variableTree.simpleName();
                if (member.is(Tree.Kind.VARIABLE) && VariableTreeCheckUtil.isStaticAndFinal(variableTree) && !pattern.matcher(simpleName.name()).matches()) {
                    context.reportIssue(this, simpleName, String.format("常量【%s】命名应全部大写并以下划线分隔", simpleName.name()));
                }
            }
        }
        super.visitClass(tree);
    }

}