package com.elvis.sonar.java.checks.other;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Collections;
import java.util.List;

/**
 * TODO JSTL或模板规则，先不迁移
 *
 * @author fengbingjian
 * @description 变量使用的时候应在$后加感叹号
 * @since 2024/9/29 22:04
 **/
@Rule(key = "UseQuietReferenceNotationRule")
public class UseQuietReferenceNotationRule extends IssuableSubscriptionVisitor {

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