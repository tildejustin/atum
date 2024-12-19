package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public ClientWorld world;

    @Shadow
    public abstract void joinWorld(@Nullable ClientWorld world);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", ordinal = 0))
    private void executeReset(boolean bl, CallbackInfo ci) {
        while (Atum.shouldReset() && bl) {
            if (this.world != null) {
                Screen gameMenuScreen = new GameMenuScreen();
                gameMenuScreen.init((MinecraftClient) (Object) this, 0, 0);
                if (!this.clickButton(gameMenuScreen, "fast_reset.menu.quitWorld", "menu.quitWorld", "menu.returnToMenu", "menu.disconnect") || this.world != null) {
                    if (this.world != null) {
                        this.world.disconnect();
                        this.joinWorld(null);
                    }
                }
            }
            Atum.createNewWorld();
        }
    }

    @Unique
    private boolean clickButton(Screen screen, String... translationKeys) {
        for (String translationKey : translationKeys) {
            for (ClickableWidget button : ((ScreenAccessor) screen).getButtons()) {
                if (button.message.equals(new TranslatableTextContent(translationKey).getString())) {
                    button.method_1826(0, 0);
                    return true;
                }
            }
        }
        return false;
    }
}
