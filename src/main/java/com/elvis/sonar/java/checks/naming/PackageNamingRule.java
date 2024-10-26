package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.PackageDeclarationTree;
import org.sonar.plugins.java.api.tree.Tree;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * @author fengbingjian
 * @description 包名应全部为小写字母和数字组成
 * @since 2024/9/26 9:28
 **/
@Rule(key = "PackageNamingRule")
public class PackageNamingRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String FORMAT = "^[a-z0-9]+(\\.[a-z][a-z0-9]*)*$";

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
    public void visitCompilationUnit(CompilationUnitTree tree) {
        if (tree.packageDeclaration() != null) {
            String name = getPackageName(tree.packageDeclaration(), ".");
            if (!pattern.matcher(name).matches()) {
                context.reportIssue(this, tree.packageDeclaration().packageName(), String.format("包名【%s】应全部为小写字母和数字组成", name));
            }
        }
    }

    /**
     * 根据类的包声明语法树，获取包名称
     *
     * @param packageDeclarationTree
     * @param separator
     * @return
     */
    public static String getPackageName(@Nullable PackageDeclarationTree packageDeclarationTree, String separator) {
        if (packageDeclarationTree == null) {
            return "";
        } else {
            Deque<String> pieces = new LinkedList();

            ExpressionTree expr;
            MemberSelectExpressionTree mse;
            for (expr = packageDeclarationTree.packageName(); expr.is(new Tree.Kind[]{Tree.Kind.MEMBER_SELECT}); expr = mse.expression()) {
                mse = (MemberSelectExpressionTree) expr;
                pieces.push(mse.identifier().name());
                pieces.push(separator);
            }

            if (expr.is(new Tree.Kind[]{Tree.Kind.IDENTIFIER})) {
                IdentifierTree idt = (IdentifierTree) expr;
                pieces.push(idt.name());
            }

            StringBuilder sb = new StringBuilder();
            Iterator var5 = pieces.iterator();

            while (var5.hasNext()) {
                String piece = (String) var5.next();
                sb.append(piece);
            }

            return sb.toString();
        }
    }

}