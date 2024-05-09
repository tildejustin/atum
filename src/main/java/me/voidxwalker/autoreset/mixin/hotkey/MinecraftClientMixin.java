package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    public ClientWorld world;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Shadow public abstract void method_1550(ClientWorld clientWorld);

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerNetworkIo;bindLocal()Ljava/net/SocketAddress;", shift = At.Shift.BEFORE))
    public void atum_trackPostWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.POST_WORLDGEN;
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "HEAD"))
    public void atum_trackPreWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.PRE_WORLDGEN;
    }

    @Inject(method = "method_0_2243", at = @At("HEAD"))
    public void atum_onKey(CallbackInfo ci) {
        if (!Keyboard.isRepeatEvent() && Atum.resetKey.method_1421() == Keyboard.getEventKey() && Keyboard.getEventKeyState()) {
            if (this.currentScreen instanceof ControlsOptionsScreen) {
                return;
            }
            Atum.hotkeyPressed = true;
        }
    }

    @Inject(method = "method_0_2281", at = @At(value = "HEAD"), cancellable = true)
    public void atum_tick(CallbackInfo ci) {
        if (Atum.hotkeyPressed) {
            if (Atum.hotkeyState != Atum.HotkeyState.INSIDE_WORLD && Atum.hotkeyState != Atum.HotkeyState.OUTSIDE_WORLD) {
                return;
            }
            Atum.hotkeyPressed = false;
            Atum.isRunning = true;
            if (Atum.hotkeyState == Atum.HotkeyState.INSIDE_WORLD) {
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.method_1550(null);
            }
            this.setScreen(new TitleScreen());
            ci.cancel();
        }
    }
}
