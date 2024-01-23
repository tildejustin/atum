package me.voidxwalker.autoreset.mixin.hotkey;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.*;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    List<ButtonWidget> getButtons();

    @Invoker
    void callButtonClicked(ButtonWidget button);
}
