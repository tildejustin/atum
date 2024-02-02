package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.StringTextComponent;
import net.minecraft.world.Difficulty;

import java.util.Arrays;

public class AtumOptionsScreen extends Screen {
    private ButtonWidget hardcoreButton;
    private ButtonWidget difficultyButton;

    public AtumOptionsScreen() {
        super(new StringTextComponent("Atum Options"));
    }

    protected void init() {
        int j = this.height / 4;
        this.hardcoreButton = this.addButton(new ButtonWidget(
                this.width / 2 - 100, j, 200, 20, "",
                (buttonWidget) -> {
                    Atum.hardcore = !Atum.hardcore;
                    Atum.save();
                    this.updateLabels();
                }
        ));
        if (Atum.difficulty != Difficulty.HARD) {
            this.hardcoreButton.active = false;
            Atum.hardcore = false;
        }
        this.difficultyButton = this.addButton(new ButtonWidget(
                this.width / 2 - 100, j + 24, 200, 20, "",
                (buttonWidget) -> {
                    Atum.difficulty = Difficulty.values()[(Arrays.asList(Difficulty.values()).indexOf(Atum.difficulty) + 1) % Difficulty.values().length];
                    if (Atum.difficulty == Difficulty.HARD) hardcoreButton.active = true;
                    else {
                        hardcoreButton.active = false;
                        Atum.hardcore = false;
                    }
                    Atum.save();
                    this.updateLabels();
                })
        );
        this.addButton(new ButtonWidget(
                this.width / 2 - 100, j + 24 + 24, 200, 20, I18n.translate("gui.done"),
                (buttonWidget) -> this.minecraft.openScreen(null))
        );
        this.updateLabels();
    }

    private void updateLabels() {
        hardcoreButton.setMessage("Hardcore: " + Atum.hardcore);
        difficultyButton.setMessage("Difficulty: " + I18n.translate(Atum.difficulty.getTranslationKey()));
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.minecraft.textRenderer, this.title.getString(), this.width / 2, this.height - 210, -1);
        super.render(mouseX, mouseY, delta);
    }
}
