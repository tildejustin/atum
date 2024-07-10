package me.voidxwalker.autoreset.mixin.access;

import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PresetsScreen.class)
public interface PresetsScreenAccessor {
    @Invoker
    static String callGetGeneratorConfigString(FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
        throw new UnsupportedOperationException();
    }
}
