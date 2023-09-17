package xyz.tildejustin.atum.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.tildejustin.atum.Atum;
import xyz.tildejustin.atum.screen.ConfigScreen;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Inject(method = "init", at = @At(value = "HEAD"))
    private void reset(CallbackInfo ci) {
        if (Atum.running) {
            disableAllButtons();
            Atum.tryCreateWorld();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At(value = "TAIL"))
    private void addTitleScreenButton(CallbackInfo ci) {
        this.buttons.add(new ButtonWidget(13, this.width / 2 - 124, this.height / 4 + 48, 20, 20, ""));
    }

    @Inject(method = "buttonClicked", at = @At(value = "TAIL"))
    private void startResetsOnClick(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 13) {
            if (!Screen.hasShiftDown()) {
                Minecraft minecraft = Minecraft.getMinecraft();
                minecraft.openScreen(new ConfigScreen(this, Atum.config));
            } else {
                disableAllButtons();
                Atum.tryCreateWorld();
            }
        }
    }

    @Inject(method = "buttonClicked", cancellable = true, at = @At(value = "HEAD"))
    private void fixTitleScreenButtonsNotRespectingTheirDisabledStatus(ButtonWidget button, CallbackInfo ci) {
        if (!button.active || Atum.loading) {
            ci.cancel();
        }
    }

    // minecraft is a well-coded game
    @Unique
    private void disableAllButtons() {
        for (Object buttonWidget : this.buttons) {
            ((ButtonWidget) buttonWidget).active = false;
        }
    }
}
