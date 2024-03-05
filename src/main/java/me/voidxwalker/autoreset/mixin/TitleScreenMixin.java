package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
import net.minecraft.client.texture.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (Atum.running && !Atum.loading) {
            this.client.setScreen(new CreateWorldScreen(this));
        } else {
            this.buttons.add(new ButtonWidget(69, this.width / 2 - 124, this.height / 4 + 48, 20, 20, ""));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.field_6557);
        Sprite texture = ((SpriteAtlasTexture) MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.field_6557)).getSprite("gold_boots");
        method_4944(this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, texture, 16, 16);
        if (mouseX > this.width / 2 - 124 && mouseX < this.width / 2 - 124 + 20 && mouseY > this.height / 4 + 48 && mouseY < this.height / 4 + 48 + 20 && hasShiftDown()) {
            drawCenteredString(client.textRenderer, getDifficultyText(), this.width / 2 - 124 + 11, this.height / 4 + 48 - 15, 16777215);
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
    public void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (Atum.loading) {
            // fixes being able to click title screen buttons during the blink
            ci.cancel();
        }
        if (button.id == 69) {
            if (hasShiftDown()) {
                client.setScreen(new AutoResetOptionScreen(null));
            } else {
                Atum.running = true;
                this.client.setScreen(new TitleScreen());
            }
            ci.cancel();
        }
    }

    @Unique
    String getDifficultyText() {
        return I18n.translate("selectWorld.gameMode.hardcore") + ": " + I18n.translate("options." + (Atum.difficulty != -1 ? "off" : "on"));
    }
}
