package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

public class AutoResetOptionScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget seedField;
    private boolean isHardcore;
    private int generatorType;
    private boolean structures;
    private final String title;
    private boolean bonusChest;

    public AutoResetOptionScreen(@Nullable Screen parent) {
        super();
        title = Atum.getTranslation("menu.autoresetTitle", "Autoreset Options").getString();
        this.parent = parent;
    }

    @Override
    public void init() {
        this.isHardcore = Atum.difficulty == -1;
        this.seedField = new TextFieldWidget(350, this.client.textRenderer, this.width / 2 - 100, this.height - 160, 200, 20);
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seedField.setFocused(true);
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;

        this.addButton(new ButtonWidget(340, this.width / 2 + 5, this.height - 100, 150, 20, "Is Hardcore: " + isHardcore) {
            @Override
            public void method_18374(double d, double e) {
                isHardcore = !isHardcore;
                this.message = ("Is Hardcore: " + isHardcore);
            }
        });
        this.addButton(new ButtonWidget(341, this.width / 2 - 155, this.height - 100, 150, 20, new TranslatableText("selectWorld.mapType").getString() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey())) {
            @Override
            public void method_18374(double d, double e) {
                generatorType = (generatorType + 1) % 5;
                this.message = new TranslatableText("selectWorld.mapType").getString() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey());
            }
        });
        this.addButton(new ButtonWidget(342, this.width / 2 - 155, this.height - 64, 150, 20, new TranslatableText("selectWorld.mapFeatures").getString() + " " + structures) {
            @Override
            public void method_18374(double d, double e) {
                structures = !structures;
                this.message = (new TranslatableText("selectWorld.mapFeatures").getString() + " " + structures);
            }
        });
        this.addButton(new ButtonWidget(344, this.width / 2 + 5, this.height - 64, 150, 20, new TranslatableText("selectWorld.bonusItems").getString() + " " + bonusChest) {
            @Override
            public void method_18374(double d, double e) {
                bonusChest = !bonusChest;
                this.message = (new TranslatableText("selectWorld.bonusItems").getString() + " " + bonusChest);
            }
        });
        this.addButton(new ButtonWidget(345, this.width / 2 - 155, this.height - 28, 150, 20, Atum.getTranslation("menu.done", "Done").getString()) {
            @Override
            public void method_18374(double d, double e) {
                Atum.seed = seedField.getText().trim();
                Atum.difficulty = isHardcore ? -1 : 2;
                Atum.structures = structures;
                Atum.bonusChest = bonusChest;
                Atum.generatorType = generatorType;
                Atum.saveProperties();
                client.setScreen(parent);
            }
        });
        this.addButton(new ButtonWidget(343, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
            @Override
            public void method_18374(double d, double e) {
                AutoResetOptionScreen.this.client.setScreen(parent);
            }
        });
    }

    public void tick() {
        seedField.tick();
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        drawCenteredString(client.textRenderer, this.title, this.width / 2, this.height - 210, -1);
        this.drawWithShadow(client.textRenderer, "Seed (Leave empty for a random Seed)", this.width / 2 - 100, this.height - 180, -6250336);
        this.seedField.method_18385(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (super.mouseClicked(d, e, i)) {
            return true;
        }
        return seedField.mouseClicked(d, e, i);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.seedField.isFocused()) {
            this.seedField.charTyped(c, i);
            return true;
        }
        return super.charTyped(c, i);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.seedField.isFocused()) {
            this.seedField.keyPressed(i, j, k);
        }
        return true;
    }
}
