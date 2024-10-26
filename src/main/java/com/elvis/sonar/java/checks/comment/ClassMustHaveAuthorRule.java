package com.elvis.sonar.java.checks.comment;

import com.elvis.sonar.java.checks.enums.CommentEnum;
import com.elvis.sonar.java.checks.utils.CommentUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author fengbingjian
 * @description 所有的类都必须添加创建者信息
 * @since 2024/10/08 16:30
 */
@Rule(key = "ClassMustHaveAuthorRule")
public class ClassMustHaveAuthorRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】缺少包含@author的注释信息";

    private static final Pattern AUTHOR_PATTERN = Pattern.compile(".*@[Aa]uthor.*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问的节点类型列表
        return Arrays.asList(Tree.Kind.CLASS, Tree.Kind.INTERFACE, Tree.Kind.ENUM, Tree.Kind.ANNOTATION_TYPE);
    }

    @Override
    public void visitNode(Tree tree) {
        checkAuthorComment(tree);
    }

    private void checkAuthorComment(Tree tree) {
        // 没有注解或未知内容的问题，由其他规则判定（CommentsMustBeJavadocFormatRule）
        if (CommentEnum.UNKNOW.equals(CommentUtil.getCommentType(tree, context))) {
            return;
        }
        String commentContent = CommentUtil.getComment(tree, context);
        if (!AUTHOR_PATTERN.matcher(commentContent).matches()) {
            reportMessage(tree);
        }
    }

    private void reportMessage(Tree tree) {
        if (tree.is(Tree.Kind.CLASS, Tree.Kind.INTERFACE, Tree.Kind.ENUM, Tree.Kind.ANNOTATION_TYPE)) {
            reportIssue(tree, String.format(MESSAGE, ((ClassTree) tree).simpleName().name()));
        } else if (tree.is(Tree.Kind.METHOD)) {
            reportIssue(tree, String.format(MESSAGE, ((MethodTree) tree).simpleName().name()));
        }
    }
}