package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

public class AutoResetOptionScreen extends Screen {
    private final Screen parent;
    private final String title;
    private TextFieldWidget seedField;
    private String seed;
    private boolean isHardcore;
    private int generatorType;
    private boolean structures;
    private boolean bonusChest;

    public AutoResetOptionScreen(@Nullable Screen parent) {
        super();
        title = Atum.getTranslation("menu.autoresetTitle", "Autoreset Options");
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        this.isHardcore = Atum.difficulty == -1;
        this.seedField = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, this.height - 160, 200, 20);
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seedField.setFocused(true);
        this.seed = Atum.seed;
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;
        this.buttons.add(new ButtonWidget(340, this.width / 2 + 5, this.height - 100, 150, 20, "Is Hardcore: " + isHardcore));
        this.buttons.add(new ButtonWidget(341, this.width / 2 - 155, this.height - 100, 150, 20, I18n.translate("selectWorld.mapType") + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey())));
        this.buttons.add(new ButtonWidget(342, this.width / 2 - 155, this.height - 64, 150, 20, I18n.translate("selectWorld.mapFeatures") + " " + structures));
        this.buttons.add(new ButtonWidget(344, this.width / 2 + 5, this.height - 64, 150, 20, I18n.translate("selectWorld.bonusItems") + " " + bonusChest));
        this.buttons.add(new ButtonWidget(345, this.width / 2 - 155, this.height - 28, 150, 20, Atum.getTranslation("menu.done", "Done")));
        this.buttons.add(new ButtonWidget(343, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
    }

    @Override
    public void tick() {
        seedField.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.client.textRenderer, this.title, this.width / 2, this.height - 210, -1);
        this.drawWithShadow(this.client.textRenderer, "Seed (Leave empty for a random Seed)", this.width / 2 - 100, this.height - 180, -6250336);
        this.seedField.render();
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void keyPressed(char character, int code) {
        if (this.seedField.isFocused()) {
            this.seedField.keyPressed(character, code);
            this.seed = this.seedField.getText();
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 340:
                isHardcore = !isHardcore;
                button.message = "Is Hardcore: " + isHardcore;
                break;
            case 341:
                ++generatorType;
                if (generatorType >= LevelGeneratorType.TYPES.length) {
                    generatorType = 0;
                }
                while (LevelGeneratorType.TYPES[this.generatorType] == null || !LevelGeneratorType.TYPES[this.generatorType].isVisible()) {
                    ++this.generatorType;
                    if (this.generatorType < LevelGeneratorType.TYPES.length) continue;
                    this.generatorType = 0;
                }
                button.message = I18n.translate("selectWorld.mapType") + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey());
                break;
            case 342:
                structures = !structures;
                button.message = I18n.translate("selectWorld.mapFeatures") + " " + structures;
                break;
            case 344:
                bonusChest = !bonusChest;
                button.message = I18n.translate("selectWorld.bonusItems") + " " + bonusChest;
                break;
            case 345:
                Atum.seed = seed;
                Atum.difficulty = isHardcore ? -1 : 2;
                Atum.structures = structures;
                Atum.bonusChest = bonusChest;
                Atum.generatorType = generatorType;
                Atum.saveProperties();
                client.setScreen(parent);
                break;
            case 343:
                client.setScreen(parent);
                break;
        }
    }
}
