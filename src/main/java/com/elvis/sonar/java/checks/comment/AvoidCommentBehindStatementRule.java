package com.elvis.sonar.java.checks.comment;

import com.elvis.sonar.java.checks.utils.CommentUtil;
import com.elvis.sonar.java.checks.utils.SourceContextUtil;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * TODO
 *
 * @author fengbingjian
 * @description 请不要使用行尾注释
 * @since 2024/10/08 16:30
 */
@Rule(key = "AvoidCommentBehindStatementRule")
public class AvoidCommentBehindStatementRule extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "请不要使用行尾注释";
    private static final int ARRAY_LIST_SIZE = 10;

    private static List<Integer> lineEndList;

    static {
        lineEndList = new ArrayList<>(ARRAY_LIST_SIZE);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.COMPILATION_UNIT);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.COMPILATION_UNIT)) {
            CompilationUnitTree compilationUnit = (CompilationUnitTree) tree;
            checkNodesForRuleViolations(compilationUnit);
        }
    }

    @Override
    public void leaveNode(Tree tree){
        if(tree.is(Tree.Kind.COMPILATION_UNIT)){
            lineEndList = new ArrayList<>(ARRAY_LIST_SIZE);
        }
    }

    /**
     * 递归遍历所有节点并检查是否违反规则。
     *
     * @param tree 当前节点
     */
    private void checkNodesForRuleViolations(Tree tree) {
        // 递归遍历所有子节点
        new BaseTreeVisitor() {
            @Override
            public void scan(Tree node) {
                if (node == null) {
                    return;
                }
                if (!node.is(Tree.Kind.COMPILATION_UNIT)) {
                    checkLineEndComment(node);
                }
                // 递归遍历子节点
                switch (node.kind()) {
                    case COMPILATION_UNIT:
                        CompilationUnitTree cu = (CompilationUnitTree) node;
                        for (Tree importDecl : cu.imports()) {
                            scan(importDecl);
                        }
                        for (Tree typeDecl : cu.types()) {
                            scan(typeDecl);
                        }
                        break;
                    case CLASS:
                    case INTERFACE:
                    case ENUM:
                    case ANNOTATION_TYPE:
                        ClassTree classes = (ClassTree) node;
                        scan(classes.members());
                        break;
                    case METHOD:
                        MethodTree method = (MethodTree) node;
                        scan(method.block());
                        break;
                    case BLOCK:
                        BlockTree block = (BlockTree) node;
                        for (StatementTree statement : block.body()) {
                            scan(statement);
                        }
                        break;
                    case IF_STATEMENT:
                        IfStatementTree ifStmt = (IfStatementTree) node;
                        scan(ifStmt.thenStatement());
                        if (ifStmt.elseStatement() != null) {
                            scan(ifStmt.elseStatement());
                        }
                        break;
                    // 添加更多特定类型的处理
                    default:
                        super.scan(node);
                        break;
                }
            }
        }.scan(tree);
    }

    /**
     * 检查Tree内是否有行内注释
     *
     * @param tree
     */
    private void checkLineEndComment(Tree tree) {
        if (tree.firstToken() == null || tree.lastToken() == null) {
            return;
        }
        int firstLineNum = tree.firstToken().line();
        int lastLineNum = tree.lastToken().line();
        if (haveLineEndComment(firstLineNum - 1) && !lineEndList.contains(firstLineNum)) {
            lineEndList.add(firstLineNum);
            reportIssue(tree.firstToken(), MESSAGE);
        }
        if (firstLineNum == lastLineNum) {
            return;
        }
        if (haveLineEndComment(lastLineNum - 1) && !lineEndList.contains(lastLineNum)) {
            lineEndList.add(lastLineNum);
            reportIssue(tree.lastToken(), MESSAGE);
        }
    }

    /**
     * 根据行号，判断是否有行尾注释
     * @param lineNum 行号
     * @return
     */
    private boolean haveLineEndComment(int lineNum) {
        String line = SourceContextUtil.getLine(lineNum, context);
        // 去掉行首和行尾的空白字符
        String trimmedLine = line.trim();
        // 检查是否为行尾注释
        if (isEndWithSlash(trimmedLine) && isCommentNotText(trimmedLine)) {
            return true;
        }
        return false;
    }

    /**
     * 注释符是否文本
     *
     * @param line
     * @return
     */
    private boolean isCommentNotText(String line) {
        int slashAt = line.lastIndexOf(CommentUtil.SINGLE_LINE);
        if (slashAt - 1 < 0) {
            return false;
        }
        String preText = line.substring(slashAt - 1, slashAt);
        if ("\"".equals(preText) || "'".equals(preText)) {
            return false;
        }
        return true;
    }

    /**
     * 是否以单行注释结尾
     *
     * @param line
     * @return
     */
    private boolean isEndWithSlash(String line) {
        return line.contains(CommentUtil.SINGLE_LINE) && !line.startsWith(CommentUtil.SINGLE_LINE);
    }

}