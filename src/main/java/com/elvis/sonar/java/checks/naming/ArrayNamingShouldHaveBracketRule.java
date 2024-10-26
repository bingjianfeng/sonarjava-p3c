package com.elvis.sonar.java.checks.naming;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ArrayTypeTree;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.Collections;
import java.util.List;

/**
 * @author fengbingjian
 * @description 数组变量中括号位置错误
 * @since 2024/9/26 17:35
 **/
@Rule(key = "ArrayNamingShouldHaveBracketRule")
public class ArrayNamingShouldHaveBracketRule extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        TypeTree type = variableTree.type();
        SyntaxToken identifierToken = variableTree.simpleName().identifierToken();
        while (type.is(Tree.Kind.ARRAY_TYPE)) {
            ArrayTypeTree arrayTypeTree = (ArrayTypeTree) type;
            SyntaxToken arrayDesignatorToken = arrayTypeTree.ellipsisToken();
            if (arrayDesignatorToken == null) {
                arrayDesignatorToken = arrayTypeTree.openBracketToken();
            }
            if (isInvalidPosition(arrayDesignatorToken, identifierToken)) {
                reportIssue(arrayDesignatorToken, String.format("数组变量【%s】中括号位置错误", variableTree.simpleName()));
                break;
            }
            type = arrayTypeTree.type();
        }
    }

    private static boolean isInvalidPosition(SyntaxToken arrayDesignatorToken, SyntaxToken identifierToken) {
        return identifierToken.line() < arrayDesignatorToken.line()
                || (identifierToken.line() == arrayDesignatorToken.line() && identifierToken.column() < arrayDesignatorToken.column());
    }

}