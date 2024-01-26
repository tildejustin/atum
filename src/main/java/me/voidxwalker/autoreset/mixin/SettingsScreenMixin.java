package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.realms.RealmsBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    @Inject(method = "init", at = @At("TAIL"))
    public void addAutoResetButton(CallbackInfo ci) {

        if (Atum.isRunning) {
            this.addButton(new ButtonWidget(1238, 0, this.height - 20, 100, 20, "Stop Resets & Quit"));
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    public void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 1238) {
            Atum.isRunning = false;
            boolean bl = MinecraftClient.getInstance().isIntegratedServerRunning();
            boolean bl2 = MinecraftClient.getInstance().isConnectedToRealms();
            MinecraftClient.getInstance().world.disconnect();
            MinecraftClient.getInstance().connect(null);
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
