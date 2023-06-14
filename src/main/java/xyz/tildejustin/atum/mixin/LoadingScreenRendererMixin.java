package xyz.tildejustin.atum.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.tildejustin.atum.Atum;

@Mixin(LoadingScreenRenderer.class)
public abstract class LoadingScreenRendererMixin {
    @Shadow
    private Minecraft client;

    @Inject(
            method = "setProgressPercentage",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;method_956(Ljava/lang/String;III)I",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void renderSeed(int percentage, CallbackInfo ci, long time, Window window, int width, int height) {
        if (Atum.running && !Atum.config.seed.isEmpty()) {
            this.client.textRenderer.method_956(Atum.config.seed, (width - this.client.textRenderer.getStringWidth(Atum.config.seed)) / 2, height / 2 - 4 - 16 - 24, 0xFFFFFF);
        }
    }
}
