package com.elvis.sonar.java.checks.concurrent;

import com.elvis.sonar.java.checks.utils.ClassTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.VariableTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 避免Random实例被多线程使用，虽然共享该实例是线程安全的，但会因竞争同一seed，导致的性能下降。
 * @since 2024/9/29 15:46
 **/
@Rule(key = "AvoidConcurrentCompetitionRandomRule")
public class AvoidConcurrentCompetitionRandomRule extends IssuableSubscriptionVisitor {

    private static final String THREAD_PACKAGE_PATH = "java.lang.Thread";
    private static final String RUNNABLE_PACKAGE_PATH = "java.lang.Runnable";
    private static final String RANDOM_PACKAGE_PATH = "java.util.Random";
    private static final String MATH_PACKAGE_PATH = "java.lang.Math";
    private static final String MATCH_RANDOM_METHOD = "random";

    private static boolean extendsThreadOrImplementsRunnable;
    private static boolean runnableInMethod;

    static {
        extendsThreadOrImplementsRunnable = false;
        runnableInMethod = false;
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS)) {
            ClassTree classTree = (ClassTree) tree;
            /**
             * 判断类是否继承Thread或实现Runnable，以及是否在方法内有Runnable实例化
             * 因为Runnable的包装实现，也会进入到Class的判断分支，所以可以写在这里
             */
            extendsThreadOrImplementsRunnable = isExtendsThreadOrImplementsRunnable(classTree);
            runnableInMethod = isNewRunnableInMethod(classTree);

            //判断类的外部变量，是否有静态常量的Random，如果有则报告问题
            reportIfHaveStaticFinallyRandom(classTree);

        } else if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            //如果是多线程内使用Math.random()，则报告问题
            reportIfHaveMathRandom((MethodInvocationTree) tree);
        }
    }

    @Override
    public void leaveNode(Tree tree) {
        // 退出类抽象树时，重置判断标记
        if (tree.is(Tree.Kind.CLASS)) {
            extendsThreadOrImplementsRunnable = false;
            runnableInMethod = false;
        }
    }

    /**
     * 判断类的外部变量，是否有静态常量的Random，如果则有报告问题
     *
     * @param classTree
     * @return
     */
    private void reportIfHaveStaticFinallyRandom(ClassTree classTree) {
        if (!extendsThreadOrImplementsRunnable && !runnableInMethod) {
            return;
        }
        for (Tree member : classTree.members()) {
            if (member.is(Tree.Kind.VARIABLE)) {
                VariableTree variableTree = (VariableTree) member;
                if (VariableTreeCheckUtil.isFromClass(variableTree, RANDOM_PACKAGE_PATH)
                        && VariableTreeCheckUtil.isStaticAndFinal(variableTree)) {
                    reportIssue(variableTree, String.format("不要在多线程并发环境下使用同一个Random对象【%s】", variableTree.simpleName()));
                }
            }
        }
    }

    /**
     * 如果是多线程内使用Math.random()，则报告问题
     *
     * @param methodInvocationTree
     */
    private void reportIfHaveMathRandom(MethodInvocationTree methodInvocationTree) {
        if ((extendsThreadOrImplementsRunnable || runnableInMethod)
                && MethodInvocationTreeCheckUtil.isMethodCall(MATCH_RANDOM_METHOD, MATH_PACKAGE_PATH, methodInvocationTree)) {
            reportIssue(methodInvocationTree.methodSelect(), "【Math.random()】应避免在多线程并发环境下使用。");
        }
    }

    /**
     * 判断类抽象树是否继承Thread，或实现Runnable
     *
     * @param classTree
     * @return
     */
    private boolean isExtendsThreadOrImplementsRunnable(ClassTree classTree) {
        if (ClassTreeCheckUtil.isExtends(THREAD_PACKAGE_PATH, classTree)
                || ClassTreeCheckUtil.isImplements(RUNNABLE_PACKAGE_PATH, classTree)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否在方法内实例化了Runnable
     *
     * @param classTree
     * @return
     */
    private boolean isNewRunnableInMethod(ClassTree classTree) {
        Tree tree = classTree.parent();
        if (tree.is(Tree.Kind.NEW_CLASS)) {
            NewClassTree newClassTree = (NewClassTree) tree;
            if (RUNNABLE_PACKAGE_PATH.equals(newClassTree.identifier().symbolType().fullyQualifiedName())) {
                return true;
            }
        }
        return false;
    }

}