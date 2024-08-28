package me.voidxwalker.autoreset.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawCenteredString(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void drawSeedString(LevelLoadingScreen screen, MatrixStack matrixStack, TextRenderer textRenderer, String s, int x, int y, int color, Operation<Void> original) {
        original.call(screen, matrixStack, textRenderer, s, x, y, color);
        if (Atum.isRunning() && Atum.isSetSeed()) {
            screen.drawCenteredString(matrixStack, textRenderer, Atum.inDemoMode() ? "North Carolina" : Atum.config.seed, x, y - 20, color);
        }
    }
}
