package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.*;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    public abstract void disconnect(Screen screen);

    @Shadow
    @Final
    public Keyboard keyboard;

    @Shadow
    @Final
    private Window window;

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isLoading()Z", shift = At.Shift.AFTER))
    private void resetPreview(CallbackInfo ci) {
        if (Atum.isResetScheduled() && FabricLoader.getInstance().isModLoaded("worldpreview")) {
            keyboard.onKey(this.window.getHandle(), GLFW.GLFW_KEY_ESCAPE, 1, 1, 0);
            this.clickButton(this.currentScreen, "menu.returnToMenu");
        }
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V", shift = At.Shift.AFTER))
    private void executeReset(CallbackInfo ci) {
        while (Atum.shouldReset()) {
            if (this.world != null) {
                Screen gameMenuScreen = new GameMenuScreen(true);
                gameMenuScreen.init((MinecraftClient) (Object) this, 0, 0);
                if (!this.clickButton(gameMenuScreen, "fast_reset.menu.quitWorld", "menu.quitWorld", "menu.returnToMenu", "menu.disconnect") || this.world != null) {
                    if (this.world != null) {
                        this.world.disconnect();
                        this.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                    }
                }
            } else if (Atum.hotkeyState == Atum.HotkeyState.OUTSIDE_WORLD) {
                Atum.resetKey.setPressed(false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                MinecraftClient.getInstance().openScreen(new TitleScreen());
            }
            Atum.createNewWorld();
        }
    }

    @Unique
    private boolean clickButton(Screen screen, String... translationKeys) {
        for (String translationKey : translationKeys) {
            for (Element element : screen.children()) {
                if (!(element instanceof ButtonWidget)) {
                    continue;
                }
                ButtonWidget button = ((ButtonWidget) element);
                if (button.getMessage().equals(new TranslatableText(translationKey).getString()) || button.getMessage().equals(translationKey)) {
                    button.onPress();
                    return true;
                }
            }
        }
        return false;
    }
}
