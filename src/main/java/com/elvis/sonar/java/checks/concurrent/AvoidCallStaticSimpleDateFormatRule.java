package com.elvis.sonar.java.checks.concurrent;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.TreeUtil;
import com.elvis.sonar.java.utils.CustomModifiersUtils;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fengbingjian
 * @description SimpleDateFormat 是线程不安全的类，一般不要定义为static变量，如果定义为static，必须加锁，或者使用DateUtils工具类。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "AvoidCallStaticSimpleDateFormatRule")
public class AvoidCallStaticSimpleDateFormatRule extends IssuableSubscriptionVisitor {

    private static final String SDF_PACKAGE_PATH = "java.text.SimpleDateFormat";
    private static final String MESSAGE = "SimpleDateFormat可能导致线程安全问题";
    private Deque<Boolean> withinSynchronizedBlock = new LinkedList<>();


    @Override
    public void setContext(JavaFileScannerContext context) {
        withinSynchronizedBlock.push(false);
        super.setContext(context);
    }

    /**
     * 如果退出扫描的文件，则清空双端队列
     *
     * @param context
     */
    @Override
    public void leaveFile(JavaFileScannerContext context) {
        withinSynchronizedBlock.clear();
    }


    /**
     * 指定抽象树的扫描范围
     *
     * @return
     */
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(
                Tree.Kind.METHOD_INVOCATION,
                Tree.Kind.SYNCHRONIZED_STATEMENT,
                Tree.Kind.NEW_CLASS,
                Tree.Kind.METHOD);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            // 进入类的方法调用扫描
            MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
            super.visitNode(tree);
            // 检查方法调用是否是 SimpleDateFormat 的实例
            if (MethodInvocationTreeCheckUtil.isClassCall(SDF_PACKAGE_PATH, methodInvocationTree)) {
                // 如果父节点不是同步语句，且不是静态变量，则报告问题
                if (MethodInvocationTreeCheckUtil.isStatic(methodInvocationTree) && !isInSyncBlock()) {
                    context.reportIssue(this, methodInvocationTree, MESSAGE);
                }
            }
        } else if (tree.is(Tree.Kind.NEW_CLASS) && TreeUtil.findParent(tree,Tree.Kind.METHOD)) {
            // 如果是方法内的类的实例化，才进入此判断节点
            super.visitNode(tree);
            NewClassTree newClassTree = (NewClassTree) tree;
            if (newClassTree.identifier().symbolType().is(SDF_PACKAGE_PATH)) {
                if (newClassTree.arguments() != null && newClassTree.arguments().size() > 0) {
                    ExpressionTree argument = newClassTree.arguments().get(0);
                    if (argument.is(Tree.Kind.IDENTIFIER)) {
                        IdentifierTree identifier = (IdentifierTree) argument;
                        Symbol symbol = identifier.symbol();
                        if (!isStaticFinalVariable(symbol)) {
                            context.reportIssue(this, newClassTree, MESSAGE);
                        }
                    } else if (argument.is(Tree.Kind.STRING_LITERAL)) {
                        context.reportIssue(this, newClassTree, MESSAGE);
                    }
                }
            }
        } else if (tree.is(Tree.Kind.METHOD)) {
            // 如果进入方法体，检查方法是否已标记线程安全，如果是，则推送true
            withinSynchronizedBlock.push(CustomModifiersUtils.hasModifier(((MethodTree) tree).modifiers(), Modifier.SYNCHRONIZED));
            super.visitNode(tree);
        } else if (tree.is(Tree.Kind.SYNCHRONIZED_STATEMENT)) {
            // 如果进入线程安全包块，推送true
            withinSynchronizedBlock.push(true);
        }
    }

    /**
     * 如果退出抽象树节点，则移除双端队列顶部元素
     *
     * @param tree
     */
    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD, Tree.Kind.SYNCHRONIZED_STATEMENT, Tree.Kind.LAMBDA_EXPRESSION)) {
            withinSynchronizedBlock.pop();
        }
    }

    /**
     * 判断是否在线程安全的方法内
     *
     * @return
     */
    public Boolean isInSyncBlock() {
        return withinSynchronizedBlock.peek();
    }

    /**
     * 判断变量是否静态常量
     *
     * @param symbol
     * @return
     */
    private boolean isStaticFinalVariable(Symbol symbol) {
        return symbol.isVariableSymbol() && symbol.isStatic() && symbol.isFinal();
    }

}