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
    @Inject(method = "method_21947", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.isRunning) {
            this.field_22537.add(new ButtonWidget(1238, 0, this.field_22536 - 20, 100, 20, "Stop Resets & Quit"));
        }
    }

    @Inject(method = "method_21930", at = @At("HEAD"))
    public void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 1238) {
            Atum.isRunning = false;
            MinecraftClient.getInstance().world.disconnect();
            MinecraftClient.getInstance().connect(null);
            MinecraftClient.getInstance().setScreen(new TitleScreen());
        }
    }
}
