package com.elvis.sonar.java.checks.utils;

import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 类抽象树检查工具类
 *
 * @author fengbingjian
 * @description
 * @since 2024/9/29 13:09
 **/
public class ClassTreeCheckUtil {

    private static final List<String> POJO_NAMES = Arrays.asList("pojo", "entity", "vo", "do", "dto");

    /**
     * 类抽象树是否继承与某个类
     *
     * @param packageName
     * @param classTree
     * @return
     */
    public static boolean isExtends(String packageName, ClassTree classTree) {
        TypeTree superClass = classTree.superClass();
        if (superClass == null) {
            return false;
        }
        if (superClass.symbolType() != null && superClass.symbolType().is(packageName)) {
            return true;
        }
        return false;
    }

    /**
     * 类抽象树是否实现某个接口
     *
     * @param packageName
     * @param classTree
     * @return
     */
    public static boolean isImplements(String packageName, ClassTree classTree) {
        Optional<TypeTree> runnableType = classTree.superInterfaces().stream()
                .filter(type -> packageName.equals(type.symbolType().fullyQualifiedName()))
                .findFirst();
        if (runnableType.isPresent()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为POJO类
     *
     * @param classTree
     * @return
     */
    public static boolean isPojo(ClassTree classTree) {
        // 如果是接口，则不是POJO类
        if (classTree.is(Tree.Kind.INTERFACE)) {
            return false;
        }
        // 如果是类，且后缀匹配的话，返回是POJO类
        String className = classTree.simpleName().name().toLowerCase();
        for (String names : POJO_NAMES) {
            if (className.endsWith(names)) {
                return true;
            }
        }
        return false;
    }

}