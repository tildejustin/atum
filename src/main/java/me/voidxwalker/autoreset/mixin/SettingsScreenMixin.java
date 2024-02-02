package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.menu.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    protected SettingsScreenMixin(TextComponent textComponent) {
        super(textComponent);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.running) {
            this.addButton(new ButtonWidget(5, this.height - 25, 100, 20, "Stop Resets & Quit", (buttonWidget) -> {
                Atum.running = false;
                this.minecraft.world.disconnect();
                this.minecraft.method_18096(new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel")));
                this.minecraft.openScreen(new TitleScreen());
            }));
        }
    }
}
