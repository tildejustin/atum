package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    @Unique
    private ButtonWidget stopResetting;

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/OptionsScreen;refreshWidgetPositions()V"))
    public void addAutoResetButton(CallbackInfo ci) {
        if (Atum.isRunning) {
            stopResetting = this.addDrawableChild(ButtonWidget.builder(Atum.getTranslation("menu.stop_resets", "Stop Resets & Quit"), (buttonWidget) -> {
                Atum.isRunning = false;
                if (this.client != null && this.client.world != null) {
                    this.client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
                    this.client.disconnectWithSavingScreen();
                    this.client.setScreen(new TitleScreen());
                }
            }).size(100, 20).build());
        }
    }

    @Inject(method = "refreshWidgetPositions", at = @At("TAIL"))
    private void moveAutoResetButton(CallbackInfo ci) {
        if (stopResetting != null) {
            stopResetting.setPosition(0, this.height - 20);
        }
    }
}
