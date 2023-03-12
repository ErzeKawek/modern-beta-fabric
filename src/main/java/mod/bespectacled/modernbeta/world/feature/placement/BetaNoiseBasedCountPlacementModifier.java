package mod.bespectacled.modernbeta.world.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mod.bespectacled.modernbeta.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbeta.world.feature.placement.noise.BetaNoiseBasedCount;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class BetaNoiseBasedCountPlacementModifier extends OldNoiseBasedCountPlacementModifier {
    public static final Codec<BetaNoiseBasedCountPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("count").forGetter(arg -> arg.count),
            Codec.DOUBLE.fieldOf("extra_chance").forGetter(arg -> arg.extraChance),
            Codec.INT.fieldOf("extra_count").forGetter(arg -> arg.extraCount)
        ).apply(instance, BetaNoiseBasedCountPlacementModifier::of));
    
    protected BetaNoiseBasedCountPlacementModifier(int count, double extraChance, int extraCount) {
        super(count, extraChance, extraCount);
    }
    
    public static BetaNoiseBasedCountPlacementModifier of(int count, double extraChance, int extraCount) {
        return new BetaNoiseBasedCountPlacementModifier(count, extraChance, extraCount);
    }
    
    @Override
    public void setOctaves(PerlinOctaveNoise octaves) {
        this.noiseDecorator = new BetaNoiseBasedCount(octaves);
    }
    
    @Override
    public PlacementModifierType<?> getType() {
        return OldPlacementTypes.BETA_NOISE_BASED_COUNT;
    }

}
