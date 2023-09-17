package xyz.tildejustin.atum.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Mutable
    @Shadow
    @Final
    public List<ServerPlayerEntity> players;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void threadSafePlayers(MinecraftServer par1, CallbackInfo ci) {
        this.players = new CopyOnWriteArrayList<>();
    }
}
