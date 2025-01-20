package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "method_1040", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(CI)V"), cancellable = true)
    private void checkAtumHotkeyInScreen(CallbackInfo ci) {
        if (Keyboard.getEventKey() != Atum.resetKey.code || Atum.loading) {
            return;
        }
        Atum.shouldReset = true;
        ci.cancel();
    }
}
