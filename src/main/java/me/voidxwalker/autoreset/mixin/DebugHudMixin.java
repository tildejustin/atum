package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getRightText", at = @At(value = "RETURN"))
    private void modifyRightText(CallbackInfoReturnable<List<String>> info) {
        if (Atum.running) {
            List<String> returnValue = info.getReturnValue();
            Language language = Language.getInstance();
            returnValue.add(AtumConfig.instance.seed.isEmpty() ? language.get("seed.random") : new TranslatableText("seed.set", AtumConfig.instance.seed).getString());
            if (AtumConfig.instance.generatorType != AtumConfig.AtumGeneratorType.DEFAULT)
                returnValue.add(language.get("selectWorld.mapType") + " " + AtumConfig.instance.generatorType.get().getTranslationKey().getString());
            if (!AtumConfig.instance.structures) returnValue.add(language.get("selectWorld.mapFeatures") + " " + language.get("gui.no"));
            if (AtumConfig.instance.bonusChest) returnValue.add(language.get("selectWorld.bonusItems") + " " + language.get("gui.yes"));
        }
    }
}
