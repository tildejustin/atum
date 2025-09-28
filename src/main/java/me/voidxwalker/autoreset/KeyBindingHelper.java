package me.voidxwalker.autoreset;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

/**
 * Copied from Fabric API
 */
public final class KeyBindingHelper {
    private static final List<KeyBinding> MODDED_KEY_BINDINGS = new ReferenceArrayList<>(); // ArrayList with identity based comparisons for contains/remove/indexOf etc., required for correctly handling duplicate keybinds

    public static KeyBinding registerKeyBinding(KeyBinding binding) {
        if (MinecraftClient.getInstance().options != null) {
            throw new IllegalStateException("GameOptions has already been initialised");
        }

        for (KeyBinding existingKeyBindings : MODDED_KEY_BINDINGS) {
            if (existingKeyBindings == binding) {
                throw new IllegalArgumentException("Attempted to register a key binding twice: " + binding.getId());
            } else if (existingKeyBindings.getId().equals(binding.getId())) {
                throw new IllegalArgumentException("Attempted to register two key bindings with equal ID: " + binding.getId() + "!");
            }
        }

        MODDED_KEY_BINDINGS.add(binding);
        return binding;
    }

    /**
     * Processes the keybindings array for our modded ones by first removing existing modded keybindings and readding them,
     * we can make sure that there are no duplicates this way.
     */
    public static KeyBinding[] process(KeyBinding[] keysAll) {
        List<KeyBinding> newKeysAll = Lists.newArrayList(keysAll);
        newKeysAll.removeAll(MODDED_KEY_BINDINGS);
        newKeysAll.addAll(MODDED_KEY_BINDINGS);
        return newKeysAll.toArray(new KeyBinding[0]);
    }
}
