package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.class_4110;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_4110.class)
public abstract class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient field_19926;

    @Inject(method = "method_18182", at = @At(value = "FIELD", target = "Lnet/minecraft/class_4110;field_19928:J", ordinal = 0), cancellable = true)
    private void atum_onKey(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && Atum.resetKey.method_18166(key, scancode)) {
            if (this.field_19926.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen) this.field_19926.currentScreen).selectedKeyBinding == Atum.resetKey) {
                return;
            }
            Atum.scheduleReset();
            ci.cancel();
        }
    }
}
