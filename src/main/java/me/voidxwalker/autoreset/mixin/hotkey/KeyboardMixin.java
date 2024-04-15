package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class KeyboardMixin {
    @Shadow public Screen currentScreen;

    @Inject(method = "handleKeyInput", at = @At("HEAD"))
    public void atum_onKey(CallbackInfo ci) {
        if (!(currentScreen instanceof ControlsOptionsScreen) && Atum.resetKey.getCode() == Keyboard.getEventKey() && Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent()) {
            Atum.hotkeyPressed = true;
        }
    }
}
