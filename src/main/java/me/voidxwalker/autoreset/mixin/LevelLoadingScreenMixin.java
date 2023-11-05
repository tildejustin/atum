package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin extends Screen {
    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "render", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void modifyString(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, String string, int i, int j, int k) {
        if (Atum.running && AtumConfig.instance.checkRandomSeed().isPresent()) {
            this.drawCenteredString(matrices, this.textRenderer, AtumConfig.instance.seed, i, j - 9 / 2 - 50, 16777215);
        }
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    // https://github.com/tildejustin/mcsr-worldpreview-1.16.1/blob/1330dc54fc83327b67f8b55f41f133570e5da098/src/main/java/me/voidxwalker/worldpreview/mixin/client/render/LevelLoadingScreenMixin.java#L143
    @Inject(method = "worldpreview_initWidgets()V", at = @At("TAIL"), require = 0)
    private void worldpreviewCheck(CallbackInfo ci) {
        Atum.loading = false;
    }
}
