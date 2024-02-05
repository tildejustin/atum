package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.running) {
            this.buttons.add(new ButtonWidget(1238, 0, this.height - 20, 100, 20, "Stop Resets & Quit"));
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    public void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 1238) {
            Atum.running = false;
            MinecraftClient.getInstance().world.disconnect();
            MinecraftClient.getInstance().connect(null);
            MinecraftClient.getInstance().setScreen(new TitleScreen());
        }
    }
}
