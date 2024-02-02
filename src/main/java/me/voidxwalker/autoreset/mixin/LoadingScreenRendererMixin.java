package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public class LoadingScreenRendererMixin extends Screen {
    @Inject(method = "method_2214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;method_1789(Lnet/minecraft/class_0_681;Ljava/lang/String;III)V", ordinal = 0, shift = At.Shift.AFTER))
    public void modifyString(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty()) {
            String string = Atum.seed;
            this.method_1789(this.field_2554, string, this.field_2561 / 2, 50, 16777215);
        }
    }
}
