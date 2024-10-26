package com.elvis.sonar.java.checks.utils;

import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

/**
 * 变量抽象树检查工具类
 *
 * @author fengbingjian
 * @description
 * @since 2024/9/29 13:09
 **/
public class VariableTreeCheckUtil {

    /**
     * 判断是否为静态常量
     *
     * @param tree
     * @return
     */
    public static boolean isStaticAndFinal(VariableTree tree) {
        return isStatic(tree) && isFinal(tree);
    }

    /**
     * 判断是否为静态变量
     *
     * @param tree
     * @return
     */
    public static boolean isStatic(VariableTree tree) {
        for (ModifierKeywordTree modifierKeywordTree : tree.modifiers().modifiers()) {
            Modifier modifier = modifierKeywordTree.modifier();
            if (modifier == Modifier.STATIC) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为常量
     *
     * @param tree
     * @return
     */
    public static boolean isFinal(VariableTree tree) {
        for (ModifierKeywordTree modifierKeywordTree : tree.modifiers().modifiers()) {
            Modifier modifier = modifierKeywordTree.modifier();
            if (modifier == Modifier.FINAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否属于某个类
     *
     * @param variableTree
     * @param classPath
     * @return
     */
    public static boolean isFromClass(VariableTree variableTree, String classPath) {
        if (classPath.equals(variableTree.type().symbolType().fullyQualifiedName())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否类属性
     *
     * @param variableTree
     * @return
     */
    public static boolean isClassAttribute(VariableTree variableTree) {
        if (TreeUtil.findParent(variableTree, Tree.Kind.METHOD)) {
            return false;
        }
        return true;
    }

}