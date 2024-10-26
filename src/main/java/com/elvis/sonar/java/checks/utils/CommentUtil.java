package com.elvis.sonar.java.checks.utils;

import com.elvis.sonar.java.checks.enums.CommentEnum;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.Tree;

/**
 * @author fengbingjian
 * @description 注释工具类
 * @since 2024/10/9 10:53
 **/
public class CommentUtil {

    public static final String START = "*";
    public static final String RIGHT_SLASH = "/";
    public static final String SINGLE_LINE = RIGHT_SLASH + RIGHT_SLASH;
    public static final String MULTI_LINE = RIGHT_SLASH + START;
    public static final String DOC_LINE = RIGHT_SLASH + START + START;
    public static final String LINE_END = START + RIGHT_SLASH;


    /**
     * 获取完整的注释内容
     *
     * @param tree    抽象树
     * @param context 上下文
     * @return
     */
    public static String getComment(Tree tree, JavaFileScannerContext context) {
        CommentEnum commentEnum = getCommentType(tree, context);
        if (CommentEnum.UNKNOW.equals(commentEnum)) {
            return null;
        }
        if (CommentEnum.SINGLE_LINE.equals(commentEnum)) {
            return getSingleLineComment(tree, context);
        }
        if (CommentEnum.MULTI_LINE.equals(commentEnum)) {
            return getLineComment(tree, context);
        }
        if (CommentEnum.DOCUMENTATION.equals(commentEnum)) {
            return getLineComment(tree, context);
        }
        return null;
    }

    /**
     * 获取单行注释内容
     *
     * @param tree    抽象树
     * @param context 上下文
     * @return 注释内容
     */
    private static String getSingleLineComment(Tree tree, JavaFileScannerContext context) {
        // 获取注释的最后一行行号
        int commentLastLine = tree.firstToken().line() - 2;
        return SourceContextUtil.getLine(commentLastLine, context);
    }

    /**
     * 获取多行注释内容
     *
     * @param tree    抽象树
     * @param context 上下文
     * @return 注释内容
     */
    private static String getLineComment(Tree tree, JavaFileScannerContext context) {
        // 获取注释的起止行号
        int commentLastLine = tree.firstToken().line() - 2;
        int commentBeginLine = getCommentBeginLine(commentLastLine, context);
        StringBuilder comment = new StringBuilder();
        for (int i = commentBeginLine; i <= commentLastLine; i++) {
            String line = SourceContextUtil.getLine(i, context);
            comment.append(line).append("\n");
        }
        return comment.toString();
    }

    /**
     * 获取注释开始的行号
     *
     * @param commentEndLine 注释结束行号
     * @param context        上下文
     * @return
     */
    private static int getCommentBeginLine(int commentEndLine, JavaFileScannerContext context) {
        int currLine = commentEndLine;
        while (currLine > 0) {
            String line = SourceContextUtil.getLine(currLine, context);
            boolean found = line.trim().startsWith(SINGLE_LINE) || line.trim().startsWith(MULTI_LINE) || line.trim().startsWith(DOC_LINE);
            if (found) {
                return currLine;
            }
            currLine--;
        }
        return 0;
    }

    /**
     * 获取注释类型
     *
     * @param tree    抽象树
     * @param context 上下文
     * @return
     */
    public static CommentEnum getCommentType(Tree tree, JavaFileScannerContext context) {
        if (tree == null) {
            return CommentEnum.UNKNOW;
        }
        // 获取注释的最后一行行号
        int commentLastLine = tree.firstToken().line() - 2;
        return getCommentType(commentLastLine, context);
    }

    /**
     * 获取注释类型
     *
     * @param lastLine 注释的最后一行
     * @param context  上下文
     * @return
     */
    public static CommentEnum getCommentType(int lastLine, JavaFileScannerContext context) {
        if (lastLine <= 0 || context == null) {
            return CommentEnum.UNKNOW;
        }
        String line = SourceContextUtil.getLine(lastLine, context);
        if (line == null || line.trim().isEmpty()) {
            return CommentEnum.UNKNOW;
        }
        if (line.trim().startsWith(SINGLE_LINE)) {
            return CommentEnum.SINGLE_LINE;
        }
        if (!line.trim().startsWith(LINE_END)) {
            return CommentEnum.UNKNOW;
        }
        for (int i = lastLine - 1; i >= 0; i--) {
            line = SourceContextUtil.getLine(i, context);
            boolean isUnknowLine = line == null || line.trim().isEmpty() ||
                    (!line.trim().startsWith(START) && !line.trim().startsWith(RIGHT_SLASH));
            if (isUnknowLine) {
                return CommentEnum.UNKNOW;
            }
            if (line.trim().startsWith(START)) {
                continue;
            }
            if (line.trim().startsWith(DOC_LINE)) {
                return CommentEnum.DOCUMENTATION;
            }
            if (line.trim().startsWith(MULTI_LINE)) {
                return CommentEnum.MULTI_LINE;
            }
        }
        return CommentEnum.UNKNOW;
    }

}