package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class DebugHudMixin extends DrawableHelper {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "method_979", at = @At("TAIL"))
    private void getRightText(float bl, boolean i, int j, int par4, CallbackInfo ci) {
        if (Atum.running && client.options.debugEnabled) {
            this.drawCenteredString(this.client.textRenderer, "Resetting" + (Atum.seed == null || Atum.seed.isEmpty() ? " a random seed" : (" the seed: \"" + Atum.seed + "\"")), 2, 114, 14737632);
        }
    }
}
