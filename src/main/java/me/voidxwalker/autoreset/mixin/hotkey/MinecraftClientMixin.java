package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.AnchialeAPI;
import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.Pingable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.realms.RealmsBridge;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientWorld world;

    @Shadow @Nullable public Screen currentScreen;

    @Shadow public abstract boolean isInSingleplayer();



    @Shadow public abstract void connect(@Nullable ClientWorld world);


    @Shadow public abstract boolean isIntegratedServerRunning();

    @Shadow public abstract void connect(@Nullable ClientWorld world, String loadingMessage);

    @Shadow public LoadingScreenRenderer loadingScreenRenderer;

    @Shadow public abstract void setScreen(Screen screen);

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
        if (this.loadingScreenRenderer != null && !((Pingable)this.loadingScreenRenderer).ping()) {
            throw new IllegalStateException();
        }
        if(Atum.hotkeyPressed){
            if(Atum.hotkeyState==Atum.HotkeyState.INSIDE_WORLD || Atum.hotkeyState == Atum.HotkeyState.POST_WORLDGEN){
                if (Atum.HAS_ANCHIALE) {
                    AnchialeAPI.setAnchialeFastReset(true);
                }
                KeyBinding.setKeyPressed( Atum.resetKey.getCode(),false);
                Atum.hotkeyPressed=false;
                Atum.isRunning = true;
                Atum.loopPrevent2=true;
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.connect(null);
                this.setScreen(new TitleScreen());
                if (Atum.HAS_ANCHIALE) {
                    AnchialeAPI.setAnchialeFastReset(false);
                }
            }
            else if(Atum.hotkeyState==Atum.HotkeyState.OUTSIDE_WORLD){
                KeyBinding.setKeyPressed ( Atum.resetKey.getCode(),false);
                Atum.hotkeyPressed=false;
                Atum.isRunning=true;
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            }
        }
    }
}
