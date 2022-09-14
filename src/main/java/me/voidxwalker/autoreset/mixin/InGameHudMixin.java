package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.Pingable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper implements Pingable {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = {"method_979"},
            at = {@At("TAIL")}
    )
    private void getRightText(float bl, boolean i, int j, int par4, CallbackInfo ci) {
        if(Atum.isRunning&&client.options.debugEnabled){
            this.drawWithShadow(this.client.textRenderer, "Resetting"+(Atum.seed==null|| Atum.seed.isEmpty()?" a random seed":(" the seed: \""+ Atum.seed+"\"")), 2, 114, 14737632);
        }

    }
    @Override
    public boolean ping() {
        return true;
    }
}