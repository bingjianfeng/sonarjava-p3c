package com.elvis.sonar.java;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.check.Rule;

public class RuleQualityProfile implements BuiltInQualityProfilesDefinition {

  @Override
  public void define(Context context) {

    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("SonarJava P3C Rules", "java");
    profile.setDefault(true);

    for (Class clazz : RulesList.getJavaChecks()) {
      if (!clazz.isAnnotationPresent(Rule.class)) {
        continue;
      }
      Rule ruleAnnotation = (Rule)clazz.getAnnotation(Rule.class);
      profile.activateRule(JavaRulesDefinition.REPOSITORY_KEY, ruleAnnotation.key());
    }

    profile.done();

  }
}
