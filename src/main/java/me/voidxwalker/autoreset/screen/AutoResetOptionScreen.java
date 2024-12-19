package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
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
        title = "Autoreset Options";
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void method_21947() {
        this.isHardcore = Atum.difficulty == -1;
        this.seedField = new TextFieldWidget(this.field_22534.textRenderer, this.field_22535 / 2 - 100, this.field_22536 - 160, 200, 20);
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seedField.setFocused(true);
        this.seed = Atum.seed;
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;
        this.field_22537.add(new ButtonWidget(340, field_22535 / 2 + 5, this.field_22536 - 100, 150, 20, "Is Hardcore: " + isHardcore));
        this.field_22537.add(new ButtonWidget(341, field_22535 / 2 - 155, this.field_22536 - 100, 150, 20, (new TranslatableText("selectWorld.mapType").asFormattedString() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey()))));
        this.field_22537.add(new ButtonWidget(342, field_22535 / 2 - 155, this.field_22536 - 64, 150, 20, new TranslatableText("selectWorld.mapFeatures").asFormattedString() + " " + structures));
        this.field_22537.add(new ButtonWidget(344, field_22535 / 2 + 5, this.field_22536 - 64, 150, 20, new TranslatableText("selectWorld.bonusItems").asFormattedString() + " " + bonusChest));
        this.field_22537.add(new ButtonWidget(345, field_22535 / 2 - 155, this.field_22536 - 28, 150, 20, "Done"));
        this.field_22537.add(new ButtonWidget(343, field_22535 / 2 + 5, this.field_22536 - 28, 150, 20, I18n.translate("gui.cancel")));
    }

    @Override
    public void method_21936() {
        seedField.tick();
    }

    @Override
    public void method_21925(int mouseX, int mouseY, float delta) {
        this.method_21946();
        this.method_21881(this.field_22534.textRenderer, this.title, field_22535 / 2, this.field_22536 - 210, -1);
        this.method_21884(this.field_22534.textRenderer, "Seed (Leave empty for a random Seed)", field_22535 / 2 - 100, this.field_22536 - 180, -6250336);
        this.seedField.render();
        super.method_21925(mouseX, mouseY, delta);
    }

    @Override
    public void method_21924(char character, int code) {
        if (this.seedField.isFocused()) {
            this.seedField.keyPressed(character, code);
            this.seed = this.seedField.getText();
        }
    }

    @Override
    protected void method_21930(ButtonWidget button) {
        switch (button.id) {
            case 340:
                isHardcore = !isHardcore;
                button.field_22510 = ("Is Hardcore: " + isHardcore);
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
                button.field_22510 = (new TranslatableText("selectWorld.mapType").asFormattedString() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey()));
                break;
            case 342:
                structures = !structures;
                button.field_22510 = (new TranslatableText("selectWorld.mapFeatures").asFormattedString() + " " + structures);
                break;
            case 344:
                bonusChest = !bonusChest;
                button.field_22510 = (new TranslatableText("selectWorld.bonusItems").asFormattedString() + " " + bonusChest);
                break;
            case 345:
                Atum.seed = seed;
                Atum.difficulty = isHardcore ? -1 : 2;
                Atum.structures = structures;
                Atum.bonusChest = bonusChest;
                Atum.generatorType = generatorType;
                Atum.saveProperties();
                field_22534.setScreen(parent);
                break;
            case 343:
                field_22534.setScreen(parent);
                break;
        }
    }
}
