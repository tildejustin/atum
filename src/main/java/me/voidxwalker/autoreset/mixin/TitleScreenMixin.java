package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/items/gold_boots.png");

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (Atum.isRunning) {
            client.setScreen(new CreateWorldScreen(this));
        } else {
            Atum.hotkeyState = Atum.HotkeyState.OUTSIDE_WORLD;
            addButton(new ButtonWidget(69, this.width / 2 - 124, this.height / 4 + 48, 20, 20, ""));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.getTextureManager().bindTexture(BUTTON_IMAGE);
        drawTexture(this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (mouseX > this.width / 2 - 124 && mouseX < this.width / 2 - 124 + 20 && mouseY > this.height / 4 + 48 && mouseY < this.height / 4 + 48 + 20 && hasShiftDown()) {
            drawCenteredString(client.textRenderer, getDifficultyText().asFormattedString(), this.width / 2 - 124 + 11, this.height / 4 + 48 - 15, 16777215);
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
    public void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 69) {
            if (hasShiftDown()) {
                client.setScreen(new AutoResetOptionScreen(null));
            } else {
                Atum.isRunning = true;
                MinecraftClient.getInstance().setScreen(null);
            }
            ci.cancel();
        }
    }

    @Unique
    Text getDifficultyText() {
        return new TranslatableText("selectWorld.gameMode.hardcore").append(": ").append(new TranslatableText("options." + (Atum.difficulty != -1 ? "off" : "on")));
    }
}
