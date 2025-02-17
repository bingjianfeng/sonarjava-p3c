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

import com.elvis.sonar.java.log.utils.LogUtil;
import org.sonar.api.Plugin;

/**
 * Entry point of your plugin containing your custom rules
 */
public class PluginApplication implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtension(JavaRulesDefinition.class);
        context.addExtension(RuleQualityProfile.class);
        context.addExtension(JavaFileCheckRegistrar.class);
        initLogUtil(context);
    }

    /**
     * 初始化日志记录器
     * @param context
     */
    private void initLogUtil(Context context){
        LogUtil.setProduct(context.getRuntime().getProduct());
        LogUtil.initial();
    }
}
