package com.elvis.sonar.java.checks.naming;

import com.elvis.sonar.java.log.utils.LogUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.regex.Pattern;

/**
 * @author fengbingjian
 * @description 抽象类命名应以Abstract或Base开头
 * @since 2024/9/26 9:28
 **/
@Rule(key = "AbstractClassShouldStartWithAbstractNamingRule")
public class AbstractClassShouldStartWithAbstractNamingRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String FORMAT = "^(Abstract|Base)[A-Z][a-zA-Z0-9]*$";

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
        LogUtil.message("开始进行抽象类扫描");
        LogUtil.message("完整包路径：%s", "test");
        IdentifierTree simpleName = tree.simpleName();
        if (tree.is(Tree.Kind.CLASS) && simpleName != null) {
            if (isAbstract(tree) && !pattern.matcher(simpleName.name()).matches()) {
                context.reportIssue(this, simpleName, String.format("%s抽象类命名不规范，应以Abstract或Base开头", simpleName.name()));
            }
        }
        super.visitClass(tree);
    }

    private static boolean isAbstract(ClassTree tree) {
        if (tree == null || tree.modifiers() == null) {
            return false;
        }
        for (ModifierKeywordTree keywordTree : tree.modifiers().modifiers()) {
            if (Modifier.ABSTRACT == keywordTree.modifier()) {
                return true;
            }
        }
        return false;
    }

}