package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.*;
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
            /* move the register here, before this it was running after process(allKeys) and the hotkey wasn't being processed. This is because
               Atum.onInitialize() runs after GameOptions.load() in 1.7 for whatever reason.
             */
        Atum.resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                Atum.getTranslation("key.atum.reset", "Create New World").asFormattedString(),
                64,
                Atum.getTranslation("key.categories.atum", "Atum").asFormattedString()
        ));
        allKeys = KeyBindingHelper.process(allKeys);
    }
}
