package com.elvis.sonar.java.checks.utils;

import org.sonar.plugins.java.api.tree.Tree;

/**
 * 抽象树检查工具类
 *
 * @author fengbingjian
 * @description
 * @since 2024/9/29 13:09
 **/
public class TreeUtil {

    /**
     * 判断抽象树内的父级，是否命中树类型
     *
     * @param tree
     * @param kinds
     * @return
     */
    public static boolean findParent(Tree tree, Tree.Kind... kinds) {
        Tree parent = tree.parent();
        while (parent != null) {
            if (parent.is(kinds)) {
                return true;
            }
            parent = parent.parent();
        }
        return false;
    }

    /**
     * 判断抽象树内的父级，是否命中树类型
     *
     * @param tree
     * @param kind
     * @return
     */
    public static Tree getParent(Tree tree, Tree.Kind kind) {
        Tree parent = tree.parent();
        while (parent != null) {
            if (parent.is(kind)) {
                return parent;
            }
            parent = parent.parent();
        }
        return null;
    }
}