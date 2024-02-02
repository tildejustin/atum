package me.voidxwalker.autoreset.mixin.hotkey;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    // @Inject(method = "prepareWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getTimeMillis()J", ordinal = 0))
    // public void trackWorldGenStart(CallbackInfo ci) {
    //     Atum.hotkeyState = Atum.HotkeyState.WORLD_GEN;
    // }
}
