package me.voidxwalker.autoreset.mixin.hotkey;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("categories")
    static Set<String> invokeGetCategoryMap() {
        throw new AssertionError();
    }
}
