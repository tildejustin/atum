package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public class LoadingScreenRendererMixin extends Screen {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;drawCenteredString(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0, shift = At.Shift.AFTER))
    public void modifyString(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty()) {
            int j = this.width;
            int k = this.height;
            String string = Atum.seed;
            this.drawCenteredString(this.textRenderer, string, j / 2, 50, 16777215);
        }
    }
}
