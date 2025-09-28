package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.*;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0), cancellable = true)
    private void atum_onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && Atum.resetKey.matchesKey(input)) {
            if (this.client.currentScreen instanceof KeybindsScreen && ((KeybindsScreen) this.client.currentScreen).selectedKeyBinding == Atum.resetKey) {
                return;
            }
            Atum.scheduleReset();
            ci.cancel();
        }
    }
}
