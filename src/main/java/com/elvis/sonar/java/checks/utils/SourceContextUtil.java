package com.elvis.sonar.java.checks.utils;

import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 源码阅读解释器
 *
 * @author fengbingjian
 * @description
 * @since 2024/10/3 1:22
 **/
public class SourceContextUtil {

    /**
     * 获取源代码
     *
     * @param context 上下文
     * @return
     */
    public static List<String> getContext(JavaFileScannerContext context) {
        return getContext(context, false, false);
    }

    /**
     * 获取源代码
     *
     * @param context           上下文
     * @param filterEmptyLine   是否过滤空行
     * @param filterCommentLine 是否过滤注释行
     * @return
     */
    public static List<String> getContext(JavaFileScannerContext context, boolean filterEmptyLine, boolean filterCommentLine) {
        if (!filterEmptyLine && !filterCommentLine) {
            return context.getFileLines();
        }
        List<String> newList = new ArrayList<>();
        for (String line : context.getFileLines()) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            if (filterCommentLine && isCommentLine(line)) {
                continue;
            }
            newList.add(line);
        }
        return newList;
    }

    /**
     * 获取指定片区内容
     *
     * @param tree   指定代码区域
     * @param context 上下文
     * @return
     */
    public static String getLineString(Tree tree, JavaFileScannerContext context) {
        List<String> resultList = getLine(tree, context);
        if(resultList.isEmpty()){
            return null;
        }
        return String.join("\n", resultList);
    }

    /**
     * 获取指定片区内容
     *
     * @param tree   指定代码区域
     * @param context 上下文
     * @return
     */
    public static List<String> getLine(Tree tree, JavaFileScannerContext context) {
        List<String> resultList = new ArrayList<>();
        List<String> sourceList = getContext(context);
        if (tree == null || sourceList == null || sourceList.size() <= tree.firstToken().line()) {
            return resultList;
        }
        int firstLine = tree.firstToken().line() - 1;
        int lastLine = tree.lastToken().line() - 1;
        if (firstLine == lastLine) {
            resultList.add(sourceList.get(firstLine));
            return resultList;
        }
        for (int i = firstLine; i <= lastLine; i++) {
            resultList.add(sourceList.get(i));
        }
        return resultList;
    }

    /**
     * 根据行号获取内容
     *
     * @param line    指定的行数
     * @param context 上下文
     * @return
     */
    public static String getLine(int line, JavaFileScannerContext context) {
        List<String> sourceList = getContext(context);
        if (sourceList == null || sourceList.size() <= line) {
            return null;
        }
        return sourceList.get(line);
    }

    /**
     * 判断指定行数的下一行代码，是否存在关键字
     *
     * @param context         上下文
     * @param line            指定的行数
     * @param keyword         下一行应该出现的关键字
     * @param filterEmptyLine 是否过滤空行
     * @param filterComment   是否过滤注释
     * @return
     */
    public static boolean keywordInNextLine(JavaFileScannerContext context, int line, String keyword, boolean filterEmptyLine, boolean filterComment) {
        List<String> sourceList = getContext(context);
        if (sourceList == null || sourceList.size() <= line) {
            return false;
        }
        for (int i = line; i < sourceList.size(); i++) {
            String lineStr = sourceList.get(i);
            boolean isMatch = lineStr.replaceAll(" ", "").indexOf(keyword.replaceAll(" ", "")) >= 0;
            if (isMatch) {
                return true;
            }
            if (filterEmptyLine && isEmptyLine(lineStr)) {
                continue;
            } else if (filterComment && isCommentLine(lineStr)) {
                continue;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断指定行数的上一行代码，是否存在关键字
     *
     * @param context         上下文
     * @param line            指定的行数
     * @param keyword         下一行应该出现的关键字
     * @param filterEmptyLine 是否过滤空行
     * @param filterComment   是否过滤注释
     * @return
     */
    public static boolean keywordInPreLine(JavaFileScannerContext context, int line, String keyword, boolean filterEmptyLine, boolean filterComment) {
        List<String> sourceList = getContext(context);
        int beginLine = line - 2;
        if (sourceList == null || sourceList.size() == 0 || beginLine < 0) {
            return false;
        }
        for (int i = beginLine; i >= 0; i--) {
            String lineStr = sourceList.get(i);
            boolean isMatch = lineStr.replaceAll(" ", "").indexOf(keyword.replaceAll(" ", "")) >= 0;
            if (isMatch) {
                return true;
            }
            if (filterEmptyLine && isEmptyLine(lineStr)) {
                continue;
            } else if (filterComment && isCommentLine(lineStr)) {
                continue;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否为空行
     *
     * @param line
     * @return
     */
    public static boolean isEmptyLine(String line) {
        return line.trim().isEmpty();
    }

    /**
     * 判断是否为注释行
     *
     * @param line
     * @return
     */
    public static boolean isCommentLine(String line) {
        return line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().startsWith("*");
    }
}