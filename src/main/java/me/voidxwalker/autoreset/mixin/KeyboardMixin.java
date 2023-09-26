package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    // all the deranged code stays here
    @Inject(method = "onKey", at = @At(value = "HEAD"))
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        if (!Atum.loading && !Atum.blocking && Atum.resetKey.matchesKey(key, scancode) && !(client.currentScreen instanceof ControlsOptionsScreen)) {
            Atum.running = Atum.blocking = true;
            // hack for resetting during preview
            if (client.currentScreen instanceof LevelLoadingScreen) {
                client.currentScreen.children().stream().filter(
                        element -> element instanceof ButtonWidget && ((ButtonWidget) element).getMessage().equals(new TranslatableText("menu.returnToMenu"))
                ).findFirst().ifPresent(button -> ((ButtonWidget) button).onPress());
            } else {
                Optional<? extends Element> buttonWidget = Optional.empty();
                if (client.currentScreen == null) {
                    GameMenuScreen menuScreen = new GameMenuScreen(true);
                    client.openScreen(menuScreen);
                    buttonWidget = menuScreen.children().stream().filter(
                            element -> element instanceof ButtonWidget && ((ButtonWidget) element).getMessage().equals(new TranslatableText("menu.quitWorld"))
                    ).findFirst();
                    buttonWidget.ifPresent(button -> ((ButtonWidget) button).onPress());
                }
                if (!buttonWidget.isPresent()) {
                    if (client.world != null) {
                        client.world.disconnect();
                        client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                    }
                    client.openScreen(new TitleScreen());
                }
            }
        }
    }
}
