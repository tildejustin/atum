package xyz.tildejustin.atum.mixin.accessor;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @SuppressWarnings("rawtypes")
    @Accessor(value = "buttons")
    List getButtons();
}
