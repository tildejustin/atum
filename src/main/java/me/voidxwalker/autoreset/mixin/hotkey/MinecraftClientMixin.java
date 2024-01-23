package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableText;
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


    @Inject(method = "handleKeyInput", at = @At("HEAD"), cancellable = true)
    private void atum_onKey(CallbackInfo ci) {
        if (!Keyboard.isRepeatEvent() && Atum.resetKey.getCode() == Keyboard.getEventKey()) {
            if (this.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen) this.currentScreen).selectedKeyBinding == Atum.resetKey) {
                return;
            }
            Atum.scheduleReset();
            ci.cancel();
        }
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJ)V", shift = At.Shift.AFTER))
    private void executeReset(CallbackInfo ci) {
        while (Atum.shouldReset()) {
            if (this.world != null) {
                Screen gameMenuScreen = new GameMenuScreen();
                gameMenuScreen.init((MinecraftClient) (Object) this, 0, 0);
                if (!this.clickButton(gameMenuScreen, "fast_reset.menu.quitWorld", "menu.quitWorld", "menu.returnToMenu", "menu.disconnect") || this.world != null) {
                    if (this.world != null) {
                        this.world.disconnect();
                        this.connect(null);
                    }
                }
            }
            Atum.createNewWorld();
        }
    }

    @Unique
    private boolean clickButton(Screen screen, String... translationKeys) {
        for (String translationKey : translationKeys) {
            for (ButtonWidget button : ((ScreenAccessor) screen).getButtons()) {
                if (button.message.equals(new TranslatableText(translationKey).asUnformattedString())) {
                    ((ScreenAccessor) screen).callButtonClicked(button);
                    return true;
                }
            }
        }
        return false;
    }
}
