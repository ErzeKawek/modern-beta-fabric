package mod.bespectacled.modernbeta.world.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mod.bespectacled.modernbeta.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbeta.world.feature.placement.noise.Infdev415NoiseBasedCount;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class Infdev415NoiseBasedCountPlacementModifier extends OldNoiseBasedCountPlacementModifier {
    public static final Codec<Infdev415NoiseBasedCountPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("count").forGetter(arg -> arg.count),
            Codec.DOUBLE.fieldOf("extra_chance").forGetter(arg -> arg.extraChance),
            Codec.INT.fieldOf("extra_count").forGetter(arg -> arg.extraCount)
        ).apply(instance, Infdev415NoiseBasedCountPlacementModifier::of));
    
    protected Infdev415NoiseBasedCountPlacementModifier(int count, double extraChance, int extraCount) {
        super(count, extraChance, extraCount);
    }
    
    public static Infdev415NoiseBasedCountPlacementModifier of(int count, double extraChance, int extraCount) {
        return new Infdev415NoiseBasedCountPlacementModifier(count, extraChance, extraCount);
    }
    
    @Override
    public void setOctaves(PerlinOctaveNoise octaves) {
        this.noiseDecorator = new Infdev415NoiseBasedCount(octaves);
    }
    
    @Override
    public PlacementModifierType<?> getType() {
        return OldPlacementTypes.INFDEV_415_NOISE_BASED_COUNT;
    }

}
