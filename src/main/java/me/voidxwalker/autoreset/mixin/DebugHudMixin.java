package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.text.Text;
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
                String s = Atum.getGeneratorTypeString(Atum.generatorType);
                if (s != null) {
                    returnValue.add(Text.literal("GenType:").append(s).getString());
                }
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
