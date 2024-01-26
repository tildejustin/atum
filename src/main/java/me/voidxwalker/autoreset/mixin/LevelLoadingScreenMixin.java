package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin extends Screen {
    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void modifyString(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty()) {
            String string = Atum.seed;
            context.drawCenteredTextWithShadow(this.textRenderer, string, i, j - 9 / 2 - 50, 16777215);
        }
    }
}
