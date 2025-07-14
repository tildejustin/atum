package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "prepareWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getTimeMillis()J", ordinal = 0))
    public void trackWorldGenStart(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.WORLD_GEN;
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void addServer(CallbackInfo ci) {
        synchronized (Atum.blocker) {
            Atum.serversAlive++;
        }
    }

    @Inject(method = "run", at = @At("TAIL"))
    private void removeServer(CallbackInfo ci) {
        synchronized (Atum.blocker) {
            Atum.serversAlive--;
            Atum.blocker.notify();
        }
    }
}
