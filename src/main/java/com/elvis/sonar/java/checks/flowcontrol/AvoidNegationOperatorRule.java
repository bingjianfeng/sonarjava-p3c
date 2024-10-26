package com.elvis.sonar.java.checks.flowcontrol;

import com.elvis.sonar.java.checks.utils.MethodInvocationTreeCheckUtil;
import com.elvis.sonar.java.checks.utils.SourceContextUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.UnaryExpressionTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author fengbingjian
 * @description 运算符不利于快速理解
 * @since 2024/9/29 22:04
 */
@Rule(key = "AvoidNegationOperatorRule")
public class AvoidNegationOperatorRule extends IssuableSubscriptionVisitor {

    private static final String DOUBLE_NEGATIVE_MESSAGE = "禁止使用双重否定运算符";
    private static final String TOO_MANY_NEGATIVE_MESSAGE = "过多否定运算符不利于快速理解";

    //否定运算符达到次数时，报告问题
    private static final int NEGATIVE_TIMES = 3;

    @Override
    public void setContext(JavaFileScannerContext context) {
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // 我们需要访问所有可能包含否定操作符的节点类型
        return Arrays.asList(
                Tree.Kind.IF_STATEMENT,
                Tree.Kind.VARIABLE,
                Tree.Kind.RETURN_STATEMENT
        );
    }

    @Override
    public void visitNode(Tree tree) {
        // 找出节点的源码
        String sourceCode = SourceContextUtil.getLineString(tree, context);
        if (sourceCode == null) {
            return;
        }
        // 如果是IF条件判断，则尽可能只取IF条件判断的部分
        if(tree.is(Tree.Kind.IF_STATEMENT)){
            if(sourceCode.indexOf("{") > 0){
                sourceCode = sourceCode.substring(0,sourceCode.indexOf("{"));
            }else if(sourceCode.indexOf(")") > 0){
                sourceCode = sourceCode.substring(0,sourceCode.lastIndexOf(")"));
            }
        }
        if (sourceCode.indexOf("!!") >= 0) {
            reportIssue(tree, DOUBLE_NEGATIVE_MESSAGE);
        } else if (countOccurrences(sourceCode, "!") >= NEGATIVE_TIMES) {
            reportIssue(tree, TOO_MANY_NEGATIVE_MESSAGE);
        }
    }

    /**
     * 计算字符串在 context 中出现的次数
     *
     * @param context 主字符串
     * @param keyword 要查找的子字符串
     * @return 子字符串 a 出现的次数
     */
    private int countOccurrences(String context, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = context.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
}