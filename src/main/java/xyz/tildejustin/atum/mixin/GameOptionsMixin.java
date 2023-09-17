package xyz.tildejustin.atum.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.tildejustin.atum.Atum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow
    public KeyBinding[] allKeys;

    @Inject(method = "load", at = @At(value = "HEAD"))
    private void addResetKey(CallbackInfo ci) {
        List<KeyBinding> newKeys = new ArrayList<>(Arrays.asList(allKeys));
        newKeys.add(Atum.resetKey);
        allKeys = newKeys.toArray(new KeyBinding[0]);
    }
}
