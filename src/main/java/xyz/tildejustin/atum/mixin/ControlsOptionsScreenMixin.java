package xyz.tildejustin.atum.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin extends Screen {
    @ModifyArg(
            method = "init",
            index = 2,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIILjava/lang/String;)V"
            )
    )
    private int atum$dynamicDoneButton(int num) {
        return this.height / 6 + 24 * (((Minecraft.getMinecraft().options.allKeys.length + 2 - 1) / 2));
    }
}
