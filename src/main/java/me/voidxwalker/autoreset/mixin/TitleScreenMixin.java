package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AtumOptionsScreen;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.menu.DifficultyScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/item/golden_boots.png");

    protected TitleScreenMixin(TextComponent textComponent) {
        super(textComponent);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        if (Atum.running) {
            minecraft.openScreen(new DifficultyScreen());
        } else {
            this.addButton(new ButtonWidget(this.width / 2 - 124, this.height / 4 + 48, 20, 20, "", (buttonWidget) -> {
                if (Screen.hasShiftDown()) {
                    this.minecraft.openScreen(new AtumOptionsScreen());
                } else {
                    Atum.running = true;
                    this.minecraft.openScreen(this);
                }
            }));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.minecraft.getTextureManager().bindTexture(BUTTON_IMAGE);
        blit(this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, 0.0F, 0.0F, 16, 16, 16, 16);
    }
}
