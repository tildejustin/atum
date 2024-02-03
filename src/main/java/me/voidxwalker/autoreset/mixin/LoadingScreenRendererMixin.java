package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin {
    @Shadow
    @Final
    private Window window;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "setProgressPercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 1, shift = At.Shift.AFTER))
    public void renderSeed(int percentage, CallbackInfo ci) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty() && MinecraftClient.getInstance().getServer() != null && MinecraftClient.getInstance().world == null) {
            int j = this.window.getWidth();
            int k = this.window.getHeight();
            String string = Atum.seed;
            this.client.textRenderer.drawWithShadow(string, (float) (j - this.client.textRenderer.getStringWidth(string)) / 2, (float) k / 2 - 4 - 40, 0xFFFFFF);
        }
    }
}
