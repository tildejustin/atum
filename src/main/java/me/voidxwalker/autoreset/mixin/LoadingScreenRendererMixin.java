package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LoadingScreenRenderer.class)
public abstract class LoadingScreenRendererMixin {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "setProgressPercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;method_956(Ljava/lang/String;III)I", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderSeed(int percentage, CallbackInfo ci, long time, Window window, int width, int height) {
        if (Atum.running && !Atum.seed.isEmpty()) {
            this.client.textRenderer.method_956(Atum.seed, (width - this.client.textRenderer.getStringWidth(Atum.seed)) / 2, height / 2 - 4 - 16 - 24, 0xFFFFFF);
        }
    }
}
