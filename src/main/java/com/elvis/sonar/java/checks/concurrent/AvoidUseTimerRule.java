package com.elvis.sonar.java.checks.concurrent;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.VariableTree;

/**
 * @author fengbingjian
 * @description 多线程并行处理定时任务时，Timer运行多个TimeTask时，应使用ScheduledExecutorService。
 * @since 2024/9/29 22:04
 **/
@Rule(key = "AvoidUseTimerRule")
public class AvoidUseTimerRule extends BaseTreeVisitor implements JavaFileScanner {

    private JavaFileScannerContext context;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitVariable(VariableTree tree) {
        Type type = tree.type().symbolType();
        if (isTimerType(type)) {
            context.reportIssue(this, tree, "多线程并行处理定时任务时，Timer运行多个TimeTask时，应使用ScheduledExecutorService。");
        }
        super.visitVariable(tree);
    }

    /**
     * 判断类型是否Timer
     *
     * @param typeSymbol
     * @return
     */
    private boolean isTimerType(Type typeSymbol) {
        return typeSymbol != null && "java.util.Timer".equals(typeSymbol.fullyQualifiedName());
    }
}