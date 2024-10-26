package com.elvis.sonar.java.checks.orm;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 禁止直接使用jdbc驱动操作数据库
 * @since 2024/10/08 16:30
 */
@Rule(key = "AvoidDirectJdbcDriverRule")
public class AvoidDirectJdbcDriverRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】避免直接使用jdbc驱动操作数据库";

    private static final List<String> METHOD_BLACK_LIST = Arrays.asList("getBaseDao", "executeQuery", "executeUpdate");
    private static final List<String> SYMBOL_BLACK_LIST = Arrays.asList("java.sql.Connection", "java.sql.PreparedStatement");

    private static List<Integer> reportIssueLine;

    static {
        reportIssueLine = new ArrayList<>();
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问的节点类型列表
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.METHOD_INVOCATION, Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            checkMethodInvocation(tree);
        } else if (tree.is(Tree.Kind.VARIABLE)) {
            checkVariable(tree);
        }
        super.visitNode(tree);
    }

    @Override
    public void leaveNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS)) {
            reportIssueLine = new ArrayList<>();
        }
    }

    /**
     * 扫描方法使用节点
     *
     * @param tree
     */
    private void checkMethodInvocation(Tree tree) {
        MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
        if (isExecuteCall(methodInvocationTree) && !reportIssueLine.contains(methodInvocationTree.firstToken().line())) {
            reportIssueLine.add(methodInvocationTree.firstToken().line());
            reportIssue(tree, String.format(MESSAGE, MethodInvocationTreeCheckUtil.getMethodName(methodInvocationTree)));
        }
    }

    /**
     * 扫描变量声明节点
     *
     * @param tree
     */
    private void checkVariable(Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        // 检查变量声明
        Type type = variableTree.type().symbolType();
        if (isConnectionOrPreparedStatement(type)) {
            reportIssue(tree, String.format(MESSAGE, type.fullyQualifiedName()));
        }
    }

    /**
     * 检查方法内是否使用了executeQuery，executeUpdate
     *
     * @param tree
     * @return
     */
    private boolean isExecuteCall(MethodInvocationTree tree) {
        return MethodInvocationTreeCheckUtil.isMethodCall(METHOD_BLACK_LIST, tree);
    }

    /**
     * 检查是否使用了Connection或PreparedStatement进行了对象实例化
     *
     * @param type
     * @return
     */
    private boolean isConnectionOrPreparedStatement(Type type) {
        return SYMBOL_BLACK_LIST.contains(type.fullyQualifiedName());
    }

}