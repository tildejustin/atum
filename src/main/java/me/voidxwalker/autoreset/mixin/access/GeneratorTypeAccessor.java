package me.voidxwalker.autoreset.mixin.access;

import net.minecraft.client.world.GeneratorType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GeneratorType.class)
public interface GeneratorTypeAccessor {
    @Accessor
    static GeneratorType getDEFAULT() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getFLAT() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getLARGE_BIOMES() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getAMPLIFIED() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getSINGLE_BIOME_SURFACE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getSINGLE_BIOME_CAVES() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getSINGLE_BIOME_FLOATING_ISLANDS() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static GeneratorType getDEBUG_ALL_BLOCK_STATES() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static GeneratorOptions callCreateFixedBiomeOptions(GeneratorOptions generatorOptions, GeneratorType generatorType, Biome biome) {
        throw new UnsupportedOperationException();
    }
}
