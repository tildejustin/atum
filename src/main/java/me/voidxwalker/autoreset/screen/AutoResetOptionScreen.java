package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

public class AutoResetOptionScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget seedField;
    private String seed;
    private boolean isHardcore;
    private int generatorType;
    private boolean structures;
    private final String title;
    private boolean bonusChest;

    public AutoResetOptionScreen(@Nullable Screen parent) {
        super();
        title = Atum.getTranslation("menu.autoresetTitle", "Autoreset Options").method_0_5147();
        this.parent = parent;
    }

    @Override
    public void method_2224() {
        this.isHardcore = Atum.difficulty == -1;
        this.seedField = new TextFieldWidget(350, this.field_2563.field_1772, this.field_2561 / 2 - 100, this.field_2559 - 160, 200, 20);
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seedField.setTextFieldFocused(true);
        this.seed = Atum.seed;
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;

        this.field_2564.add(new ClickableWidget(340, this.field_2561 / 2 + 5, this.field_2559 - 100, 150, 20, "Is Hardcore: " + isHardcore));
        this.field_2564.add(new ClickableWidget(341, this.field_2561 / 2 - 155, this.field_2559 - 100, 150, 20, new TranslatableTextContent("selectWorld.mapType").method_0_5147() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey())));
        this.field_2564.add(new ClickableWidget(342, this.field_2561 / 2 - 155, this.field_2559 - 64, 150, 20, new TranslatableTextContent("selectWorld.mapFeatures").method_0_5147() + " " + structures));
        this.field_2564.add(new ClickableWidget(344, this.field_2561 / 2 + 5, this.field_2559 - 64, 150, 20, new TranslatableTextContent("selectWorld.bonusItems").method_0_5147() + " " + bonusChest));
        this.field_2564.add(new ClickableWidget(345, this.field_2561 / 2 - 155, this.field_2559 - 28, 150, 20, Atum.getTranslation("menu.done", "Done").method_0_5147()));
        this.field_2564.add(new ClickableWidget(343, this.field_2561 / 2 + 5, this.field_2559 - 28, 150, 20, I18n.translate("gui.cancel")));
    }

    @Override
    public void method_2225() {
        seedField.tick();
    }

    @Override
    public void method_2214(int mouseX, int mouseY, float delta) {
        this.method_2240();
        this.method_1789(field_2563.field_1772, this.title, this.field_2561 / 2, this.field_2559 - 210, -1);
        this.method_1780(field_2563.field_1772, "Seed (Leave empty for a random Seed)", this.field_2561 / 2 - 100, this.field_2559 - 180, -6250336);

        this.seedField.method_1857();
        super.method_2214(mouseX, mouseY, delta);
    }


    @Override
    protected void method_0_2773(char c, int i) {
        if (this.seedField.method_1871()) {
            this.seedField.method_0_2506(c, i);
            this.seed = this.seedField.getText();
        }
    }

    @Override
    protected void method_0_2778(ClickableWidget button) {
        switch (button.field_2077) {
            case 340:
                isHardcore = !isHardcore;
                button.field_2074 = ("Is Hardcore: " + isHardcore);
                break;
            case 341:
                generatorType++;
                if (generatorType > 5) {
                    generatorType = 0;
                }
                button.field_2074 = new TranslatableTextContent("selectWorld.mapType").method_0_5147() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey());
                break;
            case 342:
                structures = !structures;
                button.field_2074 = (new TranslatableTextContent("selectWorld.mapFeatures").method_0_5147() + " " + structures);
                break;
            case 344:
                bonusChest = !bonusChest;
                button.field_2074 = (new TranslatableTextContent("selectWorld.bonusItems").method_0_5147() + " " + bonusChest);
                break;
            case 345:
                Atum.seed = seed;
                Atum.difficulty = isHardcore ? -1 : 0;
                Atum.structures = structures;
                Atum.bonusChest = bonusChest;
                Atum.generatorType = generatorType;
                Atum.saveProperties();
                field_2563.setScreen(parent);
                break;
            case 343:
                field_2563.setScreen(parent);
                break;
        }
    }
}
