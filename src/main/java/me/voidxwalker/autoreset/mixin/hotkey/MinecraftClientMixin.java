package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public ClientWorld world;

    @Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "TAIL"))
    private void allowResettingAfterWorldJoin(ClientWorld world, String loadingMessage, CallbackInfo ci) {
        if (world != null) {
            Atum.loading = false;
        }
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 4))    public void atum_tick(CallbackInfo ci) {
        if (!Keyboard.isKeyDown(Atum.resetKey.code) || Atum.loading) {
            return;
        }
        Atum.hotkeyPressed = false;
        Atum.running = true;
        if (world != null) {
            MinecraftClient.getInstance().world.disconnect();
            MinecraftClient.getInstance().connect(null);
        }
        MinecraftClient.getInstance().setScreen(null);
    }
}
