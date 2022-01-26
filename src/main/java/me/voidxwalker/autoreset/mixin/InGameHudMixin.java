package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Main;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper implements Pingable {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = {"method_979"},
            at = {@At("TAIL")}
    )
    private void getRightText(float bl, boolean i, int j, int par4, CallbackInfo ci) {
        this.drawWithShadow(this.client.textRenderer, "Resetting "+(Main.seed==null||Main.seed.isEmpty()?" a random seed":(" the seed: \""+Main.seed+"\"")), 2, 140, 14737632);
    }
    @Override
    public boolean ping() {
        return true;
    }
}
