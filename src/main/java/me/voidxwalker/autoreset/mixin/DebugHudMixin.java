package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getRightText", at = @At("RETURN"))
    private void getRightText(CallbackInfoReturnable<List<String>> info) {
        if (Atum.isRunning) {
            List<String> returnValue = info.getReturnValue();
            returnValue.add("Resetting " + (Atum.seed == null || Atum.seed.isEmpty() ? "a random seed" : ("the seed: \"" + Atum.seed + "\"")));
            if (Atum.generatorType != 0) {
                returnValue.add(new LiteralText("").append("GenType:").append(GeneratorTypeAccessor.getVALUES().get(Atum.generatorType).getDisplayName()).asString());
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
