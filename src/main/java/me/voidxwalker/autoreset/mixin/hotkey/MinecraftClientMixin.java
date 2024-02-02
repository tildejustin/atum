package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
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
    public abstract void connect(@Nullable ClientWorld world);

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerNetworkIo;bindLocal()Ljava/net/SocketAddress;", shift = At.Shift.BEFORE))
    public void atum_trackPostWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.POST_WORLDGEN;
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "HEAD"))
    public void atum_trackPreWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.PRE_WORLDGEN;
    }

    @Inject(method = "handleKeyInput", at = @At("HEAD"))
    public void atum_onKey(CallbackInfo ci) {
        if (!Keyboard.isRepeatEvent() && Atum.resetKey.getCode() == Keyboard.getEventKey()) {
            if (this.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen) this.currentScreen).selectedKeyBinding == Atum.resetKey) {
                return;
            }
            Atum.hotkeyPressed = true;
        }
    }

    @Inject(method = "runGameLoop", at = @At(value = "HEAD"), cancellable = true)
    public void atum_tick(CallbackInfo ci) {
        if (Atum.hotkeyPressed) {
            if (Atum.hotkeyState != Atum.HotkeyState.INSIDE_WORLD && Atum.hotkeyState != Atum.HotkeyState.OUTSIDE_WORLD) {
                return;
            }
            Atum.hotkeyPressed = false;
            Atum.isRunning = true;
            if (Atum.hotkeyState == Atum.HotkeyState.INSIDE_WORLD) {
                System.out.println("disconnecting");
                if (this.world != null) {
                    this.world.disconnect();
                }
                System.out.println("connect nulling");
                this.connect(null);
            }
            this.setScreen(new TitleScreen());
            ci.cancel();
        }
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;shouldPauseGame()Z"))
    private boolean donotpaus(Screen instance) {
         return false;
    }
}
