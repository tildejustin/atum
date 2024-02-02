package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "method_3774", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;method_0_6455()J", ordinal = 0))
    public void trackWorldGenStart(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.WORLD_GEN;
    }
}
