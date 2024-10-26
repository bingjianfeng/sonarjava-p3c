package com.elvis.sonar.java.checks.oop;

import com.elvis.sonar.java.checks.utils.ClassTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 定义DO/DTO/VO等POJO类时，不要加任何属性默认值。
 * @since 2024/10/08 16:30
 */
@Rule(key = "PojoNoDefaultValueRule")
public class PojoNoDefaultValueRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "字段【%s】不应该加默认值";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问类或接口声明节点
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.INTERFACE);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS, Tree.Kind.INTERFACE)) {
            ClassTree classTree = (ClassTree) tree;
            if (ClassTreeCheckUtil.isPojo(classTree)) {
                for (Tree member : classTree.members()) {
                    if (member.is(Tree.Kind.VARIABLE)) {
                        VariableTree variable = (VariableTree) member;
                        if (shouldProcess(variable)) {
                            reportIssue(variable, String.format(MESSAGE, variable.simpleName().name()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断是否为有默认值的字段
     * @param variable
     * @return
     */
    private boolean shouldProcess(VariableTree variable) {
        // 检查变量是否是字段（成员变量），并且不是public、final、static或volatile
        // 同时检查变量是否有初始化表达式
        return variable.symbol().owner().isTypeSymbol() &&
                !variable.symbol().isPublic() &&
                !variable.symbol().isFinal() &&
                !variable.symbol().isStatic() &&
                !variable.symbol().isVolatile() &&
                variable.initializer() != null;
    }

}