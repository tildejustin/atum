package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public ClientWorld world;

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerNetworkIo;bindLocal()Ljava/net/SocketAddress;", shift = At.Shift.BEFORE))
    public void atum_trackPostWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.POST_WORLDGEN;
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "HEAD"))
    public void atum_trackPreWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.PRE_WORLDGEN;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void atum_tick(CallbackInfo ci) {
        if (Atum.hotkeyPressed) {
            if (Atum.hotkeyState == Atum.HotkeyState.INSIDE_WORLD || Atum.hotkeyState == Atum.HotkeyState.POST_WORLDGEN) {
                KeyBinding.setKeyPressed(Atum.resetKey.getCode(), false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                if (world != null) {
                    MinecraftClient.getInstance().world.disconnect();
                }
                MinecraftClient.getInstance().connect(null);
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            } else if (Atum.hotkeyState == Atum.HotkeyState.OUTSIDE_WORLD) {
                KeyBinding.setKeyPressed(Atum.resetKey.getCode(), false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            }
        }
    }
}
