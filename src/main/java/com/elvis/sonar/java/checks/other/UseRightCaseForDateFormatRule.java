package com.elvis.sonar.java.checks.other;

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
 * @description 日期格式化字符串使用错误，应注意使用小写‘y’表示当天所在的年，大写‘Y’代表week in which year。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "UseRightCaseForDateFormatRule")
public class UseRightCaseForDateFormatRule extends IssuableSubscriptionVisitor {

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