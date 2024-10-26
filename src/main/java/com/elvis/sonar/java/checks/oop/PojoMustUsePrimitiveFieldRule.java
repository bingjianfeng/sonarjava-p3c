package com.elvis.sonar.java.checks.oop;

import com.elvis.sonar.java.checks.utils.ClassTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 字段应使用包装类型
 * @since 2024/10/08 16:30
 */
@Rule(key = "PojoMustUsePrimitiveFieldRule")
public class PojoMustUsePrimitiveFieldRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "字段【%s】应使用包装类型";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问类或接口声明节点
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.INTERFACE);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS, Tree.Kind.INTERFACE)) {
            ClassTree classTree = (ClassTree) tree;
            // 如果不是POJO类，则判断下一个
            if (!ClassTreeCheckUtil.isPojo(classTree)) {
                return;
            }
            for (Tree member : classTree.members()) {
                if (!member.is(Tree.Kind.VARIABLE)) {
                    continue;
                }
                VariableTree variable = (VariableTree) member;
                if (!isField(variable)) {
                    continue;
                }
                TypeTree typeTree = variable.type();
                if (isPrimitiveType(typeTree)) {
                    reportIssue(typeTree, String.format(MESSAGE, variable.simpleName()));
                }
            }
        }
    }

    /**
     * 判断是否为字段
     *
     * @param variable
     * @return
     */
    private boolean isField(VariableTree variable) {
        // 检查变量是否是字段（成员变量）
        // 字段通常在类级别声明，而不是在方法内部或其他局部作用域
        return variable.symbol().owner().isTypeSymbol();
    }

    /**
     * 判断是否为基本类型
     *
     * @param typeTree
     * @return
     */
    private boolean isPrimitiveType(TypeTree typeTree) {
        // 检查类型是否为基本类型
        return typeTree.is(Tree.Kind.PRIMITIVE_TYPE);
    }

}