package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.Pingable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public Screen currentScreen;

    @Shadow public int width;

    @Shadow public int height;

    @Shadow public ClientWorld world;

    @Shadow public LoadingScreenRenderer loadingScreenRenderer;

    @Inject(method = "startIntegratedServer",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/ServerNetworkIo;bindLocal()Ljava/net/SocketAddress;",shift = At.Shift.BEFORE))
    public void atum_trackPostWorldGen(CallbackInfo ci){
        Atum.hotkeyState= Atum.HotkeyState.POST_WORLDGEN;
    }
    @Inject(method = "startIntegratedServer",at = @At(value = "HEAD"))
    public void atum_trackPreWorldGen( CallbackInfo ci){
        Atum.hotkeyState= Atum.HotkeyState.PRE_WORLDGEN;
    }
    @Inject(method = "tick",at = @At("HEAD"),cancellable = true)
    public void atum_tick(CallbackInfo ci){
        if(Atum.hotkeyPressed){
            if(this.loadingScreenRenderer != null && !((Pingable)(this.loadingScreenRenderer)).ping()){
                throw new IllegalStateException();
            }
            if(Atum.hotkeyState==Atum.HotkeyState.INSIDE_WORLD || Atum.hotkeyState == Atum.HotkeyState.POST_WORLDGEN){
                KeyBinding.setKeyPressed( Atum.resetKey.getCode(),false);
                Atum.hotkeyPressed=false;
                Atum.isRunning = true;
                boolean bl = MinecraftClient.getInstance().isIntegratedServerRunning();
                if (world != null) {
                    MinecraftClient.getInstance().world.disconnect();
                }
                MinecraftClient.getInstance().connect((ClientWorld)null);
                if (bl) {
                    MinecraftClient.getInstance().setScreen(new TitleScreen());
                } else {
                    MinecraftClient.getInstance().setScreen(new MultiplayerScreen(new TitleScreen()));
                }
                ci.cancel();
            }
            else if(Atum.hotkeyState==Atum.HotkeyState.OUTSIDE_WORLD){
                System.out.println(1);
                KeyBinding.setKeyPressed ( Atum.resetKey.getCode(),false);
                Atum.hotkeyPressed=false;
                Atum.isRunning=true;
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            }
        }
    }
}
