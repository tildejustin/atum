package me.voidxwalker.autoreset.mixin;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.nbt.*;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Unique
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Shadow
    public NbtCompound field_3200;

    @Shadow
    private boolean creatingLevel;

    @Shadow
    private boolean hardcore;

    @Shadow
    private boolean tweakedCheats;

    @Shadow
    private String gameMode;

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            if (Atum.difficulty == -1) {
                hardcore = true;
            }
            createLevel();
        }
    }

    @Unique
    private void createLevel() {
        if (this.creatingLevel) {
            this.client.setScreen(null);
            return;
        }
        this.creatingLevel = true;
        long l = (new Random()).nextLong();
        String string = Atum.seed;
        if (!StringUtils.isEmpty(string)) {
            try {
                long m = Long.parseLong(string);
                if (m != 0L) {
                    l = m;
                }
            } catch (NumberFormatException var7) {
                l = string.hashCode();
            }
        }
        if (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0")) {
            Atum.rsgAttempts++;
        } else {
            Atum.ssgAttempts++;
        }
        this.field_3200 = new NbtCompound();
        LevelInfo levelInfo = new LevelInfo(l, GameMode.byName(this.gameMode), Atum.structures, this.hardcore, LevelGeneratorType.TYPES[Atum.generatorType]);
        levelInfo.setGeneratorOptions(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.field_3200));
        if (Atum.bonusChest && !this.hardcore) {
            levelInfo.setBonusChest();
        }
        if (this.tweakedCheats && !this.hardcore) {
            levelInfo.enableCommands();
        }
        Atum.saveProperties();
        Atum.log(Level.INFO, (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0") ? "Resetting a random seed" : "Resetting the set seed" + " \"" + l + "\""));
        String saveName = (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0")) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts;
        this.client.startIntegratedServer(CreateWorldScreen.method_2724(this.client.getLevelStorage(), saveName), saveName, levelInfo);
    }
}
