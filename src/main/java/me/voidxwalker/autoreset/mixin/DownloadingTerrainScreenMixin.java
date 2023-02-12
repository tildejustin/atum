package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public abstract class DownloadingTerrainScreenMixin extends Screen {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;drawCenteredString(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", shift = At.Shift.AFTER))
    private void renderSeed(int mouseY, int tickDelta, float par3, CallbackInfo ci) {
        if(Atum.isRunning&& Atum.seed!=null&&!Atum.seed.isEmpty()){
            String string = Atum.seed;
            this.drawCenteredString(this.textRenderer, string, this.width / 2, this.height / 2 - 74, 0xFFFFFF);}
        }

}
