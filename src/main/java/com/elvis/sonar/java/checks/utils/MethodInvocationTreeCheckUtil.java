package com.elvis.sonar.java.checks.utils;

import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;

/**
 * 方法抽象树检查工具类
 *
 * @author fengbingjian
 * @description
 * @since 2024/9/29 13:09
 **/
public class MethodInvocationTreeCheckUtil {

    /**
     * 判断是否调用了某个类
     *
     * @param packageName 类的完整包路径
     * @param tree        方法抽象树
     * @return
     */
    public static boolean isClassCall(String packageName, MethodInvocationTree tree) {
        if (!tree.methodSelect().is(Tree.Kind.MEMBER_SELECT)) {
            return false;
        }
        MemberSelectExpressionTree mseTree = (MemberSelectExpressionTree) tree.methodSelect();
        if (mseTree.expression().is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree idt = (IdentifierTree) mseTree.expression();
            return idt.symbolType().is(packageName);
        }
        return false;
    }

    /**
     * 判断是否调用了某个方法
     *
     * @param methodName  调用的方法名称
     * @param packageName 类的完整包路径
     * @param tree        方法抽象树
     * @return
     */
    public static boolean isMethodCall(String methodName, String packageName, MethodInvocationTree tree) {
        if (!tree.methodSelect().is(Tree.Kind.MEMBER_SELECT)) {
            return false;
        }
        MemberSelectExpressionTree mseTree = (MemberSelectExpressionTree) tree.methodSelect();
        if (mseTree.expression().is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree idt = (IdentifierTree) mseTree.expression();
            return idt.symbolType().is(packageName) && methodName.equals(mseTree.identifier().name());
        }
        return false;
    }

    /**
     * 判断是否调用了某个方法
     *
     * @param methodName 调用的方法名称
     * @param tree       方法抽象树
     * @return
     */
    public static boolean isMethodCall(List<String> methodName, MethodInvocationTree tree) {
        return methodName.contains(getMethodName(tree));
    }

    /**
     * 判断是否调用了某个方法
     *
     * @param methodName 调用的方法名称
     * @param tree       方法抽象树
     * @return
     */
    public static boolean isMethodCall(String methodName, MethodInvocationTree tree) {
        return methodName.equals(getMethodName(tree));
    }

    /**
     * 获取方法抽象树的方法名称
     *
     * @param tree 方法抽象树
     * @return
     */
    public static String getMethodName(MethodInvocationTree tree) {
        if (tree.methodSelect().is(Tree.Kind.IDENTIFIER)) {
            return ((IdentifierTree)tree.methodSelect()).name();
        }
        if (!tree.methodSelect().is(Tree.Kind.MEMBER_SELECT)) {
            return null;
        }
        MemberSelectExpressionTree mseTree = (MemberSelectExpressionTree) tree.methodSelect();
        if (mseTree.expression().is(Tree.Kind.IDENTIFIER) || mseTree.identifier().is(Tree.Kind.IDENTIFIER)) {
            return mseTree.identifier().name();
        }
        return null;
    }

    /**
     * 判断是否调用了实现了某个接口的方法
     *
     * @param methodName           调用的方法名称
     * @param interfacePackageName 实现接口的完整包路径
     * @param tree                 方法抽象树
     * @return
     */
    public static boolean isMethodCallWithInterfaceImplement(String methodName, String interfacePackageName, MethodInvocationTree tree) {
        if (!isMethodCall(methodName, tree)) {
            return false;
        }
        Tree methodReceiver = tree.methodSelect();
        if (!methodReceiver.is(Tree.Kind.MEMBER_SELECT)) {
            return false;
        }
        MemberSelectExpressionTree memberSelectTree = (MemberSelectExpressionTree) methodReceiver;
        if (interfacePackageName.equals(memberSelectTree.expression().symbolType().fullyQualifiedName())) {
            return true;
        }
        return false;
    }

    /**
     * 判断方法抽象树内的变量是否静态变量
     *
     * @param tree
     * @return
     */
    public static boolean isStatic(MethodInvocationTree tree) {
        if (!tree.methodSelect().is(Tree.Kind.MEMBER_SELECT)) {
            return false;
        }
        MemberSelectExpressionTree mseTree = (MemberSelectExpressionTree) tree.methodSelect();
        if (mseTree.expression().is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree idt = (IdentifierTree) mseTree.expression();
            // 获取与标识符关联的符号
            Symbol symbol = idt.symbol();
            // 检查符号是否为变量符号，并且是静态的
            return symbol != null && symbol.isVariableSymbol() && symbol.isStatic();
        }
        return false;
    }

    /**
     * 获取方法的名称
     *
     * @param tree 方法抽象树
     * @return
     */
    public static String getName(MethodInvocationTree tree) {
        if (!tree.methodSelect().is(Tree.Kind.MEMBER_SELECT)) {
            return null;
        }
        MemberSelectExpressionTree mseTree = (MemberSelectExpressionTree) tree.methodSelect();
        if (mseTree.expression().is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree idt = (IdentifierTree) mseTree.expression();
            return idt.name();
        }
        return null;
    }

    /**
     * 获取方法调用的接收者
     *
     * @param tree
     * @return
     */
    public static ExpressionTree getReceiver(MethodInvocationTree tree) {
        ExpressionTree methodSelect = tree.methodSelect();
        if (methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
            return memberSelect.expression();
        } else if (methodSelect.is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree identifier = (IdentifierTree) methodSelect;
            return identifier;
        }
        return null;
    }
}