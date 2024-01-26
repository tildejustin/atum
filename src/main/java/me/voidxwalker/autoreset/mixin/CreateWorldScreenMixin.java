package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.gen.WorldPreset;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Optional;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    protected abstract void createLevel();

    @Shadow
    @Final
    WorldCreator worldCreator;

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            Difficulty difficulty;
            switch (Atum.difficulty) {
                case 0 -> difficulty = Difficulty.PEACEFUL;
                case 1 -> difficulty = Difficulty.EASY;
                case 2 -> difficulty = Difficulty.NORMAL;
                case 3 -> difficulty = Difficulty.HARD;
                case 4 -> {
                    difficulty = Difficulty.HARD;
                    this.worldCreator.setGameMode(WorldCreator.Mode.HARDCORE);
                }
                default -> {
                    Atum.log(Level.WARN, "Invalid difficulty");
                    difficulty = Difficulty.EASY;
                }
            }
            if (Atum.seed == null || Atum.seed.isEmpty()) {
                Atum.rsgAttempts++;
            } else {
                Atum.ssgAttempts++;
            }
            try {
                Atum.saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.worldCreator.setDifficulty(difficulty);
            this.worldCreator.setWorldName((Atum.seed == null || Atum.seed.isEmpty()) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts);
            this.worldCreator.setWorldType(this.worldCreator.getNormalWorldTypes().get(Atum.generatorType));
            this.worldCreator.setGenerateStructures(Atum.structures);
            this.worldCreator.setBonusChestEnabled(Atum.bonusChest);
            this.worldCreator.setSeed(Atum.seed);
            createLevel();
        }
    }
}
