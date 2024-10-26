package com.elvis.sonar.java.checks.oop;

import com.elvis.sonar.java.checks.utils.ClassTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;


/**
 * @author fengbingjian
 * @description POJO类必须写toString方法
 * @since 2024/10/08 16:30
 */
@Rule(key = "PojoMustOverrideToStringRule")
public class PojoMustOverrideToStringRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "POJO类必须写toString方法";
    private static final String LOMBOK_ANNOTATION = "lombok.Data";
    private static final String LOMBOK_TOSTRING_ANNOTATION = "lombok.ToString";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们想要访问类声明节点
        return Arrays.asList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.CLASS)) {
            ClassTree classTree = (ClassTree) tree;
            if (ClassTreeCheckUtil.isPojo(classTree) && !withLombokAnnotation(classTree)) {
                checkForToStringMethod(classTree);
            }
        }
    }

    /**
     * 检查类是否有Lombok的@Data或@ToString注解
     *
     * @param classTree
     * @return
     */
    private boolean withLombokAnnotation(ClassTree classTree) {
        for (AnnotationTree annotation : classTree.modifiers().annotations()) {
            if (LOMBOK_ANNOTATION.equals(annotation.annotationType().symbolType().fullyQualifiedName()) ||
                    LOMBOK_TOSTRING_ANNOTATION.equals(annotation.annotationType().symbolType().fullyQualifiedName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查类中是否有公共的无参toString方法
     *
     * @param classTree
     */
    private void checkForToStringMethod(ClassTree classTree) {
        boolean hasToString = false;
        for (Tree member : classTree.members()) {
            if (!member.is(Tree.Kind.METHOD)) {
                continue;
            }
            MethodTree method = (MethodTree) member;
            if (method.symbol().name().contentEquals("toString") &&
                    method.parameters().isEmpty() &&
                    method.symbol().isPublic()) {
                hasToString = true;
                break;
            }
        }
        if (!hasToString) {
            reportIssue(classTree, MESSAGE);
        }
    }
}