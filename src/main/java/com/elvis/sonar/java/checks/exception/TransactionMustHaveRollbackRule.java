package com.elvis.sonar.java.checks.exception;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CatchTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 注解【Transactional】需要设置rollbackFor属性
 * @since 2024/9/29 22:04
 **/
@Rule(key = "TransactionMustHaveRollbackRule")
public class TransactionMustHaveRollbackRule extends IssuableSubscriptionVisitor {

    private static final String CLASS_TRANSACTIONAL_MUST_ROLL_BACK_FOR_MESSAGE = "注解【Transactional】需要设置rollbackFor属性";
    private static final String METHOD_MUST_HAVE_ROLL_BACK_CALL_MESSAGE = "方法【%s】需要在Transactional注解指定rollbackFor，或者在方法中的catch中显式的rollback。";

    private static final String TRANSACTIONAL_ANNOTATION_NAME = "Transactional";
    private static final String ROLLBACK_FOR_ATTRIBUTE = "rollbackFor";
    private static final String ROLLBACK_PACKAGE_NAME = "DataSourceTransactionManager";
    private static final String ROLLBACK_METHOD = "rollback";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.ANNOTATION, Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.ANNOTATION)) {
            AnnotationTree annotation = (AnnotationTree) tree;
            // 检查注解是否是 @Transactional
            if (isTransactional(annotation)) {
                // 获取注解所在的父级节点
                Tree parentTree = findParent(annotation, Tree.Kind.CLASS, Tree.Kind.METHOD);
                // 判断注解是否有rollbackFor属性
                boolean transactionAnnotationHaveRollbackFor = hasRollbackForAttribute(annotation);
                if ((parentTree == null || parentTree.is(Tree.Kind.CLASS)) && !transactionAnnotationHaveRollbackFor) {
                    // 如果注解的父级是类，且没有rollbackFor属性，则报告问题
                    reportIssue(annotation, CLASS_TRANSACTIONAL_MUST_ROLL_BACK_FOR_MESSAGE);
                } else if (parentTree.is(Tree.Kind.METHOD)) {
                    // 如果注解的父级是方法，但注解没有rollbackFor属性，且方法内没有调用rollback，则报告问题
                    MethodTree methodTree = (MethodTree) parentTree;
                    if (!hasExplicitRollbackCall(methodTree.block()) && !transactionAnnotationHaveRollbackFor) {
                        reportIssue(methodTree, String.format(METHOD_MUST_HAVE_ROLL_BACK_CALL_MESSAGE, methodTree.simpleName()));
                    }
                }
            }
        }
    }

    /**
     * 向上查找父级节点，直到找到指定的节点类型
     *
     * @param tree
     * @param kinds
     * @return
     */
    private Tree findParent(Tree tree, Tree.Kind... kinds) {
        while (tree != null) {
            if (tree.is(kinds)) {
                return tree;
            }
            tree = tree.parent();
        }
        return null;
    }

    /**
     * 检查注解是否是 @Transactional
     *
     * @param annotation
     * @return
     */
    private boolean isTransactional(AnnotationTree annotation) {
        // 获取注解的类型
        Type annotationType = annotation.symbolType();
        // 检查注解类型是否是 @Transactional
        return TRANSACTIONAL_ANNOTATION_NAME.equals(annotationType.name());
    }

    /**
     * 检查注解是否有rollbackFor属性
     *
     * @param annotation
     * @return
     */
    private boolean hasRollbackForAttribute(AnnotationTree annotation) {
        // 获取注解的所有成员值对
        List<ExpressionTree> arguments = annotation.arguments();
        for (ExpressionTree argument : arguments) {
            if (argument.is(Tree.Kind.ASSIGNMENT)) {
                AssignmentExpressionTree assignment = (AssignmentExpressionTree) argument;
                if (assignment.variable().is(Tree.Kind.IDENTIFIER)) {
                    IdentifierTree identifier = (IdentifierTree) assignment.variable();
                    if (ROLLBACK_FOR_ATTRIBUTE.equals(identifier.name())) {
                        return true; // 找到了 rollbackFor 属性
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查方法块中是否有rollback方法调用
     *
     * @param block
     * @return
     */
    private boolean hasExplicitRollbackCall(BlockTree block) {
        if (block == null || block.body().isEmpty()) {
            return false;
        }
        for (StatementTree statement : block.body()) {
            if (statement instanceof TryStatementTree) {
                TryStatementTree tryStatementTree = (TryStatementTree) statement;
                return checkRollbackInCatchBlock(tryStatementTree.catches());
            }
        }
        return false;
    }

    /**
     * 检查catch中是否有rollback方法调用
     *
     * @param catchTreeList
     * @return
     */
    private boolean checkRollbackInCatchBlock(List<CatchTree> catchTreeList) {
        if (catchTreeList == null) {
            return false;
        }
        for (CatchTree catchTree : catchTreeList) {
            BlockTree catchBlockTree = catchTree.block();
            for (StatementTree catchStatement : catchBlockTree.body()) {
                if (catchStatement instanceof ExpressionStatementTree) {
                    ExpressionStatementTree expressionStatement = (ExpressionStatementTree) catchStatement;
                    if (expressionStatement.expression() instanceof MethodInvocationTree) {
                        MethodInvocationTree methodInvocation = (MethodInvocationTree) expressionStatement.expression();
                        if (MethodInvocationTreeCheckUtil.isMethodCall(ROLLBACK_METHOD, methodInvocation)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}