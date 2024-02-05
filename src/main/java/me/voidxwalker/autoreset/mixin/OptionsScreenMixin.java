package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
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
            this.addDrawableChild(ButtonWidget.builder(Atum.getTranslation("menu.stop_resets", "Stop Resets & Quit"), (buttonWidget) -> {
                Atum.isRunning = false;
                if (this.client != null && this.client.world != null) {
                    this.client.world.disconnect();
                    this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
                    this.client.setScreen(new TitleScreen());
                }
            }).dimensions(0, this.height - 20, 100, 20).build());
        }
    }
}
