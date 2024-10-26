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
package com.elvis.sonar.java.checks.oop;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description Object的equals方法容易抛空指针异常，应使用常量或确定有值的对象来调用equals。
 * @since 2024/9/26 17:35
 **/
class EqualsAvoidNullRuleTest {

    @Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/oop/EqualsAvoidNullRule.java")
                .withCheck(new EqualsAvoidNullRule())
                .verifyIssues();
    }

}
