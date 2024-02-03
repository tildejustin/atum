package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.level.LevelGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getRightText", at = @At("RETURN"))
    private void atum_getRightText(CallbackInfoReturnable<List<String>> info) {
        if (Atum.isRunning) {
            List<String> returnValue = info.getReturnValue();
            returnValue.add("Resetting " + (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0") ? "a random seed" : ("the seed: \"" + Atum.seed + "\"")));
            if (Atum.generatorType != 0) {
                returnValue.add("GenType: " + I18n.translate(LevelGeneratorType.TYPES[Atum.generatorType].getTranslationKey()));
            }
            if (!Atum.structures) {
                returnValue.add("NoStructures");
            }
            if (Atum.bonusChest) {
                returnValue.add("BonusChest");
            }
        }
    }
}
