package com.elvis.sonar.java.checks.other;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description 注意 Math.random() 这个方法返回是double类型，注意取值的范围。
 * @since 2024/9/29 15:46
 **/
@Rule(key = "AvoidMissUseOfMathRandomRule")
public class AvoidMissUseOfMathRandomRule extends IssuableSubscriptionVisitor {

    private static final MethodMatchers MATH_RANDOM_METHOD_MATCHER = MethodMatchers.create()
            .ofTypes("java.lang.Math").names("random").addWithoutParametersMatcher().build();


    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
        if (MATH_RANDOM_METHOD_MATCHER.matches(methodInvocation)) {
            reportIssue(methodInvocation.methodSelect(), "注意 Math.random() 这个方法返回是double类型，注意取值的范围。");
        }
    }
}