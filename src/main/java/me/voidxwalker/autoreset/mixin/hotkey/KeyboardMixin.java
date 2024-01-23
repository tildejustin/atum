package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.*;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"))
    public void atum_onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        if (Atum.resetKey.matchesKey(key, scancode) && !Atum.hotkeyHeld && !(this.client.currentScreen instanceof ControlsOptionsScreen)) {
            Atum.scheduleReset();
            Atum.hotkeyHeld = true;
        } else if (Atum.hotkeyHeld) {
            Atum.hotkeyHeld = false;
        }
    }
}
