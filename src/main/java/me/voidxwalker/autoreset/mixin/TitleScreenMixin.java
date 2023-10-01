package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/item/golden_boots.png");

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void init(CallbackInfo ci) {
        if (Atum.running) Atum.runnable = Atum::tryCreateWorld;
        this.addButton(new ButtonWidget(this.width / 2 - 124, this.height / 4 + 48, 20, 20, LiteralText.EMPTY, (buttonWidget) -> Atum.tryCreateWorld()));
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void runRunnable(CallbackInfo ci) {
        if (Atum.runnable != null) Atum.runnable.run();
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "render", at = @At(value = "TAIL"))
    private void goldBootsOverlay(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.getTextureManager().bindTexture(BUTTON_IMAGE);
        drawTexture(matrices, this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, 0.0F, 0.0F, 16, 16, 16, 16);
    }
}
