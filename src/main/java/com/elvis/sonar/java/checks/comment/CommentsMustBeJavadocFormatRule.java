package com.elvis.sonar.java.checks.comment;

import com.elvis.sonar.java.checks.enums.CommentEnum;
import com.elvis.sonar.java.checks.utils.CommentUtil;
import com.elvis.sonar.java.checks.utils.TreeUtil;
import com.elvis.sonar.java.checks.utils.VariableTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.EnumConstantTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description 类、类属性、类方法的注释必须使用javadoc规范
 * @since 2024/10/08 16:30
 */
@Rule(key = "CommentsMustBeJavadocFormatRule")
public class CommentsMustBeJavadocFormatRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "类、类属性、类方法的注释必须使用javadoc规范";
    private static final String CLASS_MESSAGE = "类【%s】必须使用javadoc形式的注释";
    private static final String CONSTRUCTOR_MESSAGE = "构造方法【%s()】必须使用javadoc形式的注释";
    private static final String METHOD_MESSAGE = "方法【%s】必须使用javadoc形式的注释";
    private static final String FIELD_MESSAGE = "字段【%s】必须使用javadoc形式的注释";
    private static final String ENUM_MESSAGE = "枚举【%s】必须使用javadoc形式的注释";


    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问的节点类型列表
        return Arrays.asList(
                Tree.Kind.CLASS,
                Tree.Kind.INTERFACE,
                Tree.Kind.METHOD,
                Tree.Kind.CONSTRUCTOR,
                Tree.Kind.VARIABLE,
                Tree.Kind.ENUM,
                Tree.Kind.ENUM_CONSTANT
        );
    }

    @Override
    public void visitNode(Tree tree) {
        // 如果是类属性，不检查
        if (tree.is(Tree.Kind.VARIABLE) && !VariableTreeCheckUtil.isClassAttribute((VariableTree) tree)) {
            return;
        }
        // 如果不是枚举值，不检查
        if (!tree.is(Tree.Kind.ENUM_CONSTANT) && TreeUtil.findParent(tree, Tree.Kind.ENUM)) {
            return;
        }
        // 获取抽象树的注释类型
        CommentEnum commentEnum = CommentUtil.getCommentType(tree, context);
        if (!CommentEnum.DOCUMENTATION.equals(commentEnum) && !CommentEnum.UNKNOW.equals(commentEnum)) {
            reportIssueByTree(tree);
        }
    }

    /**
     * 根据树类型汇报问题
     *
     * @param tree
     */
    private void reportIssueByTree(Tree tree) {
        if (tree.is(Tree.Kind.CLASS, Tree.Kind.INTERFACE, Tree.Kind.ENUM)) {
            ClassTree classTree = (ClassTree) tree;
            reportIssue(tree, String.format(CLASS_MESSAGE, classTree.simpleName().name()));
        } else if (tree.is(Tree.Kind.METHOD)) {
            MethodTree methodTree = (MethodTree) tree;
            reportIssue(tree, String.format(METHOD_MESSAGE, methodTree.simpleName().name()));
        } else if (tree.is(Tree.Kind.CONSTRUCTOR)) {
            MethodTree methodTree = (MethodTree) tree;
            reportIssue(tree, String.format(CONSTRUCTOR_MESSAGE, methodTree.simpleName().name()));
        } else if (tree.is(Tree.Kind.VARIABLE)) {
            VariableTree variableTree = (VariableTree) tree;
            reportIssue(tree, String.format(FIELD_MESSAGE, variableTree.simpleName().name()));
        } else if (tree.is(Tree.Kind.ENUM_CONSTANT)) {
            EnumConstantTree enumConstantTree = (EnumConstantTree) tree;
            reportIssue(tree, String.format(ENUM_MESSAGE, enumConstantTree.simpleName().name()));
        } else {
            reportIssue(tree, MESSAGE);
        }
    }

}