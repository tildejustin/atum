package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Main;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;


@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen{
    @Shadow private TextFieldWidget levelNameField;


    @Shadow private boolean creatingLevel;

    @Shadow private String gamemodeName;

    @Shadow private boolean structures;

    @Shadow private boolean hardcore;

    @Shadow private int generatorType;

    @Shadow public String generatorOptions;

    @Shadow private boolean bonusChest;

    @Shadow private boolean tweakedCheats;

    @Shadow private String saveDirectoryName;

    @Shadow private TextFieldWidget seedField;

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Main.isRunning) {
            if(Main.isHardcore){
                hardcore=true;
            }
            levelNameField.setText((Main.seed==null||Main.seed.isEmpty()?"Random":"Set")+"Speedrun #" + Main.getNextAttempt());
            createLevel();
        }
    }
    private void createLevel (){
        this.client.openScreen((Screen)null);
        if (this.creatingLevel) {
            return;
        }

        this.creatingLevel = true;
        long l = (new Random()).nextLong();
        String string = Main.seed;
        if (!MathHelper.method_2340(string)) {
            try {
                long var5 = Long.parseLong(string);
                if (var5 != 0L) {
                    l = var5;
                }
            } catch (NumberFormatException var7) {
                l = (long)string.hashCode();
            }
        }

        GameMode var8 = GameMode.setGameModeWithString(this.gamemodeName);
        LevelInfo var6 = new LevelInfo(l, var8, this.structures, this.hardcore, LevelGeneratorType.TYPES[this.generatorType]);
        var6.setGeneratorOptions(this.generatorOptions);
        if (this.bonusChest && !this.hardcore) {
            var6.setBonusChest();
        }

        if (this.tweakedCheats && !this.hardcore) {
            var6.enableCommands();
        }

        this.client.startGame((Main.seed==null||Main.seed.isEmpty()?"Random":"Set")+"Speedrun #" + Main.getNextAttempt(), (Main.seed==null||Main.seed.isEmpty()?"Random":"Set")+"Speedrun #" + Main.getNextAttempt(), var6);


        Main.log(Level.INFO,(Main.seed==null||Main.seed.isEmpty()?"Resetting a random seed":"Resetting the set seed"+"\""+l+"\""));

    }
}
