/*
 * SonarQube Java
 * Copyright (C) 2012-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.elvis.sonar.java;

import com.elvis.sonar.java.checks.comment.AbstractMethodOrInterfaceMethodMustUseJavadocRule;
import com.elvis.sonar.java.checks.comment.AvoidCommentBehindStatementRule;
import com.elvis.sonar.java.checks.comment.ClassMustHaveAuthorRule;
import com.elvis.sonar.java.checks.comment.CommentsMustBeJavadocFormatRule;
import com.elvis.sonar.java.checks.concurrent.AvoidCallStaticSimpleDateFormatRule;
import com.elvis.sonar.java.checks.concurrent.AvoidConcurrentCompetitionRandomRule;
import com.elvis.sonar.java.checks.concurrent.AvoidManuallyCreateThreadRule;
import com.elvis.sonar.java.checks.concurrent.AvoidUseTimerRule;
import com.elvis.sonar.java.checks.concurrent.CountDownShouldInFinallyRule;
import com.elvis.sonar.java.checks.concurrent.LockShouldWithTryFinallyRule;
import com.elvis.sonar.java.checks.concurrent.ThreadLocalShouldRemoveRule;
import com.elvis.sonar.java.checks.concurrent.ThreadPoolCreationRule;
import com.elvis.sonar.java.checks.constant.UndefineMagicConstantRule;
import com.elvis.sonar.java.checks.constant.UpperEllRule;
import com.elvis.sonar.java.checks.exception.AvoidReturnInFinallyRule;
import com.elvis.sonar.java.checks.exception.MethodReturnWrapperTypeRule;
import com.elvis.sonar.java.checks.exception.TransactionMustHaveRollbackRule;
import com.elvis.sonar.java.checks.flowcontrol.AvoidComplexConditionRule;
import com.elvis.sonar.java.checks.flowcontrol.AvoidNegationOperatorRule;
import com.elvis.sonar.java.checks.flowcontrol.NeedBraceRule;
import com.elvis.sonar.java.checks.flowcontrol.SwitchStatementRule;
import com.elvis.sonar.java.checks.naming.AbstractClassShouldStartWithAbstractNamingRule;
import com.elvis.sonar.java.checks.naming.ArrayNamingShouldHaveBracketRule;
import com.elvis.sonar.java.checks.naming.AvoidStartWithDollarAndUnderLineNamingRule;
import com.elvis.sonar.java.checks.naming.BooleanPropertyShouldNotStartWithIsRule;
import com.elvis.sonar.java.checks.naming.ClassAvoidStartWithDollarAndUnderLineNamingRule;
import com.elvis.sonar.java.checks.naming.ClassNamingShouldBeCamelRule;
import com.elvis.sonar.java.checks.naming.ConstantFieldShouldBeUpperCaseRule;
import com.elvis.sonar.java.checks.naming.ExceptionClassShouldEndWithExceptionRule;
import com.elvis.sonar.java.checks.naming.LowerCamelCaseVariableNamingRule;
import com.elvis.sonar.java.checks.naming.PackageNamingRule;
import com.elvis.sonar.java.checks.naming.ServiceOrDaoClassShouldEndWithImplRule;
import com.elvis.sonar.java.checks.naming.TestClassShouldEndWithTestNamingRule;
import com.elvis.sonar.java.checks.oop.BigDecimalAvoidDoubleConstructorRule;
import com.elvis.sonar.java.checks.oop.EqualsAvoidNullRule;
import com.elvis.sonar.java.checks.oop.PojoMustOverrideToStringRule;
import com.elvis.sonar.java.checks.oop.PojoMustUsePrimitiveFieldRule;
import com.elvis.sonar.java.checks.oop.PojoNoDefaultValueRule;
import com.elvis.sonar.java.checks.oop.StringConcatRule;
import com.elvis.sonar.java.checks.oop.WrapperTypeEqualityRule;
import com.elvis.sonar.java.checks.orm.AvoidDirectJdbcDriverRule;
import com.elvis.sonar.java.checks.other.AvoidApacheBeanUtilsCopyRule;
import com.elvis.sonar.java.checks.other.AvoidDoubleOrFloatEqualCompareRule;
import com.elvis.sonar.java.checks.other.AvoidMissUseOfMathRandomRule;
import com.elvis.sonar.java.checks.other.AvoidNewDateGetTimeRule;
import com.elvis.sonar.java.checks.other.AvoidPatternCompileInMethodRule;
import com.elvis.sonar.java.checks.other.MethodTooLongRule;
import com.elvis.sonar.java.checks.set.ClassCastExceptionWithSubListToArrayListRule;
import com.elvis.sonar.java.checks.set.ClassCastExceptionWithToArrayRule;
import com.elvis.sonar.java.checks.set.CollectionInitShouldAssignCapacityRule;
import com.elvis.sonar.java.checks.set.ConcurrentExceptionWithModifyOriginSubListRule;
import com.elvis.sonar.java.checks.set.DontModifyInForeachCircleRule;
import com.elvis.sonar.java.checks.set.UnsupportedExceptionWithModifyAsListRule;
import com.elvis.sonar.java.pojo.RuleCategory;
import org.sonar.plugins.java.api.JavaCheck;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class RulesList {

    private RulesList() {
    }

    public static List<Class<? extends JavaCheck>> getChecks() {
        List<Class<? extends JavaCheck>> checks = merge(getJavaChecks(),
                getJavaTestChecks());
        return Collections.unmodifiableList(checks);
    }

    public static List<Class<? extends JavaCheck>> getJavaChecks() {
        return Collections.unmodifiableList(getJavaRulesCategory().stream()
                .map(RuleCategory::getRuleList)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    public static List<RuleCategory> getJavaRulesCategory() {
        return Arrays.asList(getNamingCheck(),
                getCommentCheck(),
                getConcurrentCheck(),
                getConstantCheck(),
                getExceptionCheck(),
                getFlowControlCheck(),
                getOopCheck(),
                getOrmCheck(),
                getSetCheck(),
                getOtherCheck());
    }

    public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
        return Collections.emptyList();
    }

    /**
     * 命名规范的规则集
     *
     * @return
     */
    public static RuleCategory getNamingCheck() {
        return new RuleCategory("naming",
                Collections.unmodifiableList(Arrays.asList(
                        AbstractClassShouldStartWithAbstractNamingRule.class,
                        ArrayNamingShouldHaveBracketRule.class,
                        AvoidStartWithDollarAndUnderLineNamingRule.class,
                        ClassAvoidStartWithDollarAndUnderLineNamingRule.class,
                        BooleanPropertyShouldNotStartWithIsRule.class,
                        ClassNamingShouldBeCamelRule.class,
                        ConstantFieldShouldBeUpperCaseRule.class,
                        ExceptionClassShouldEndWithExceptionRule.class,
                        PackageNamingRule.class,
                        ServiceOrDaoClassShouldEndWithImplRule.class,
                        TestClassShouldEndWithTestNamingRule.class,
                        LowerCamelCaseVariableNamingRule.class)));
    }

    /**
     * 注释类规则集
     *
     * @return
     */
    public static RuleCategory getCommentCheck() {
        return new RuleCategory("comment",
                Collections.unmodifiableList(Arrays.asList(
                        CommentsMustBeJavadocFormatRule.class,
                        AvoidCommentBehindStatementRule.class,
                        AbstractMethodOrInterfaceMethodMustUseJavadocRule.class,
                        ClassMustHaveAuthorRule.class)));
    }

    /**
     * 并发类规则集
     *
     * @return
     */
    public static RuleCategory getConcurrentCheck() {
        return new RuleCategory("concurrent",
                Collections.unmodifiableList(Arrays.asList(
                        AvoidUseTimerRule.class,
                        AvoidCallStaticSimpleDateFormatRule.class,
                        AvoidConcurrentCompetitionRandomRule.class,
                        AvoidManuallyCreateThreadRule.class,
                        CountDownShouldInFinallyRule.class,
                        ThreadLocalShouldRemoveRule.class,
                        ThreadPoolCreationRule.class,
                        LockShouldWithTryFinallyRule.class)));
    }

    /**
     * 常量类规则集
     *
     * @return
     */
    public static RuleCategory getConstantCheck() {
        return new RuleCategory("constant",
                Collections.unmodifiableList(Arrays.asList(
                        UndefineMagicConstantRule.class,
                        UpperEllRule.class)));
    }

    /**
     * 异常类规则集
     *
     * @return
     */
    public static RuleCategory getExceptionCheck() {
        return new RuleCategory("exception",
                Collections.unmodifiableList(Arrays.asList(
                        AvoidReturnInFinallyRule.class,
                        MethodReturnWrapperTypeRule.class,
                        TransactionMustHaveRollbackRule.class)));
    }

    /**
     * 流程/分支/判断规则集
     *
     * @return
     */
    public static RuleCategory getFlowControlCheck() {
        return new RuleCategory("flowcontrol",
                Collections.unmodifiableList(Arrays.asList(
                        AvoidNegationOperatorRule.class,
                        SwitchStatementRule.class,
                        NeedBraceRule.class,
                        AvoidComplexConditionRule.class)));
    }

    /**
     * 面向对象规范规则集
     *
     * @return
     */
    public static RuleCategory getOopCheck() {
        return new RuleCategory("oop",
                Collections.unmodifiableList(Arrays.asList(
                        EqualsAvoidNullRule.class,
                        WrapperTypeEqualityRule.class,
                        PojoMustUsePrimitiveFieldRule.class,
                        PojoNoDefaultValueRule.class,
                        PojoMustOverrideToStringRule.class,
                        StringConcatRule.class,
                        BigDecimalAvoidDoubleConstructorRule.class)));
    }


    /**
     * ORM框架使用规范规则集
     *
     * @return
     */
    public static RuleCategory getOrmCheck() {
        return new RuleCategory("orm",
                Collections.unmodifiableList(Arrays.asList(
                        AvoidDirectJdbcDriverRule.class)));
    }

    /**
     * 集合使用规范规则集
     *
     * @return
     */
    public static RuleCategory getSetCheck() {
        return new RuleCategory("set",
                Collections.unmodifiableList(Arrays.asList(
                        ClassCastExceptionWithSubListToArrayListRule.class,
                        ClassCastExceptionWithToArrayRule.class,
                        CollectionInitShouldAssignCapacityRule.class,
                        ConcurrentExceptionWithModifyOriginSubListRule.class,
                        DontModifyInForeachCircleRule.class,
                        UnsupportedExceptionWithModifyAsListRule.class)));
    }

    /**
     * 其他规则集
     *
     * @return
     */
    public static RuleCategory getOtherCheck() {
        return new RuleCategory("other",
                Collections.unmodifiableList(Arrays.asList(
                        MethodTooLongRule.class,
                        AvoidPatternCompileInMethodRule.class,
                        AvoidApacheBeanUtilsCopyRule.class,
                        AvoidNewDateGetTimeRule.class,
                        AvoidMissUseOfMathRandomRule.class,
                        AvoidDoubleOrFloatEqualCompareRule.class)));
    }

    private static List<Class<? extends JavaCheck>> merge(RuleCategory... categories) {
        return Arrays.stream(categories)
                .map(RuleCategory::getRuleList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static List<Class<? extends JavaCheck>> merge(List<Class<? extends JavaCheck>>... collections) {
        return Arrays.stream(collections)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
