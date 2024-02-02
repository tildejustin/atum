package me.voidxwalker.autoreset;

import com.google.common.collect.Lists;
import me.voidxwalker.autoreset.mixin.hotkey.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

public final class KeyBindingHelper {
    private static final List<KeyBinding> moddedKeyBindings = Lists.newArrayList();
    public static KeyBinding registerKeyBinding(KeyBinding binding) {
        KeyBindingAccessor.getCategories().add(binding.getCategory());
        moddedKeyBindings.add(binding);
        return binding;
    }

    public static KeyBinding[] process(KeyBinding[] keysAll) {
        List<KeyBinding> newKeysAll = Lists.newArrayList(keysAll);
        newKeysAll.removeAll(moddedKeyBindings);
        newKeysAll.addAll(moddedKeyBindings);
        return newKeysAll.toArray(new KeyBinding[0]);
    }
}
