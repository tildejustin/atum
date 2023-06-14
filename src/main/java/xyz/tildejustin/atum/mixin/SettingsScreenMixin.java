package xyz.tildejustin.atum.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.tildejustin.atum.Atum;
import xyz.tildejustin.atum.mixin.accessor.GameMenuScreenAccessor;
import xyz.tildejustin.atum.mixin.accessor.ScreenAccessor;

@Mixin(SettingsScreen.class)
public abstract class SettingsScreenMixin extends Screen {
    @Shadow
    @Final
    private Screen parent;

    @SuppressWarnings("unchecked")
    @Inject(
            method = "init",
            at = @At(
                    value = "TAIL"
            )
    )
    private void atum$addStopResetsButton(CallbackInfo ci) {
        if (Atum.running) {
            this.buttons.add(new ButtonWidget(201, 5, this.height - 20 - 5, 100, 20, "Stop Resets & Quit"));
        }
    }

    @Inject(
            method = "buttonClicked",
            at = @At(
                    value = "TAIL"
            )
    )
    private void atum$stopResetsAndQuit(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 201) {
            button.active = false;
            Atum.running = false;
            if (this.parent instanceof GameMenuScreen) {
                // good code :)
                ButtonWidget quitButton = (ButtonWidget) ((ScreenAccessor) this.parent).getButtons().get(0);
                ((GameMenuScreenAccessor) this.parent).callButtonClicked(quitButton);
            }
        }
    }
}
