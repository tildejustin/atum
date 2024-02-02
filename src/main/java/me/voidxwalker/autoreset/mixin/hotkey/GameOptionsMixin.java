package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.option.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow
    public KeyBinding[] allKeys;

    @Inject(method = "load", at = @At("TAIL"))
    private void addResetKey(CallbackInfo ci) {
        Atum.resetKey = new KeyBinding("Reset Key", 64);
        List<KeyBinding> newKeys = new ArrayList<>(Arrays.asList(allKeys));
        newKeys.add(Atum.resetKey);
        allKeys = newKeys.toArray(new KeyBinding[0]);
    }
}
