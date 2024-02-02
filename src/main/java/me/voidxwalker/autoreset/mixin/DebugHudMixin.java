package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getRightText", at = @At("RETURN"))
    private void atum_getRightText(CallbackInfoReturnable<List<String>> info) {
        if (Atum.running) {
            List<String> returnValue = info.getReturnValue();
            returnValue.add("Resetting a random seed");
        }
    }
}
