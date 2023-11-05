package me.voidxwalker.autoreset.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.profiler.ProfileResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    private @Nullable ProfileResult tickProfilerResult;

    @Shadow
    protected abstract boolean shouldMonitorTickDuration();

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void fixGhostPie(Screen screen, CallbackInfo ci) {
        this.tickProfilerResult = null;
        this.options.debugProfilerEnabled = false;
    }

    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;endMonitor(ZLnet/minecraft/util/TickDurationMonitor;)V"), index = 0)
    private boolean fixGhostPieBlink(boolean active) {
        return active && this.shouldMonitorTickDuration();
    }
}
