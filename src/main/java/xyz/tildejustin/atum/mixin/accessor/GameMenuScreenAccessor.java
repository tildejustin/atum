package xyz.tildejustin.atum.mixin.accessor;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameMenuScreen.class)
public interface GameMenuScreenAccessor {
    @Invoker(value = "buttonClicked")
    void callButtonClicked(ButtonWidget button);
}
