package mod.bespectacled.modernbeta.world.biome.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import mod.bespectacled.modernbeta.api.world.biome.BiomeProvider;
import mod.bespectacled.modernbeta.api.world.biome.BiomeResolverBlock;
import mod.bespectacled.modernbeta.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbeta.api.world.biome.climate.Clime;
import mod.bespectacled.modernbeta.util.chunk.ChunkCache;
import mod.bespectacled.modernbeta.util.chunk.ChunkClimate;
import mod.bespectacled.modernbeta.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbeta.world.biome.provider.climate.ClimateMapping;
import mod.bespectacled.modernbeta.world.biome.provider.climate.ClimateType;
import mod.bespectacled.modernbeta.world.biome.voronoi.VoronoiPointBiome;
import mod.bespectacled.modernbeta.world.biome.voronoi.VoronoiPointRules;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class BiomeProviderVoronoi extends BiomeProvider implements BiomeResolverBlock, BiomeResolverOcean {
    private final VoronoiClimateSampler climateSampler;
    private final VoronoiPointRules<ClimateMapping, Clime> rules;
    
    public BiomeProviderVoronoi(NbtCompound settings, RegistryEntryLookup<Biome> biomeRegistry, long seed) {
        super(settings, biomeRegistry, seed);
        
        this.climateSampler = new VoronoiClimateSampler(
            seed,
            this.settings.climateTempNoiseScale,
            this.settings.climateRainNoiseScale,
            this.settings.climateDetailNoiseScale
        );
        this.rules = buildRules(this.settings.voronoiPoints);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int biomeX, int biomeY, int biomeZ) {
        int x = biomeX << 2;
        int z = biomeZ << 2;
        
        Clime clime = this.climateSampler.sample(x, z);
        ClimateMapping climateMapping = this.rules.calculateClosestTo(clime);
        
        return this.biomeRegistry.getOrThrow(climateMapping.getBiome(ClimateType.LAND));
    }
 
    @Override
    public RegistryEntry<Biome> getOceanBiome(int biomeX, int biomeY, int biomeZ) {
        int x = biomeX << 2;
        int z = biomeZ << 2;
        
        Clime clime = this.climateSampler.sample(x, z);
        ClimateMapping climateMapping = this.rules.calculateClosestTo(clime);
        
        return this.biomeRegistry.getOrThrow(climateMapping.getBiome(ClimateType.OCEAN));
    }
    
    @Override
    public RegistryEntry<Biome> getDeepOceanBiome(int biomeX, int biomeY, int biomeZ) {
        int x = biomeX << 2;
        int z = biomeZ << 2;
        
        Clime clime = this.climateSampler.sample(x, z);
        ClimateMapping climateMapping = this.rules.calculateClosestTo(clime);
        
        return this.biomeRegistry.getOrThrow(climateMapping.getBiome(ClimateType.DEEP_OCEAN));
    }

    @Override
    public RegistryEntry<Biome> getBiomeBlock(int x, int y, int z) {
        Clime clime = this.climateSampler.sample(x, z);
        ClimateMapping climateMapping = this.rules.calculateClosestTo(clime);
        
        return this.biomeRegistry.getOrThrow(climateMapping.getBiome(ClimateType.LAND));
    }

    @Override
    public List<RegistryEntry<Biome>> getBiomes() {
        List<RegistryEntry<Biome>> biomes = new ArrayList<>();
        biomes.addAll(this.rules.getItems().stream().distinct().map(key -> this.biomeRegistry.getOrThrow(key.getBiome(ClimateType.LAND))).collect(Collectors.toList()));
        biomes.addAll(this.rules.getItems().stream().distinct().map(key -> this.biomeRegistry.getOrThrow(key.getBiome(ClimateType.OCEAN))).collect(Collectors.toList()));
        biomes.addAll(this.rules.getItems().stream().distinct().map(key -> this.biomeRegistry.getOrThrow(key.getBiome(ClimateType.DEEP_OCEAN))).collect(Collectors.toList()));
        
        return biomes;
    }
    
    private static VoronoiPointRules<ClimateMapping, Clime> buildRules(List<VoronoiPointBiome> points) {
        VoronoiPointRules.Builder<ClimateMapping, Clime> builder = new VoronoiPointRules.Builder<>();
        
        for (VoronoiPointBiome point : points) {
            String biome = point.biome().isBlank() ? null : point.biome();
            String oceanBiome = point.oceanBiome().isBlank() ? null : point.oceanBiome();
            String deepOceanBiome = point.deepOceanBiome().isBlank() ? null : point.deepOceanBiome();
            
            builder.add(new ClimateMapping(biome, oceanBiome, deepOceanBiome), new Clime(point.temp(), point.rain()));
        }
        
        return builder.build();
    }
    
    private static class VoronoiClimateSampler {
        private final SimplexOctaveNoise tempOctaveNoise;
        private final SimplexOctaveNoise rainOctaveNoise;
        private final SimplexOctaveNoise detailOctaveNoise;
        
        private final ChunkCache<ChunkClimate> chunkCacheClimate;
        
        private final double tempNoiseScale;
        private final double rainNoiseScale;
        private final double detailNoiseScale;
        
        public VoronoiClimateSampler(long seed, double tempNoiseScale, double rainNoiseScale, double detailNoiseScale) {
            this.tempOctaveNoise = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
            this.rainOctaveNoise = new SimplexOctaveNoise(new Random(seed * 39811L), 4);
            this.detailOctaveNoise = new SimplexOctaveNoise(new Random(seed * 543321L), 2);
            
            this.chunkCacheClimate = new ChunkCache<>("climate", (chunkX, chunkZ) -> new ChunkClimate(chunkX, chunkZ, this::sampleNoise));
            
            this.tempNoiseScale = tempNoiseScale;
            this.rainNoiseScale = rainNoiseScale;
            this.detailNoiseScale = detailNoiseScale;
        }

        public Clime sample(int x, int z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            
            return this.chunkCacheClimate.get(chunkX, chunkZ).sampleClime(x, z);
        }
        
        public Clime sampleNoise(int x, int z) {
            double temp = this.tempOctaveNoise.sample(x, z, this.tempNoiseScale, this.tempNoiseScale, 0.25D);
            double rain = this.rainOctaveNoise.sample(x, z, this.rainNoiseScale, this.rainNoiseScale, 0.33333333333333331D);
            double detail = this.detailOctaveNoise.sample(x, z, this.detailNoiseScale, this.detailNoiseScale, 0.58823529411764708D);

            detail = detail * 1.1D + 0.5D;

            temp = (temp * 0.15D + 0.7D) * 0.99D + detail * 0.01D;
            rain = (rain * 0.15D + 0.5D) * 0.998D + detail * 0.002D;

            temp = 1.0D - (1.0D - temp) * (1.0D - temp);
            
            return new Clime(MathHelper.clamp(temp, 0.0, 1.0), MathHelper.clamp(rain, 0.0, 1.0));
        }
    }
}
