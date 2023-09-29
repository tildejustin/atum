package me.voidxwalker.autoreset;

import net.minecraft.client.world.GeneratorType;

public interface IMoreOptionsDialog {
    void atum$setGeneratorType(GeneratorType g);

    void atum$setGenerateStructure(boolean generate);

    void atum$setGenerateBonusChest(boolean generate);
}
