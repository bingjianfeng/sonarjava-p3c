package com.elvis.sonar.java.checks.enums;

/**
 * @author fengbingjian
 * @description 注释类型枚举
 * @since 2024/10/9 10:54
 **/
public enum CommentEnum {

    SINGLE_LINE("//", "单行注释"),
    MULTI_LINE("/* ... */", "多行注释"),
    DOCUMENTATION("/** ... */", "文档注释"),
    UNKNOW("", "未知");

    private final String syntax;
    private final String description;

    CommentEnum(String syntax, String description) {
        this.syntax = syntax;
        this.description = description;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getDescription() {
        return description;
    }

}