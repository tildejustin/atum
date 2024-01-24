package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.*;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.*;
import net.minecraft.util.profiler.ProfileResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public abstract void disconnect(Screen screen);

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    private @Nullable ProfileResult tickProfilerResult;

    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    protected abstract boolean shouldMonitorTickDuration();

    @Shadow
    @Final
    public Keyboard keyboard;

    @Shadow
    @Final
    private Window window;

    @Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isLoading()Z", shift = At.Shift.AFTER))
    private void resetPreview(CallbackInfo ci) {
        if (Atum.isResetScheduled() && FabricLoader.getInstance().isModLoaded("worldpreview")) {
            this.keyboard.onKey(this.window.getHandle(), GLFW.GLFW_KEY_ESCAPE, 1, 0, 0);
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
            }
            Atum.createNewWorld();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void fixGhostPie(Screen screen, CallbackInfo ci) {
        this.tickProfilerResult = null;
        this.options.debugProfilerEnabled = false;
    }

    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;endMonitor(ZLnet/minecraft/util/TickDurationMonitor;)V"), index = 0)
    private boolean fixGhostPieBlink(boolean active) {
        return active && this.shouldMonitorTickDuration();
    }

    @Unique
    private boolean clickButton(Screen screen, String... translationKeys) {
        for (String translationKey : translationKeys) {
            for (Element element : screen.children()) {
                if (!(element instanceof ButtonWidget)) {
                    continue;
                }
                ButtonWidget button = ((ButtonWidget) element);
                Text text = button.getMessage();
                if (text instanceof TranslatableText && ((TranslatableText) text).getKey().equals(translationKey)) {
                    button.onPress();
                    return true;
                }
            }
        }
        return false;
    }
}
