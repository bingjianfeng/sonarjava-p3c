package com.elvis.sonar.java.pojo;

import org.sonar.plugins.java.api.JavaCheck;

import java.util.List;

/**
 * @author fengbingjian
 * @description TODO
 * @since 2024/9/27 10:35
 **/
public class RuleCategory {

    public RuleCategory(String categoryName, List<Class<? extends JavaCheck>> ruleList) {
        this.categoryName = categoryName;
        this.ruleList = ruleList;
    }

    /**
     * 规则分类名称
     */
    private String categoryName;

    /**
     * 规则集
     */
    private List<Class<? extends JavaCheck>> ruleList;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Class<? extends JavaCheck>> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Class<? extends JavaCheck>> ruleList) {
        this.ruleList = ruleList;
    }
}