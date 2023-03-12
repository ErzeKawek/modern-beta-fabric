package mod.bespectacled.modernbeta.world.cavebiome.provider;

import java.util.List;
import java.util.Random;

import mod.bespectacled.modernbeta.ModernBeta;
import mod.bespectacled.modernbeta.api.world.biome.climate.Clime;
import mod.bespectacled.modernbeta.api.world.cavebiome.CaveBiomeProvider;
import mod.bespectacled.modernbeta.api.world.cavebiome.climate.CaveClimateSampler;
import mod.bespectacled.modernbeta.util.NbtTags;
import mod.bespectacled.modernbeta.util.NbtUtil;
import mod.bespectacled.modernbeta.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbeta.util.settings.Settings;
import mod.bespectacled.modernbeta.world.biome.voronoi.VoronoiPointRules;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class VoronoiCaveBiomeProvider extends CaveBiomeProvider implements CaveClimateSampler {
    private final VoronoiCaveClimateSampler climateSampler;
    private final VoronoiPointRules<RegistryKey<Biome>, Clime> rules;

    public VoronoiCaveBiomeProvider(long seed, Settings settings, Registry<Biome> biomeRegistry) {
        super(seed, settings, biomeRegistry);
        
        int verticalNoiseScale = NbtUtil.toInt(
            settings.get(NbtTags.VERTICAL_NOISE_SCALE),
            ModernBeta.CAVE_BIOME_CONFIG.verticalNoiseScale
        );
        
        int horizontalNoiseScale = NbtUtil.toInt(
            settings.get(NbtTags.HORIZONTAL_NOISE_SCALE),
            ModernBeta.CAVE_BIOME_CONFIG.horizontalNoiseScale
        );
        
        this.climateSampler = new VoronoiCaveClimateSampler(seed, verticalNoiseScale, horizontalNoiseScale);
        this.rules = buildRules(settings);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int biomeX, int biomeY, int biomeZ) {
        Clime clime = this.sample(biomeX, biomeY, biomeZ);
        RegistryKey<Biome> biomeKey = this.rules.calculateClosestTo(clime);
        
        return biomeKey == null || biomeKey.equals(BiomeKeys.THE_VOID) ? 
            null : 
            this.biomeRegistry.getOrCreateEntry(biomeKey);
    }
    
    @Override
    public List<RegistryEntry<Biome>> getBiomesForRegistry() {
        return this.rules
            .getItems()
            .stream()
            .distinct()
            .filter(key -> !key.getValue().getPath().isBlank())
            .map(key -> this.biomeRegistry.getOrCreateEntry(key)).toList();
    }

    @Override
    public Clime sample(int x, int y, int z) {
        return this.climateSampler.sample(x, y, z);
    }
    
    private static VoronoiPointRules<RegistryKey<Biome>, Clime> buildRules(Settings settings) {
        VoronoiPointRules.Builder<RegistryKey<Biome>, Clime> builder = new VoronoiPointRules.Builder<>();
        
        NbtUtil.toListOrThrow(settings.get(NbtTags.BIOMES)).stream().forEach(e -> {
            NbtCompound compound = NbtUtil.toCompoundOrThrow(e);
            String biome = NbtUtil.readStringOrThrow(NbtTags.BIOME, compound);
            double temp = NbtUtil.readDoubleOrThrow(NbtTags.TEMP, compound);
            double rain = NbtUtil.readDoubleOrThrow(NbtTags.RAIN, compound);
            boolean nullBiome = NbtUtil.readBooleanOrThrow(NbtTags.NULL_BIOME, compound);
            
            RegistryKey<Biome> biomeKey = nullBiome ? null : RegistryKey.of(Registry.BIOME_KEY, new Identifier(biome));
            builder.add(biomeKey, new Clime(temp, rain));
        });
        
        return builder.build();
    }

    private static class VoronoiCaveClimateSampler {
        private final PerlinOctaveNoise tempNoiseOctaves;
        private final PerlinOctaveNoise rainNoiseOctaves;
        private final PerlinOctaveNoise detailNoiseOctaves;
        
        private final int verticalScale;
        private final int horizontalScale;
        
        public VoronoiCaveClimateSampler(long seed, int verticalScale, int horizontalScale) {
            this.tempNoiseOctaves = new PerlinOctaveNoise(new Random(seed * 9871L), 2, true);
            this.rainNoiseOctaves = new PerlinOctaveNoise(new Random(seed * 39811L), 2, true);
            this.detailNoiseOctaves = new PerlinOctaveNoise(new Random(seed * 543321L), 1, true);
            
            this.verticalScale = verticalScale;
            this.horizontalScale = horizontalScale;
        }
        
        public Clime sample(int x, int y, int z) {
            // 1 Octave range: -0.6240559817912857/0.6169702737066762
            // 2 Octave range: -1.4281536012354779/1.4303502066204832
            // 4 Octave range: -7.6556244276339145/7.410194314594666
            
            double tempNoise = this.tempNoiseOctaves.sample(
                x / (double)this.horizontalScale,
                y / (double)this.verticalScale,
                z / (double)this.horizontalScale
            );
            
            double rainNoise = this.rainNoiseOctaves.sample(
                x / (double)this.horizontalScale, 
                y / (double)this.verticalScale, 
                z / (double)this.horizontalScale
            );
            
            double detailNoise = this.detailNoiseOctaves.sample(
                x / (double)this.horizontalScale, 
                y / (double)this.verticalScale, 
                z / (double)this.horizontalScale
            );
            
            tempNoise /= 1.4D;
            rainNoise /= 1.4D;
            detailNoise /= 0.55D;
            
            tempNoise = tempNoise * 0.99D + detailNoise * 0.01D;
            rainNoise = rainNoise * 0.98D + detailNoise * 0.02D;
            
            tempNoise = (tempNoise + 1.0) / 2D;
            rainNoise = (rainNoise + 1.0) / 2D;
            
            return new Clime(
                MathHelper.clamp(tempNoise, 0.0, 1.0),
                MathHelper.clamp(rainNoise, 0.0, 1.0)
            );
        }
    }
}
