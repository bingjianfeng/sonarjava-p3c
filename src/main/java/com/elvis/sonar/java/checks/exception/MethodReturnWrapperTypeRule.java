package com.elvis.sonar.java.checks.exception;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.ReturnStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengbingjian
 * @description 返回类型为基本数据类型，return包装数据类型的对象时，自动拆箱有可能产生NPE的风险
 * @since 2024/9/29 22:04
 **/
@Rule(key = "MethodReturnWrapperTypeRule")
public class MethodReturnWrapperTypeRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "返回类型为基本数据类型【%s】，return包装数据类型的对象【%s】时，自动拆箱有可能产生NPE的风险";

    private static final Map<String, String> PRIMITIVE_TYPE_TO_WRAPPER_TYPE = new HashMap<>();

    static {
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("int", "java.lang.Integer");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("long", "java.lang.Long");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("double", "java.lang.Double");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("float", "java.lang.Float");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("boolean", "java.lang.Boolean");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("char", "java.lang.Character");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("byte", "java.lang.Byte");
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("short", "java.lang.Short");
    }

    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.METHOD);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD)) {
            MethodTree methodTree = (MethodTree) tree;
            // 检查方法的返回类型是否是基本类型
            Type returnType = methodTree.returnType().symbolType();
            if (returnType.isPrimitive()) {
                // 如果是基本类型，检查return的是否包装类型，如果是，则报告问题
                checkReturnWrapperType(methodTree);
            }
        }
    }

    /**
     * 检查return的是否包装类型，如果是，则报告问题
     *
     * @param methodTree
     */
    private void checkReturnWrapperType(MethodTree methodTree) {
        // 获取基本类型的名称
        String primitiveTypeName = methodTree.returnType().symbolType().name();

        if (methodTree.block() == null || methodTree.block().body() == null) {
            return;
        }

        // 遍历方法体中的所有语句
        for (StatementTree statement : methodTree.block().body()) {

            if (!statement.is(Tree.Kind.RETURN_STATEMENT)) {
                continue;
            }
            ReturnStatementTree returnStatement = (ReturnStatementTree) statement;
            ExpressionTree expression = returnStatement.expression();
            // 如果没有表达式，跳过
            if (expression == null) {
                continue;
            }

            // 检查返回表达式的类型
            Type returnTypeOfExpression = expression.symbolType();
            if (!returnTypeOfExpression.isClass()) {
                continue;
            }

            // 获取返回表达式的类型名称
            String wrapperTypeName = returnTypeOfExpression.fullyQualifiedName();

            // 检查返回类型是否是基本类型的包装类
            if (PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(primitiveTypeName).equals(wrapperTypeName)) {
                reportIssue(returnStatement, String.format(MESSAGE, primitiveTypeName, wrapperTypeName));
            }
        }
    }

}