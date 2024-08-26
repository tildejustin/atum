package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0), cancellable = true)
    private void atum_onKey(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && Atum.resetKey.matchesKey(key, scancode)) {
            Screen screen = this.client.currentScreen;
            if (screen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen) screen).focusedBinding == Atum.resetKey) return;
            if (screen != null && Atum.disableHotkeyInTextboxes) {
                if (screen.getFocused() instanceof TextFieldWidget && ((TextFieldWidget) screen.getFocused()).isActive()) return;
                // special cases
                if (screen.getFocused() instanceof RecipeBookWidget && ((RecipeBookWidgetAccessor) screen.getFocused()).getSearchField().isFocused()) return;
                if (screen instanceof CreativeInventoryScreen && ((CreativeInventoryScreenAccessor) screen).getSearchBox().isActive()) return;
            }
            Atum.scheduleReset();
            ci.cancel();
        }
    }
}
