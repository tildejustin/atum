package me.voidxwalker.autoreset.mixin.gui;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumCreateWorldScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/item/golden_boots.png");

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        this.addButton(new ButtonWidget(this.width / 2 - 124, this.height / 4 + 48, 20, 20, "", button -> {
            if (!Screen.hasShiftDown()) {
                Atum.scheduleReset();
            } else {
                MinecraftClient.getInstance().openScreen(new AtumCreateWorldScreen(this));
            }
        }) {
            @Override
            public void renderButton(int mouseX, int mouseY, float delta) {
                super.renderButton(mouseX, mouseY, delta);
                MinecraftClient.getInstance().getTextureManager().bindTexture(BUTTON_IMAGE);
                drawTexture(this.x + 2, this.y + 2, 0.0F, 0.0F, 16, 16, 16, 16);
                if (this.isHovered() && hasShiftDown()) {
                    this.drawCenteredString(TitleScreenMixin.this.textRenderer, I18n.translate("atum.menu.open_config"), this.x + this.width / 2, this.y - 15, 16777215);
                }
            }
        });
    }
}
