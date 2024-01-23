package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin extends Screen {
    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void modifyString(int mouseX, int mouseY, float delta, CallbackInfo ci, String ignored, int i, int j) {
        if (Atum.isRunning && Atum.seed != null && !Atum.seed.isEmpty()) {
            String string = Atum.seed;
            this.drawCenteredString(minecraft.textRenderer, string, i, j - 9 / 2 - 50, 16777215);
        }
    }
}
