package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.*;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_0_675.class)
public class LoadingScreenRendererMixin {
    @Shadow
    @Final
    private class_0_686 field_0_2786;

    @Shadow @Final private MinecraftClient field_0_2782;

    @Inject(method = "progressStagePercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_0_681;method_0_2382(Ljava/lang/String;FFI)I", ordinal = 1, shift = At.Shift.AFTER))
    public void renderSeed(int percentage, CallbackInfo ci) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty() && MinecraftClient.getInstance().getServer() != null && MinecraftClient.getInstance().world == null) {
            int j = this.field_0_2786.method_0_2459();
            int k = this.field_0_2786.method_0_2460();
            String string = Atum.seed;
            this.field_0_2782.field_1772.method_0_2382(string, (float) (j - this.field_0_2782.field_1772.method_0_2381(string)) / 2, (float) k / 2 - 4 - 40, 0xFFFFFF);
        }
    }
}
