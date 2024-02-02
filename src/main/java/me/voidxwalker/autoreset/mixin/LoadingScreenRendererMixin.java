package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LoadingScreenRenderer.class, priority = 1100) // Hello WorldPreview, don't let us get in your way
public class LoadingScreenRendererMixin {
    @Shadow
    private Window field_7695;

    @Shadow
    private MinecraftClient field_1029;

    @Inject(method = "progressStagePercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 1, shift = At.Shift.AFTER))
    public void renderSeed(int percentage, CallbackInfo ci) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty()) {
            int j = this.field_7695.getWidth();
            int k = this.field_7695.getHeight();
            String string = Atum.seed;
            this.field_1029.textRenderer.drawWithShadow(string, (j - this.field_1029.textRenderer.getStringWidth(string)) / 2, k / 2 - 4 - 40, 0xFFFFFF);
        }
    }
}
