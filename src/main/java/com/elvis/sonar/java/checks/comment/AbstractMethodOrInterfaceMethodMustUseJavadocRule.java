package com.elvis.sonar.java.checks.comment;

import com.elvis.sonar.java.checks.enums.CommentEnum;
import com.elvis.sonar.java.checks.utils.CommentUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author fengbingjian
 * @description 所有的抽象方法必须要用javadoc注释、除了返回值、参数、异常说明外，还必须指出该方法的用途。
 * @since 2024/10/08 16:30
 */
@Rule(key = "AbstractMethodOrInterfaceMethodMustUseJavadocRule")
public class AbstractMethodOrInterfaceMethodMustUseJavadocRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "所有的抽象方法（包括接口中的方法）必须要用javadoc注释";
    private static final String ABSTRACT_METHOD_MESSAGE = "抽象方法【%s】必须使用javadoc注释";
    private static final String ABSTRACT_INTERFACE_MESSAGE = "接口方法【%s】必须使用javadoc注释";
    private static final String METHOD_DESC_NOT_FOUND_MESSAGE = "请详细描述方法【%s】的功能与意图";
    private static final String METHOD_PARAMS_DESC_NOT_FOUND_MESSAGE = "方法【%s】的参数【%s】缺少javadoc注释";
    private static final String METHOD_RETURN_DESC_NOT_FOUND_MESSAGE = "方法【%s】的返回值缺少javadoc注释";
    private static final String METHOD_EXCEPTION_DESC_NOT_FOUND_MESSAGE = "方法【%s】的异常【%s】缺少javadoc注释";

    // 判断是否有详细方法说明的正则表达式
    private static final Pattern EMPTY_CONTENT_PATTERN = Pattern.compile("[/*\\n\\r\\s]+(@.*)?", Pattern.DOTALL);

    // 判断是否有return注释的正则表达式
    private static final Pattern RETURN_PATTERN = Pattern.compile(".*@return.*", Pattern.DOTALL);

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问的节点类型列表
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.INTERFACE);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS, Tree.Kind.INTERFACE)) {
            ClassTree classTree = (ClassTree) tree;
            if (tree.is(Tree.Kind.CLASS) && !classTree.symbol().isAbstract()) {
                return;
            }
            checkAbstractMethods(tree.kind(), classTree.members());
        }
    }

    /**
     * 检查是否抽象方法
     *
     * @param parentKind 父节点抽象树类型
     * @param members    节点成员
     */
    private void checkAbstractMethods(Tree.Kind parentKind, List<Tree> members) {
        for (Tree member : members) {
            if (member.is(Tree.Kind.METHOD)) {
                MethodTree methodTree = (MethodTree) member;
                if (methodTree.symbol().isMethodSymbol() && methodTree.symbol().isAbstract()) {
                    checkMethodCommentFormat(parentKind, methodTree);
                }
            }
        }
    }

    /**
     * 检查抽象方法的注释是否符合规范
     *
     * @param parentKind 父节点抽象树类型
     * @param method     方法抽象树
     */
    private void checkMethodCommentFormat(Tree.Kind parentKind, MethodTree method) {

        // 如果不是javadoc注释，则报告问题
        if (!CommentEnum.DOCUMENTATION.equals(CommentUtil.getCommentType(method, context))) {
            if (Tree.Kind.CLASS.equals(parentKind)) {
                reportIssue(method, getMessage("class", method.simpleName().name()));
            } else if (Tree.Kind.INTERFACE.equals(parentKind)) {
                reportIssue(method, getMessage("interface", method.simpleName().name()));
            }
            return;
        }

        String commentContent = CommentUtil.getComment(method, context);

        // 方法说明
        if (EMPTY_CONTENT_PATTERN.matcher(commentContent).matches()) {
            reportIssue(method, getMessage("desc", method.simpleName().name()));
        }

        // 参数描述
        for (VariableTree param : method.parameters()) {
            String paramName = param.simpleName().name();
            if (!Pattern.compile(".*@param\\s+" + paramName + ".*", Pattern.DOTALL).matcher(commentContent).matches()) {
                reportIssue(method, getMessage("parameter", method.simpleName().name(), paramName));
            }
        }

        // 返回值
        if (!method.returnType().symbolType().isVoid() && !RETURN_PATTERN.matcher(commentContent).matches()) {
            reportIssue(method, getMessage("return", method.simpleName().name()));
        }

        // 可能抛出的异常
        for (TypeTree thrown : method.throwsClauses()) {
            String exceptionName = thrown.symbolType().name();
            if (!Pattern.compile(".*@throws\\s+" + exceptionName + ".*", Pattern.DOTALL).matcher(commentContent).matches()) {
                reportIssue(method, getMessage("exception", method.simpleName().name(), exceptionName));
            }
        }
    }

    /**
     * 根据不同的key获取不同的message
     *
     * @param key
     * @param args
     * @return
     */
    private String getMessage(String key, String... args) {
        switch (key) {
            case "class":
                return String.format(ABSTRACT_METHOD_MESSAGE, args[0]);
            case "interface":
                return String.format(ABSTRACT_INTERFACE_MESSAGE, args[0]);
            case "desc":
                return String.format(METHOD_DESC_NOT_FOUND_MESSAGE, args[0]);
            case "parameter":
                return String.format(METHOD_PARAMS_DESC_NOT_FOUND_MESSAGE, args[0], args[1]);
            case "return":
                return String.format(METHOD_RETURN_DESC_NOT_FOUND_MESSAGE, args[0]);
            case "exception":
                return String.format(METHOD_EXCEPTION_DESC_NOT_FOUND_MESSAGE, args[0], args[1]);
            default:
                return MESSAGE;
        }
    }
}