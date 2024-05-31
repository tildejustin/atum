package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.stream.IntStream;

public class AutoResetOptionScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget seedField;
    private String seed;
    private int difficulty;
    private int generatorType;
    private boolean structures;
    private boolean bonusChest;

    public AutoResetOptionScreen(@Nullable Screen parent) {
        super(Atum.getTranslation("menu.autoresetTitle", "Autoreset Options"));
        this.parent = parent;
    }

    protected void init() {
        this.seedField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height - 160, 200, 20, Atum.getTranslation("menu.enterSeed", "Enter a Seed"));
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seed = Atum.seed;
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;
        this.difficulty = Atum.difficulty;
        this.seedField.setChangedListener((string) -> this.seed = string);
        this.addDrawableChild(
                CyclingButtonWidget.<Integer>builder(value -> value == -1 ? Text.translatable("selectWorld.gameMode.hardcore") : Difficulty.byId(value).getTranslatableName())
                        .values(IntStream.range(-1, 4).boxed().sorted().toList())
                        .initially(this.difficulty)
                        .build(this.width / 2 + 5, this.height - 100, 150, 20, Text.translatable("options.difficulty"), ((button, value) -> this.difficulty = value))
        );
        this.addDrawableChild(
                CyclingButtonWidget.<Integer>builder(
                                value -> Text.translatable(Identifier.of(Atum.getGeneratorTypeString(value)).toTranslationKey("generator"))
                        )
                        .values(IntStream.range(0, 5).boxed().sorted().toList())
                        .initially(this.generatorType)
                        .build(this.width / 2 - 155, this.height - 100, 150, 20, Text.translatable("selectWorld.mapType"), (button, value) -> this.generatorType = value)
        );
        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(this.structures).build(this.width / 2 - 155, this.height - 64, 150, 20, Text.translatable("selectWorld.mapFeatures"), (button, value) -> this.structures = value)
        );
        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(this.bonusChest).build(this.width / 2 + 5, this.height - 64, 150, 20, Text.translatable("selectWorld.bonusItems"), (button, value) -> this.bonusChest = value)
        );
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), (buttonWidget) -> {
            Atum.seed = this.seed;
            Atum.difficulty = this.difficulty;
            Atum.structures = this.structures;
            Atum.bonusChest = this.bonusChest;
            Atum.generatorType = this.generatorType;
            try {
                Atum.saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (buttonWidget) -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.addSelectableChild(this.seedField);
        this.setInitialFocus(this.seedField);
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height - 210, -1);
        context.drawCenteredTextWithShadow(this.textRenderer, Atum.getTranslation("menu.enterSeed", "Seed (Leave empty for a random Seed)").getString(), this.width / 2, this.height - 180, -6250336);
        this.seedField.render(context, mouseX, mouseY, delta);
    }
}
