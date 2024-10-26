package com.elvis.sonar.java.checks.concurrent;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 不要显式创建线程，请使用线程池。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "AvoidManuallyCreateThreadRule")
public class AvoidManuallyCreateThreadRule extends IssuableSubscriptionVisitor {

    private static final String THREAD_PACKAGE_PATH = "java.lang.Thread";
    private static final String RUNNABLE_PACKAGE_PATH = "java.lang.Runnable";
    private static final String MESSAGE = "不要显式创建线程，请使用线程池。";


    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }


    /**
     * 指定抽象树的扫描范围
     *
     * @return
     */
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(
                Tree.Kind.NEW_CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.NEW_CLASS)) {
            super.visitNode(tree);
            NewClassTree newClassTree = (NewClassTree) tree;
            // 如果是Thread的实例化，且参数不是实现Runnable，则报告问题
            if (newClassTree.identifier().symbolType().is(THREAD_PACKAGE_PATH) && !argumentsIsImplementsRunnable(newClassTree)) {
                context.reportIssue(this, newClassTree, MESSAGE);
            }
        }
    }

    /**
     * 查看类参数是否实现了Runnable
     *
     * @param classTree
     * @return
     */
    private boolean argumentsIsImplementsRunnable(NewClassTree classTree) {
        Arguments arguments = classTree.arguments();
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        Type firstArgumentType = arguments.get(0).symbolType();
        if (firstArgumentType != null && firstArgumentType.isSubtypeOf(RUNNABLE_PACKAGE_PATH)) {
            return true;
        }
        return false;
    }
}