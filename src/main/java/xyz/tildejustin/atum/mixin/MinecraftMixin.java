package xyz.tildejustin.atum.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.world.ClientWorld;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.tildejustin.atum.Atum;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public Screen currentScreen;

    @Inject(
            method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V",
            at = @At(
                    value = "TAIL"
            )
    )
    private void atum$allowResettingAfterWorldJoin(ClientWorld world, String loadingMessage, CallbackInfo ci) {
        if (world != null) {
            Atum.loading = false;
        }
    }

    @Inject(
            method = "runGameLoop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    ordinal = 4
            )
    )
    private void atum$checkHotkey(CallbackInfo ci) {
        if (Keyboard.isKeyDown(Atum.resetKey.code) && !(this.currentScreen instanceof ControlsOptionsScreen)) {
            Atum.tryCreateWorld();
        }
    }
}
