package me.voidxwalker.autoreset.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    // We could add an actual debug category, but that makes the section disableable, and involves a bunch more work to prevent that.
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;getWorld()Lnet/minecraft/world/World;", shift = At.Shift.AFTER))
    private void addDebugInfo(DrawContext context, CallbackInfo ci, @Local DebugHudLines lines) {
        if (Atum.isRunning && MinecraftClient.getInstance().debugHudEntryList.isF3Enabled()) {
            lines.addLineToSection(Atum.DEBUG_SECTION_IDENTIFIER, "Resetting " + (Atum.seed == null || Atum.seed.isEmpty() ? "a random seed" : "the seed: \"" + Atum.seed + "\"") + ", " + (Atum.difficulty != -1 ? "" + Difficulty.byId(Atum.difficulty).getName().charAt(0) : "hc"));
            if (Atum.generatorType != 0) {
                String s = Atum.getGeneratorTypeString(Atum.generatorType);
                if (s != null) {
                    lines.addLineToSection(Atum.DEBUG_SECTION_IDENTIFIER, Text.literal("GenType: ").append(s).getString());
                }
            }
            if (!Atum.structures) {
                lines.addLineToSection(Atum.DEBUG_SECTION_IDENTIFIER, "NoStructures");
            }
            if (Atum.bonusChest) {
                lines.addLineToSection(Atum.DEBUG_SECTION_IDENTIFIER, "BonusChest");
            }
        }
    }
}
