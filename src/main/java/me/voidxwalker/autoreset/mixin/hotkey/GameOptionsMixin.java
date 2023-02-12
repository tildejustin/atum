package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
        @Mutable @Shadow public KeyBinding[] allKeys;

        @Inject(at = @At("HEAD"), method = "load()V")
        public void loadHook(CallbackInfo info) {
            /* move the register here, before this it was running after process(allKeys) and the hotkey wasn't being processed. This is because
               Atum.onInitialize() runs after GameOptions.load() in 1.7 for whatever reason.
             */
            Atum.resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    Atum.getTranslation("key.atum.reset","Create New World").asFormattedString(),
                    64,
                    Atum.getTranslation("key.categories.atum","Atum").asFormattedString()
            ));
            allKeys = KeyBindingHelper.process(allKeys);
        }

}
