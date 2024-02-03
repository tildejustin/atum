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
    private MinecraftClient client;

    @Inject(method = "setProgressPercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;method_956(Ljava/lang/String;III)I", ordinal = 1, shift = At.Shift.AFTER))
    public void renderSeed(int percentage, CallbackInfo ci) {
        if (Atum.running && Atum.seed != null && !Atum.seed.isEmpty() && MinecraftClient.getInstance().getServer() != null && MinecraftClient.getInstance().world == null) {
            Window window = new Window(this.client.options, this.client.width, this.client.height);
            int j = window.getWidth();
            int k = window.getHeight();
            String string = Atum.seed;
            this.client.textRenderer.method_956(string, (j - this.client.textRenderer.getStringWidth(string)) / 2, k / 2 - 4 - 40, 0xFFFFFF);
        }
    }
}
