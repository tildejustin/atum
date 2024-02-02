package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/items/gold_boots.png");

    @Unique
    private String difficulty;

    @Inject(method = "method_2224", at = @At("HEAD"))
    private void clearLastScreenWidgets(CallbackInfo ci) {
        this.field_2564.clear();
    }

    @Inject(method = "method_2224", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (Atum.isRunning) {
            field_2563.setScreen(new CreateWorldScreen(this));
        } else {
            Atum.hotkeyState = Atum.HotkeyState.OUTSIDE_WORLD;
            method_2219(new ClickableWidget(69, this.field_2561 / 2 - 124, this.field_2559 / 4 + 48, 20, 20, ""));
        }
    }

    @Inject(method = "method_2214", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        getDifficulty();
        this.field_2563.getTextureManager().bindTextureInner(BUTTON_IMAGE);
        method_1781(this.field_2561 / 2 - 124 + 2, this.field_2559 / 4 + 48 + 2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (mouseX > this.field_2561 / 2 - 124 && mouseX < this.field_2561 / 2 - 124 + 20 && mouseY > this.field_2559 / 4 + 48 && mouseY < this.field_2559 / 4 + 48 + 20 && method_2223()) {
            method_1789(field_2563.field_1772, difficulty, this.field_2561 / 2 - 124 + 11, this.field_2559 / 4 + 48 - 15, 16777215);
        }
    }

    @Inject(method = "method_0_2778", at = @At("HEAD"), cancellable = true)
    public void buttonClicked(ClickableWidget button, CallbackInfo ci) {
        if (button.field_2077 == 69) {
            if (method_2223()) {
                field_2563.setScreen(new AutoResetOptionScreen(null));
            } else {
                Atum.isRunning = true;
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            }
            ci.cancel();
        }
    }

    @Unique
    private void getDifficulty() {
        if (Atum.difficulty == -1) {
            difficulty = "Hardcore: ON";
        } else {
            difficulty = "Hardcore: OFF";
        }
    }
}
