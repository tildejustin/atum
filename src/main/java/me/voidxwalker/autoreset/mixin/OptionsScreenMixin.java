package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.isRunning) {
            this.addButton(new ButtonWidget(0, this.height - 20, 100, 20, Text.of("Stop Resets & Quit"), (buttonWidget) -> {
                Atum.isRunning = false;
                if (this.client != null && this.client.world != null) {
                    this.client.world.disconnect();
                    this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                    this.client.openScreen(new TitleScreen());
                }
            }));
        }
    }
}
