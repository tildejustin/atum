package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
    @Inject(method = "method_20373", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;disconnect()V"))
    private void stopResettingOnDeathQuit(CallbackInfo ci) {
        Atum.isRunning = false;
    }
}
