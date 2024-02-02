package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class KeyboardMixin {
    @Shadow
    public Screen currentScreen;

    @Unique
    long atum_lastHeld = 0;

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=keyboard"))
    public void atum_onKey(CallbackInfo ci) {
        if (System.currentTimeMillis() - atum_lastHeld > 1000) {
            Atum.hotkeyHeld = false;
        }
        if (Keyboard.isKeyDown(Atum.resetKey.getCode()) && !(this.currentScreen instanceof ControlsOptionsScreen) && !Atum.hotkeyHeld) {
            Atum.hotkeyHeld = true;
            atum_lastHeld = System.currentTimeMillis();
            KeyBinding.setKeyPressed(Atum.resetKey.getCode(), true);
            Atum.hotkeyPressed = true;
        } else {
            Atum.hotkeyHeld = false;
        }
    }
}
