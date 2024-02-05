package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.realms.RealmsBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class SettingsScreenMixin extends Screen {
    @Inject(method = "method_2224", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {

        if (Atum.isRunning) {
            this.field_2564.add(new ClickableWidget(1238, 0, this.field_2559 - 20, 100, 20, "Stop Resets & Quit"));
        }
    }

    @Inject(method = "method_0_2778", at = @At("HEAD"))
    public void buttonClicked(ClickableWidget button, CallbackInfo ci) {
        if (button.field_2077 == 1238) {
            Atum.isRunning = false;
            boolean bl = MinecraftClient.getInstance().isIntegratedServerRunning();
            boolean bl2 = MinecraftClient.getInstance().isConnectedToRealms();
            MinecraftClient.getInstance().world.disconnect();
            MinecraftClient.getInstance().method_1550(null);
            if (bl) {
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            } else if (bl2) {
                RealmsBridge realmsBridge = new RealmsBridge();
                realmsBridge.switchToRealms(new TitleScreen());
            } else {
                MinecraftClient.getInstance().setScreen(new MultiplayerScreen(new TitleScreen()));
            }
        }
    }
}

