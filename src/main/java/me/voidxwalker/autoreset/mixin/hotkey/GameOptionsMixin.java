package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.KeyBindingHelper;
import net.minecraft.client.options.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Mutable
    @Final
    @Shadow
    public KeyBinding[] keysAll;

    @Inject(at = @At("HEAD"), method = "load()V")
    public void loadHook(CallbackInfo info) {
        keysAll = KeyBindingHelper.process(keysAll);
    }
}
