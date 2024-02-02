package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.DifficultyScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DifficultyScreen.class)
public abstract class DifficultyScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    private void createWorld(CallbackInfo ci) {
        Atum.resets++;
        Atum.save();
        MinecraftClient.getInstance().getLevelStorage().deleteLevel("shareware");
        ((DifficultyScreen) (Object) this).new DifficultyButton(0, 0, "", Atum.difficulty, Atum.hardcore).onPress();
    }
}
