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
package com.elvis.sonar.java.checks.other;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 注意 Math.random() 这个方法返回是double类型，注意取值的范围。
 * @since 2024/9/26 17:35
 **/
class AvoidMissUseOfMathRandomRuleTest {

    @Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/other/AvoidMissUseOfMathRandomRule.java")
                .withCheck(new AvoidMissUseOfMathRandomRule())
                .verifyIssues();
    }

}
