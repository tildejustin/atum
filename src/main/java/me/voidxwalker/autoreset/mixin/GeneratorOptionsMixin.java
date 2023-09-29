package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.OptionalLong;

@Mixin(GeneratorOptions.class)
public abstract class GeneratorOptionsMixin {
    @Shadow
    public abstract SimpleRegistry<DimensionOptions> getDimensionMap();

    @ModifyArg(method = "withHardcore", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;<init>(JZZLnet/minecraft/util/registry/SimpleRegistry;)V", ordinal = 1), index = 3)
    private SimpleRegistry<DimensionOptions> setSuperflatChunkGenerator(SimpleRegistry<DimensionOptions> simpleRegistry) {
        if (!Atum.running || !AtumConfig.instance.generatorType.equals(AtumConfig.AtumGeneratorType.FLAT)) return simpleRegistry;
        FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
        config = PresetsScreen.method_29060(AtumConfig.instance.superflatConfig, config);
        return GeneratorOptions.method_28608(this.getDimensionMap(), new FlatChunkGenerator(config));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(method = "withHardcore", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void setBiomeforSingleBiomeGeneratorTypes(boolean hardcore, OptionalLong seed, CallbackInfoReturnable<GeneratorOptions> cir, GeneratorOptions generatorOptions) {
        if (Atum.running && AtumConfig.singleBiomeSourceBiomes.contains(AtumConfig.instance.generatorType)) {
            cir.setReturnValue(GeneratorType.method_29079(generatorOptions, AtumConfig.instance.generatorType.get(), Optional.ofNullable(Registry.BIOME.get(new Identifier(AtumConfig.instance.biome))).orElse(Biomes.PLAINS)));
        }
    }
}
