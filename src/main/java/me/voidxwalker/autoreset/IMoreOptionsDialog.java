package me.voidxwalker.autoreset;

import net.minecraft.world.gen.WorldPreset;

public interface IMoreOptionsDialog {
    void atum$setGeneratorType(WorldPreset preset);

    void atum$setGenerateStructure(boolean generate);

    void atum$setGenerateBonusChest(boolean generate);
}
