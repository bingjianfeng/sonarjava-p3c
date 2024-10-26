package com.elvis.sonar.java.checks.concurrent;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengbingjian
 * @description ThreadLocal字段应该至少调用一次remove方法
 * @since 2024/9/29 22:04
 **/
@Rule(key = "ThreadLocalShouldRemoveRule")
public class ThreadLocalShouldRemoveRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "ThreadLocal字段【%s】应该至少调用一次remove()方法。";
    private static final String THREAD_LOCAL_PACKAGE_PATH = "java.lang.ThreadLocal";
    private static final String REMOVE_METHOD = "remove";

    private Map<String, VariableTree> threadLocalName;
    private Map<String, String> threadLocalHaveRemove;

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.VARIABLE, Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS)) {
            threadLocalName = new HashMap<>();
            threadLocalHaveRemove = new HashMap<>();
        } else if (tree.is(Tree.Kind.VARIABLE)) {
            VariableTree variableTree = (VariableTree) tree;
            TypeTree type = variableTree.type();
            if (type.symbolType().is(THREAD_LOCAL_PACKAGE_PATH)) {
                threadLocalName.put(variableTree.simpleName().name(), variableTree);
            }
        } else if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
            if (MethodInvocationTreeCheckUtil.isMethodCall(REMOVE_METHOD, THREAD_LOCAL_PACKAGE_PATH, methodInvocationTree)) {
                String methodName = MethodInvocationTreeCheckUtil.getName(methodInvocationTree);
                threadLocalHaveRemove.put(methodName, methodName);
            }
        }
    }

    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS)) {
            if (threadLocalName.size() > 0) {
                for (String key : threadLocalName.keySet()) {
                    if (threadLocalHaveRemove.get(key) == null) {
                        reportIssue(threadLocalName.get(key), String.format(MESSAGE, key));
                    }
                }
            }
            threadLocalName = new HashMap<>();
            threadLocalHaveRemove = new HashMap<>();
        }
    }
}