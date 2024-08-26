package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.mixin.GeneratorTypeAccessor;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class AutoResetOptionScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget seedField;
    private String seed;
    private int difficulty;
    private int generatorType;
    private boolean structures;
    private boolean bonusChest;
    private boolean disableHotkeyInTextboxes;

    public AutoResetOptionScreen(@Nullable Screen parent) {
        super(Text.of("Autoreset Options"));
        this.parent = parent;
    }

    protected void init() {
        int height = this.height / 4 + 48;
        int spacing = 24;
        this.client.keyboard.enableRepeatEvents(true);
        this.seedField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, height - 48, 200, 20, Text.of("Enter a Seed")) {
        };
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seed = Atum.seed;
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;
        this.difficulty = Atum.difficulty;
        this.disableHotkeyInTextboxes = Atum.disableHotkeyInTextboxes;
        this.seedField.setChangedListener((string) -> this.seed = string);
        this.addButton(new ButtonWidget(this.width / 2 - 155, height, 150, 20, new TranslatableText("selectWorld.mapType"), (buttonWidget) -> generatorType = (generatorType + 1) % GeneratorTypeAccessor.getVALUES().size()) {
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(GeneratorTypeAccessor.getVALUES().get(generatorType).getTranslationKey());
            }
        });
        this.addButton(new ButtonWidget(this.width / 2 + 5, height, 150, 20, new TranslatableText("options.difficulty"), (buttonWidget) -> this.difficulty = this.difficulty >= 3 ? -1 : this.difficulty + 1) {
            public Text getMessage() {
                if (difficulty == -1) {
                    return super.getMessage().shallowCopy().append(": ").append(new TranslatableText("selectWorld.gameMode.hardcore"));
                }
                return super.getMessage().shallowCopy().append(": ").append(Difficulty.byOrdinal(difficulty).getTranslatableName());
            }
        });

        this.addButton(new ButtonWidget(this.width / 2 - 155, height + spacing, 150, 20, new TranslatableText("selectWorld.mapFeatures"), (buttonWidget) -> this.structures = !structures) {
            public Text getMessage() {
                MutableText text = super.getMessage().shallowCopy();
                return text.append(text.getString().endsWith(":") ? " " : ": ").append(String.valueOf(structures));
            }
        });

        this.addButton(new ButtonWidget(this.width / 2 + 5, height + spacing, 150, 20, new TranslatableText("selectWorld.bonusItems"), (buttonWidget) -> this.bonusChest = !bonusChest) {
            public Text getMessage() {
                MutableText text = super.getMessage().shallowCopy();
                return text.append(text.getString().endsWith(":") ? " " : ": ").append(String.valueOf(bonusChest));
            }
        });
        this.addButton(new ButtonWidget(this.width / 2 - 155, height + spacing * 2, 150, 20, new LiteralText("Disable Hotkey in Text Boxes"), (buttonWidget) -> this.disableHotkeyInTextboxes = !this.disableHotkeyInTextboxes) {
            @Override
            public Text getMessage() {
                MutableText text = super.getMessage().shallowCopy();
                return text.append(text.getString().endsWith(":") ? " " : ": ").append(String.valueOf(disableHotkeyInTextboxes));
            }
        });

        this.addButton(new ButtonWidget(this.width / 2 - 155, height + spacing * 3 + 12, 150, 20, ScreenTexts.DONE, (buttonWidget) -> {
            Atum.seed = this.seed;
            Atum.difficulty = this.difficulty;
            Atum.structures = this.structures;
            Atum.bonusChest = this.bonusChest;
            Atum.generatorType = this.generatorType;
            Atum.disableHotkeyInTextboxes = this.disableHotkeyInTextboxes;
            try {
                Atum.saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.client.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, height + spacing * 3 + 12, 150, 20, ScreenTexts.CANCEL, (buttonWidget) -> this.client.openScreen(this.parent)));
        this.children.add(this.seedField);
        this.setInitialFocus(this.seedField);
    }

    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, -1);
        drawStringWithShadow(matrices, this.textRenderer, "Seed (Leave empty for a Random Seed)", this.width / 2 - 100, this.height / 4 - 20, -6250336);

        this.seedField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
