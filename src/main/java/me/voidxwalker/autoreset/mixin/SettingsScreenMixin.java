package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class SettingsScreenMixin extends Screen {
    @Shadow
    protected abstract void method_0_2778(ClickableWidget clickableWidget);

    @Inject(method = "method_2214", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.isRunning) {
            this.method_2219(new ClickableWidget(1238, 5, this.field_2559 - 25, 100, 20, "Stop Resets & Quit"));
        }
    }

    @Inject(method = "method_0_2778", at = @At("HEAD"))
    public void buttonClicked(ClickableWidget button, CallbackInfo ci) {
        if (button.field_2077 == 1238) {
            if (!Atum.isRunning) {
                return;
            }
            Atum.isRunning = false;
            if (MinecraftClient.getInstance().world != null) {
                this.field_2563.world.disconnect();
            }
            this.field_2563.method_1550(null);
            this.field_2563.setScreen(null);
        }
    }
}
