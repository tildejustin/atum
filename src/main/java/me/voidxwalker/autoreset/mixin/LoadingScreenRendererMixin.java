package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.Pingable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LoadingScreenRenderer.class, priority = 1100) // Hello WorldPreview, don't let us get in your way
public class LoadingScreenRendererMixin implements Pingable  {

    @Shadow private MinecraftClient client;

    @Shadow private Window window;

    @Inject(method = "setProgressPercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;method_956(Ljava/lang/String;III)I", ordinal = 1, shift = At.Shift.AFTER))
    public void renderSeed(int percentage, CallbackInfo ci){
        if(Atum.isRunning&& Atum.seed!=null&&!Atum.seed.isEmpty()){
            int j = this.window.getWidth();
            int k = this.window.getHeight();
            String string = Atum.seed;
            this.client.textRenderer.method_956(string, (j - this.client.textRenderer.getStringWidth(string)) / 2, k / 2 - 4 - 40, 0xFFFFFF);
        }
    }

    @Override
    public boolean ping() {
        return true;
    }
}
