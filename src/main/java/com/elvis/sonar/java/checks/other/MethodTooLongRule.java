package com.elvis.sonar.java.checks.other;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.SyntaxToken;

import java.util.List;

/**
 * @author fengbingjian
 * @description 检查代码行数是否超过阈值
 * @since 2024/9/26 9:20
 **/
@Rule(key = "MethodTooLongRule")
public class MethodTooLongRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final int DEFAULT_MAX = 80;

    @RuleProperty(description = "方法内的最大有效行数", defaultValue = "" + DEFAULT_MAX)
    public int max = DEFAULT_MAX;

    private JavaFileScannerContext context;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitMethod(MethodTree tree) {

        BlockTree block = tree.block();

        if (block == null) {
            // 如果方法没有块（比如抽象方法），则直接返回
            return;
        }

        // 获取方法的第一个token
        SyntaxToken firstToken = tree.firstToken();
        // 获取方法的最后一个token
        SyntaxToken lastToken = tree.lastToken();
        if (firstToken == null || lastToken == null) {
            return;
        }

        // 计算方法的起始行号
        int startLine = firstToken.line();
        // 计算方法的结束行号
        int endLine = lastToken.line();

        /**
         * 如果方法内行数，算上注释也小于限制行数
         * 则直接通过，提高效率
         */
        int totalLines = endLine - startLine;
        if (totalLines < max) {
            return;
        }

        /**
         * 获取过滤掉注释后的准确代码行数
         * 如果超出则告警
         */
        totalLines = getLineCount(firstToken, lastToken, context.getFileLines());
        if (totalLines > max) {
            context.reportIssue(this, tree,
                    String.format("当前方法行数为：%s，单个方法的总行数不能超过%s行", totalLines, max));
        }
        super.visitMethod(tree);
    }

    /**
     * 获取缺省注释后的准确行数
     *
     * @param firstToken
     * @param lastToken
     * @param sourceCodeList
     * @return
     */
    private int getLineCount(SyntaxToken firstToken, SyntaxToken lastToken, List<String> sourceCodeList) {
        int totalLines = 0;
        int beginTag = firstToken.line();
        int endTag = lastToken.line() - 1;
        for (int i = beginTag; i < endTag; i++) {
            String sourceLine = sourceCodeList.get(i);
            if (!isBlankOrComment(sourceLine)) {
                totalLines++;
            }
        }
        return totalLines;
    }

    /**
     * 判断是否为注释行
     *
     * @param line
     * @return
     */
    private boolean isBlankOrComment(String line) {
        return line.trim().isEmpty() || line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().startsWith("*");
    }

}
