package me.voidxwalker.autoreset.mixin.access;

import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@Mixin(LevelGeneratorOptions.class)
public interface LevelGeneratorOptionsAccessor {
    @Accessor("chunkGeneratorFactory")
    Function<IWorld, ChunkGenerator<?>> getChunkGeneratorFactory();
}
