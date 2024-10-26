package com.elvis.sonar.java.checks.naming;

import com.elvis.sonar.java.checks.utils.VariableTreeCheckUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author fengbingjian
 * @description 方法名、参数名、成员变量、局部变量都统一使用lowerCamelCase，必须遵从驼峰形式
 * @since 2024/9/26 17:35
 **/
@Rule(key = "LowerCamelCaseVariableNamingRule")
public class LowerCamelCaseVariableNamingRule extends IssuableSubscriptionVisitor {

    private static final String FORMAT = "^[a-z][a-z0-9]*([A-Z][a-z0-9]+)*(DO|DTO|VO|DAO|BO|DOList|DTOList|VOList|DAOList|BOList|X|Y|Z|UDF|UDAF|[A-Z])?$";
    private Pattern pattern = null;
    private static final String DOLLAR = "$";
    private static final String UNDER_LINE = "_";

    @Override
    public void setContext(JavaFileScannerContext context) {
        if (pattern == null) {
            pattern = Pattern.compile(FORMAT, Pattern.DOTALL);
        }
        super.setContext(context);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.METHOD, Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD)) {
            scanMethod((MethodTree) tree);
        }else if (tree.is(Tree.Kind.VARIABLE)) {
            scanVariable((VariableTree) tree);
        }
    }

    private void scanMethod(MethodTree tree){
        if (!pattern.matcher(tree.simpleName().name()).matches()) {
            reportIssue(tree.simpleName(), String.format("方法名【%s】不符合lowerCamelCase命名风格", tree.simpleName().name()));
        }
    }

    private void scanVariable(VariableTree tree){
        String varName = tree.simpleName().name();
        if (!pattern.matcher(varName).matches()) {
            /**
             * 如果是以_或$开头
             * 或者是常量的，则忽略
             * 防止与 AvoidStartWithDollarAndUnderLineNamingRule 及 ConstantFieldShouldBeUpperCaseRule 规则发生冲突
             */
            if(startWithDollarOrUnderLine(varName) || VariableTreeCheckUtil.isStaticAndFinal(tree)){
                return;
            }
            reportIssue(tree.simpleName(), String.format("变量名【%s】不符合lowerCamelCase命名风格", tree.simpleName().name()));
        }
    }

    private boolean startWithDollarOrUnderLine(String name){
        return name.startsWith(DOLLAR) || name.startsWith(UNDER_LINE);
    }
}