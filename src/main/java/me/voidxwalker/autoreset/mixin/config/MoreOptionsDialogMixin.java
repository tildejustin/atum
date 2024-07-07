package me.voidxwalker.autoreset.mixin.config;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import me.voidxwalker.autoreset.interfaces.IMoreOptionsDialog;
import me.voidxwalker.autoreset.mixin.access.GeneratorTypeAccessor;
import me.voidxwalker.autoreset.mixin.access.PresetsScreenAccessor;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(MoreOptionsDialog.class)
public abstract class MoreOptionsDialogMixin implements IMoreOptionsDialog {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    private Optional<GeneratorType> field_25049;
    @Shadow
    private GeneratorOptions generatorOptions;
    @Shadow
    private RegistryTracker.Modifiable field_25483;
    @Shadow
    private String seedText;

    @Override
    public void atum$loadAtumConfigurations() {
        this.seedText = Atum.config.seed;
        GeneratorType generatorType = Atum.config.generatorType.get();
        this.field_25049 = Optional.of(generatorType);
        this.generatorOptions = generatorType.method_29077(this.field_25483, this.generatorOptions.getSeed(), Atum.config.structures, Atum.config.bonusChest);

        if (!Atum.config.generatorDetails.isEmpty()) {
            ChunkGenerator chunkGenerator = this.generatorOptions.getChunkGenerator();
            switch (Atum.config.generatorType) {
                case FLAT:
                    this.generatorOptions = this.generatorOptions.method_29573(GeneratorOptions.method_28608(this.generatorOptions.getDimensionMap(), new FlatChunkGenerator(PresetsScreen.method_29060(Atum.config.generatorDetails, (chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator) chunkGenerator).getGeneratorConfig() : FlatChunkGeneratorConfig.getDefaultConfig())))));
                    break;
                case SINGLE_BIOME_SURFACE:
                case SINGLE_BIOME_CAVES:
                case SINGLE_BIOME_FLOATING_ISLANDS:
                    Identifier biomeID = new Identifier(Atum.config.generatorDetails);
                    Optional<Biome> biome = Registry.BIOME.getOrEmpty(new Identifier(Atum.config.generatorDetails));
                    if (biome.isPresent()) {
                        this.generatorOptions = GeneratorTypeAccessor.callMethod_29079(this.generatorOptions, Atum.config.generatorType.get(), biome.get());
                    } else {
                        Atum.log(Level.ERROR, "Error while parsing biome => Unknown biome, " + biomeID);
                    }
            }
        }
    }

    @Override
    public void atum$saveAtumConfigurations() {
        Atum.config.seed = this.seedText;
        Atum.config.generatorType = this.field_25049.map(generatorType -> {
            for (AtumConfig.AtumGeneratorType atumGeneratorType : AtumConfig.AtumGeneratorType.values()) {
                if (atumGeneratorType.get() == generatorType) {
                    return atumGeneratorType;
                }
            }
            return null;
        }).orElse(AtumConfig.AtumGeneratorType.DEFAULT);
        Atum.config.structures = this.generatorOptions.shouldGenerateStructures();
        Atum.config.bonusChest = this.generatorOptions.hasBonusChest();

        String generatorDetails = "";
        switch (Atum.config.generatorType) {
            case FLAT:
                ChunkGenerator chunkGenerator = this.generatorOptions.getChunkGenerator();
                if (chunkGenerator instanceof FlatChunkGenerator) {
                    generatorDetails = PresetsScreenAccessor.callMethod_29062(((FlatChunkGenerator) chunkGenerator).getGeneratorConfig());
                }
                break;
            case SINGLE_BIOME_SURFACE:
            case SINGLE_BIOME_CAVES:
            case SINGLE_BIOME_FLOATING_ISLANDS:
                Identifier biomeID = Registry.BIOME.getId(this.generatorOptions.getChunkGenerator().getBiomeSource().getBiomes().get(0));
                if (biomeID != null) {
                    generatorDetails = biomeID.toString();
                }
        }
        Atum.config.generatorDetails = generatorDetails;
    }

    @Override
    public boolean atum$isSetSeed() {
        return !this.seedText.isEmpty();
    }
}
