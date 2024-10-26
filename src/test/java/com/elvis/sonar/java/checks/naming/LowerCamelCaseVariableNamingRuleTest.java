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
package com.elvis.sonar.java.checks.naming;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 方法名、参数名、成员变量、局部变量都统一使用lowerCamelCase，必须遵从驼峰形式
 * @since 2024/9/26 17:35
 **/
class LowerCamelCaseVariableNamingRuleTest {

    @Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/naming/LowerCamelCaseVariableNamingRule.java")
                .withCheck(new LowerCamelCaseVariableNamingRule())
                .verifyIssues();
    }

}
