package me.voidxwalker.autoreset.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Util.class)
public abstract class UtilMixin {
    @Redirect(method = "executeTask", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;fatal(Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 0))
    private static void stopLog(Logger instance, String s, Throwable throwable) {
        if (MinecraftClient.getInstance().getServer() != null && MinecraftClient.getInstance().world == null) {
            return;
        }
        instance.fatal(s, throwable);
    }
}
