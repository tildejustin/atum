package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    @Inject(method = "init", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.isRunning) {
            this.addButton(new ButtonWidget(1238, 0, this.height - 20, 100, 20, "Stop Resets & Quit") {
                @Override
                public void method_18374(double d, double e) {
                    Atum.isRunning = false;
                    if (client != null && client.world != null) {
                        client.world.disconnect();
                        client.connect(null);
                        client.setScreen(new TitleScreen());
                    }
                }
            });
        }
    }
}
