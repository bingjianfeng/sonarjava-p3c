package com.elvis.sonar.java.checks.concurrent;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Collections;
import java.util.List;

/**
 * TODO 该规则不太合理，不迁移
 *
 * @author fengbingjian
 * @description 创建线程或线程池时请指定有意义的线程名称，创建线程池请使用带ThreadFactory的构造函数，并实现ThreadFactory。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "ThreadShouldSetNameRule")
public class ThreadShouldSetNameRule extends IssuableSubscriptionVisitor {

    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    /**
     * 指定抽象树的扫描范围
     *
     * @return
     */
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.emptyList();
    }

    @Override
    public void visitNode(Tree tree) {

    }

}