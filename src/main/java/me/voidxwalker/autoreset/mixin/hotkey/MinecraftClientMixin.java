package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    public abstract void connect(@Nullable ClientWorld world);

    @Shadow
    public abstract void openScreen(Screen screen);

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerNetworkIo;bindLocal()Ljava/net/SocketAddress;", shift = At.Shift.BEFORE))
    public void atum_trackPostWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.POST_WORLDGEN;
    }

    @Inject(method = "startGame", at = @At(value = "HEAD"))
    public void atum_trackPreWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.PRE_WORLDGEN;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void atum_tick(CallbackInfo ci) {
        if (Atum.hotkeyPressed) {
            if (Atum.hotkeyState == Atum.HotkeyState.INSIDE_WORLD || Atum.hotkeyState == Atum.HotkeyState.POST_WORLDGEN) {
                if (Atum.HAS_ANCHIALE) {
                    AnchialeAPI.setAnchialeFastReset(true);
                }
                KeyBinding.setKeyPressed(Atum.resetKey.getCode(), false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                Atum.loopPrevent2 = true;
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.connect(null);
                this.openScreen(null);
                if (Atum.HAS_ANCHIALE) {
                    AnchialeAPI.setAnchialeFastReset(false);
                }
            } else if (Atum.hotkeyState == Atum.HotkeyState.OUTSIDE_WORLD) {
                KeyBinding.setKeyPressed(Atum.resetKey.getCode(), false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                MinecraftClient.getInstance().openScreen(new TitleScreen());
            }
        }
    }
}
