package com.elvis.sonar.java.utils;

import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;

import javax.annotation.CheckForNull;
import java.util.Optional;

/**
 * @author fengbingjian
 * @description TODO
 * @since 2024/9/30 15:19
 **/
public class CustomModifiersUtils {

    public CustomModifiersUtils() {
    }


    public static boolean hasModifier(ModifiersTree modifiers, Modifier expectedModifier) {
        return findModifier(modifiers, expectedModifier).isPresent();
    }

    @CheckForNull
    public static ModifierKeywordTree getModifier(ModifiersTree modifiers, Modifier expectedModifier) {
        return findModifier(modifiers, expectedModifier).orElse(null);
    }

    public static Optional<ModifierKeywordTree> findModifier(ModifiersTree modifiersTree, Modifier expectedModifier) {
        return modifiersTree.modifiers().stream()
                .filter(modifierKeywordTree -> modifierKeywordTree.modifier() == expectedModifier)
                .findAny();
    }

}