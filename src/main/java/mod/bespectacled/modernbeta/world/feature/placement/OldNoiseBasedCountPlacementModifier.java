package mod.bespectacled.modernbeta.world.feature.placement;

import java.util.Random;

import mod.bespectacled.modernbeta.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbeta.world.feature.placement.noise.BetaNoiseBasedCount;
import mod.bespectacled.modernbeta.world.feature.placement.noise.OldNoiseBasedCount;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;

public abstract class OldNoiseBasedCountPlacementModifier extends AbstractCountPlacementModifier {
    protected final int count;
    protected final double extraChance;
    protected final int extraCount;

    protected OldNoiseBasedCount noiseDecorator;
    
    protected OldNoiseBasedCountPlacementModifier(int count, double extraChance, int extraCount) {
        this.count = count;
        this.extraChance = extraChance;
        this.extraCount = extraCount;
        
        this.noiseDecorator = new BetaNoiseBasedCount(new Random(0L));
    }
    
    @Override
    protected int getCount(Random random, BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        
        return this.noiseDecorator.sample(chunkX, chunkZ, random) + this.count + ((random.nextFloat() < this.extraChance) ? this.extraCount : 0);
    }

    public abstract void setOctaves(PerlinOctaveNoise octaves);
}
