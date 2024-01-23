package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.*;
import net.minecraft.util.profiler.ProfileResult;
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
    @Nullable
    public Screen currentScreen;

    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    @Final
    private Window window;

    @Shadow
    private @Nullable ProfileResult tickProfilerResult;

    @Shadow
    public abstract void disconnect(Screen screen);

    @Shadow
    public abstract boolean isInSingleplayer();

    @Shadow
    public abstract boolean isConnectedToRealms();

    @Shadow
    public abstract void disconnect();

    @Shadow
    protected abstract boolean shouldMonitorTickDuration();

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerNetworkIo;bindLocal()Ljava/net/SocketAddress;", shift = At.Shift.BEFORE))
    public void atum_trackPostWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.POST_WORLDGEN;
    }

    @Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "HEAD"))
    public void atum_trackPreWorldGen(CallbackInfo ci) {
        Atum.hotkeyState = Atum.HotkeyState.PRE_WORLDGEN;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void atum_tick(CallbackInfo ci) {
        if (Atum.hotkeyPressed) {
            if (Atum.hotkeyState == Atum.HotkeyState.INSIDE_WORLD) {
                Screen s = new GameMenuScreen(true);
                s.init((MinecraftClient) (Object) this, this.window.getScaledWidth(), this.window.getScaledHeight());
                ButtonWidget b = null;
                for (Element e : s.children()) {
                    if (e instanceof ButtonWidget) {
                        if (((ButtonWidget) e).getMessage().equals(new TranslatableText("menu.quitWorld"))) {
                            if (b == null) {
                                b = (ButtonWidget) e;
                            }
                        }
                    }
                }
                Atum.resetKey.setPressed(false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                if (b == null) {
                    boolean bl = this.isInSingleplayer();
                    boolean bl2 = this.isConnectedToRealms();
                    this.world.disconnect();
                    if (bl) {
                        this.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                    } else {
                        this.disconnect();
                    }
                    TitleScreen titleScreen = new TitleScreen();
                    if (bl) {
                        this.setScreen(titleScreen);
                    } else if (bl2) {
                        this.setScreen(new RealmsMainScreen(titleScreen));
                    } else {
                        this.setScreen(new MultiplayerScreen(titleScreen));
                    }
                } else {
                    b.onPress();
                }
            } else if (Atum.hotkeyState == Atum.HotkeyState.OUTSIDE_WORLD) {
                Atum.resetKey.setPressed(false);
                Atum.hotkeyPressed = false;
                Atum.isRunning = true;
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            }
            Atum.createNewWorld();
        }
    }

    @Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/integrated/IntegratedServer;isLoading()Z"), cancellable = true)
    public void atum_tickDuringWorldGen(CallbackInfo ci) {
        if (Atum.hotkeyPressed && Atum.hotkeyState == Atum.HotkeyState.WORLD_GEN) {
            if (currentScreen instanceof LevelLoadingScreen) {

                if (currentScreen.children().isEmpty()) {
                    this.setScreen(new GameMenuScreen(true));
                }
                if (this.clickButton(this.currentScreen, "menu.returnToMenu")) {

                    Atum.resetKey.setPressed(false);
                    Atum.hotkeyPressed = false;
                }
            }
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
