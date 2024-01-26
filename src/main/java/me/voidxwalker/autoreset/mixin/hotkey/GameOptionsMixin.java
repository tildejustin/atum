package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.KeyBindingHelper;
import net.minecraft.client.option.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Mutable
    @Shadow
    public KeyBinding[] allKeys;

    @Inject(at = @At("HEAD"), method = "load()V")
    public void loadHook(CallbackInfo info) {
        allKeys = KeyBindingHelper.process(allKeys);
    }
}
