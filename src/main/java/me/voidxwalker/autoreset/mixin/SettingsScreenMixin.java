package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    protected SettingsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {

        if (Atum.isRunning) {
            this.addButton(new ButtonWidget(0, this.height - 20, 100, 20, Atum.getTranslation("menu.stop_resets", "Stop Resets & Quit").asString(), (buttonWidget) -> {
                Atum.isRunning = false;
                this.minecraft.world.disconnect();
                this.minecraft.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                this.minecraft.openScreen(new TitleScreen());
            }));
        }
    }
}
