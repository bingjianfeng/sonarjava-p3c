package com.elvis.sonar.java.checks.set;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 集合初始化时，指定集合初始值大小
 * @since 2024/9/29 22:04
 */
@Rule(key = "CollectionInitShouldAssignCapacityRule")
public class CollectionInitShouldAssignCapacityRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "【%s】初始化时，尽量指定初始值大小";

    // 黑名单，包含需要检查的集合类型
    private static final List<String> COLLECTION_LIST = Arrays.asList(
            "ArrayList", "HashMap", "HashSet", "LinkedHashSet", "TreeSet", "LinkedList"
    );

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.NEW_CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.NEW_CLASS)) {
            NewClassTree newClassTree = (NewClassTree) tree;
            checkCollectionInitialization(newClassTree);
        }
    }

    private void checkCollectionInitialization(NewClassTree newClassTree) {
        // 获取构造函数调用中的参数列表
        Arguments arguments = newClassTree.arguments();
        // 检查是否是需要检查的集合类型
        String className = newClassTree.identifier().symbolType().name();
        if (COLLECTION_LIST.contains(className) && arguments.isEmpty()) {
            reportIssue(newClassTree, String.format(MESSAGE, className));
        }
    }
}