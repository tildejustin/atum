package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Main;
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

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin implements Pingable {
    @Shadow private MinecraftClient field_1029;


    @Shadow private Window field_7695;

    @Inject(method = "progressStagePercentage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;method_956(Ljava/lang/String;III)I", shift = At.Shift.AFTER))
    public void modifyString(int percentage, CallbackInfo ci){
        if(Main.isRunning&&Main.seed!=null&&!Main.seed.isEmpty()){
            int j = field_7695.getWidth();
            int k = field_7695.getHeight();
            String string =Main.seed;
            this.field_1029.textRenderer.draw(string, ((j - this.field_1029.textRenderer.getStringWidth(string)) / 2), (k / 2 - 4 -40), 16777215);
        }

    }
    @Override
    public boolean ping() {
        return true;
    }
}
