package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/item/golden_boots.png");

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (Atum.isRunning) {
            Atum.scheduleReset();
        }
        this.method_2219(new ClickableWidget(69, this.width / 2 - 124, this.height / 4 + 48, 20, 20, "") {
            @Override
            public void method_1826(double d, double e) {
                if (method_2223()) {
                    client.setScreen(new AutoResetOptionScreen(TitleScreenMixin.this));
                } else {
                    Atum.scheduleReset();
                }
            }
        });
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.getTextureManager().bindTextureInner(BUTTON_IMAGE);
        method_1781(this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (mouseX > this.width / 2 - 124 && mouseX < this.width / 2 - 124 + 20 && mouseY > this.height / 4 + 48 && mouseY < this.height / 4 + 48 + 20 && method_2223()) {
            this.method_1789(client.textRenderer, getDifficultyText().getString(), this.width / 2 - 124 + 11, this.height / 4 + 48 - 15, 16777215);
        }
    }

    @Unique
    Text getDifficultyText() {
        return new TranslatableTextContent("selectWorld.gameMode.hardcore").append(": ").append(new TranslatableTextContent("options." + (Atum.difficulty != -1 ? "off" : "on")));
    }
}
