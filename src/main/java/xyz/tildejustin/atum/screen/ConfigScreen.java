package xyz.tildejustin.atum.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Language;
import net.minecraft.world.level.LevelGeneratorType;
import org.lwjgl.input.Keyboard;
import xyz.tildejustin.atum.Config;

import java.util.Arrays;
import java.util.List;

//       [seed field]
// structures    world type
//  gamemode     bonus chest
//           done
public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config;
    private final List<String> gameModes = Arrays.asList("survival", "hardcore", "creative");
    private TextFieldWidget seedField;
    private ButtonWidget generateStructuresButton;
    private ButtonWidget gameModeButton;
    private ButtonWidget mapTypeSwitchButton;
    private ButtonWidget bonusChestButton;
    private int generatorType;

    public ConfigScreen(Screen parent, Config config) {
        this.parent = parent;
        this.config = config;
        // wtf is this :sob:
        this.generatorType = Arrays.asList(LevelGeneratorType.TYPES).indexOf(LevelGeneratorType.getTypeFromName(config.generatorType));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        Language language = Language.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.generateStructuresButton = new ButtonWidget(1, this.width / 2 - 155, 100, 150, 20, language.translate("selectWorld.mapFeatures"));
        this.buttons.add(this.generateStructuresButton);
        this.gameModeButton = new ButtonWidget(2, this.width / 2 - 155, 136, 150, 20, language.translate("selectWorld.gameMode"));
        this.buttons.add(this.gameModeButton);
        this.mapTypeSwitchButton = new ButtonWidget(3, this.width / 2 + 5, 100, 150, 20, language.translate("selectWorld.mapType"));
        this.buttons.add(this.mapTypeSwitchButton);
        this.bonusChestButton = new ButtonWidget(4, this.width / 2 + 5, 136, 150, 20, language.translate("selectWorld.bonusItems"));
        this.buttons.add(this.bonusChestButton);
        this.buttons.add(new ButtonWidget(0, this.width / 2 - 75, 172, 150, 20, language.translate("gui.done")));
        this.seedField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20);
        this.seedField.setText(this.config.seed);
        this.updateLabels();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (button.id == 0) {
            minecraft.openScreen(this.parent);
        } else if (button.id == this.mapTypeSwitchButton.id) {
            ++this.generatorType;
            if (this.generatorType >= LevelGeneratorType.TYPES.length) {
                this.generatorType = 0;
            }
            while (LevelGeneratorType.TYPES[this.generatorType] == null || !LevelGeneratorType.TYPES[this.generatorType].isVisible()) {
                ++this.generatorType;
                if (this.generatorType < LevelGeneratorType.TYPES.length) continue;
                this.generatorType = 0;
            }
            this.config.generatorType = LevelGeneratorType.TYPES[this.generatorType].getName();
        } else if (button.id == this.gameModeButton.id) {
            this.config.gameMode = this.gameModes.get(this.mod(this.gameModes.indexOf(this.config.gameMode) + 1, this.gameModes.size()));
            if (this.config.gameMode.equals("hardcore")) {
                this.config.hardcore = true;
                this.config.bonusChest = false;
                this.bonusChestButton.active = false;
            } else {
                this.config.hardcore = false;
                this.bonusChestButton.active = true;
            }
        } else if (button.id == this.generateStructuresButton.id) {
            this.config.structures = !this.config.structures;
        } else if (button.id == this.bonusChestButton.id) {
            this.config.bonusChest = !this.config.bonusChest;
        }
        if (button.id != 0) {
            this.updateLabels();
            Config.writeConfig(this.config);
        }
    }

    private int mod(int numerator, int divisor) {
        return ((numerator % divisor) + divisor) % divisor;
    }

    private void updateLabels() {
        Language language = Language.getInstance();
        this.gameModeButton.message = language.translate("selectWorld.gameMode") + " " + language.translate("selectWorld.gameMode." + this.config.gameMode);
        this.generateStructuresButton.message = language.translate("selectWorld.mapFeatures") + " ";
        this.generateStructuresButton.message = this.config.structures ? this.generateStructuresButton.message + language.translate("options.on") : this.generateStructuresButton.message + language.translate("options.off");
        this.bonusChestButton.message = language.translate("selectWorld.bonusItems") + " ";
        this.bonusChestButton.message = this.config.bonusChest && !this.config.hardcore ? this.bonusChestButton.message + language.translate("options.on") : this.bonusChestButton.message + language.translate("options.off");
        this.mapTypeSwitchButton.message = language.translate("selectWorld.mapType") + " " + language.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey());
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, "Atum configuration", this.width / 2, 20, 0xFFFFFF);
        this.seedField.render();
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.seedField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyPressed(char id, int code) {
        if (this.seedField.isFocused()) {
            this.seedField.keyPressed(id, code);
        }
        this.config.seed = seedField.getText().trim();
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
}
