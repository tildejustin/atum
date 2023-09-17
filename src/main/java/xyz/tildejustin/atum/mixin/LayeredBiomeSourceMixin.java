package xyz.tildejustin.atum.mixin;

import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LayeredBiomeSource;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(LayeredBiomeSource.class)
public abstract class LayeredBiomeSourceMixin {
    @Shadow
    private Layer layer;

    /**
     * @author tildejustin
     * @reason fix thread safety issue?
     */
    @Overwrite
    public Vec3i method_3855(int i, int j, int k, List<Biome> list, Random random) {
        synchronized ((LayeredBiomeSource) (Object) this) {
            int var6 = i - k >> 2;
            int var7 = j - k >> 2;
            int var8 = i + k >> 2;
            int var9 = j + k >> 2;
            int var10 = var8 - var6 + 1;
            int var11 = var9 - var7 + 1;
            int[] var12 = this.layer.method_143(var6, var7, var10, var11);
            Vec3i var13 = null;
            int var14 = 0;

            for (int var15 = 0; var15 < var12.length; ++var15) {
                int var16 = var6 + var15 % var10 << 2;
                int var17 = var7 + var15 / var10 << 2;
                Biome var18;
                try {
                    var18 = Biome.BIOMES[var12[var15]];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(Arrays.toString(var12));
                    System.out.println(var15);
                    System.out.println(Arrays.toString(Biome.BIOMES));
                    throw new RuntimeException(e);
                }
                if (list.contains(var18) && (var13 == null || random.nextInt(var14 + 1) == 0)) {
                    var13 = new Vec3i(var16, 0, var17);
                    ++var14;
                }
            }

            return var13;
        }
    }
}
