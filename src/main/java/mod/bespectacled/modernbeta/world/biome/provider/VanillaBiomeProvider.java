package mod.bespectacled.modernbeta.world.biome.provider;

import java.util.ArrayList;
import java.util.List;

import mod.bespectacled.modernbeta.api.world.biome.BiomeBlockResolver;
import mod.bespectacled.modernbeta.api.world.biome.BiomeProvider;
import mod.bespectacled.modernbeta.api.world.biome.OceanBiomeResolver;
import mod.bespectacled.modernbeta.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbeta.api.world.biome.climate.Clime;
import mod.bespectacled.modernbeta.util.chunk.ChunkCache;
import mod.bespectacled.modernbeta.util.chunk.ClimateChunk;
import mod.bespectacled.modernbeta.util.chunk.FullResBiomeChunk;
import mod.bespectacled.modernbeta.util.chunk.LowResBiomeChunk;
import mod.bespectacled.modernbeta.util.settings.Settings;
import mod.bespectacled.modernbeta.world.biome.provider.climate.BiomeClimateRules;
import mod.bespectacled.modernbeta.world.biome.vanilla.VanillaBiomeSource;
import mod.bespectacled.modernbeta.world.biome.vanilla.VanillaBiomeSourceCreator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.source.BiomeAccess;

public class VanillaBiomeProvider extends BiomeProvider implements ClimateSampler, BiomeBlockResolver, OceanBiomeResolver {
    private final VanillaBiomeSource vanillaBiomeSource;
    private final VanillaBiomeSource oceanBiomeSource;
    private final VanillaBiomeSource deepOceanBiomeSource;
    
    private final VanillaClimateSampler climateSampler;
    
    public VanillaBiomeProvider(long seed, Settings settings, Registry<Biome> biomeRegistry) {
        super(seed, settings, biomeRegistry);
        
        this.vanillaBiomeSource = VanillaBiomeSourceCreator.buildLandBiomeSource(seed, settings, biomeRegistry);
        this.oceanBiomeSource = VanillaBiomeSourceCreator.buildOceanBiomeSource(seed, settings, biomeRegistry);
        this.deepOceanBiomeSource = VanillaBiomeSourceCreator.buildDeepOceanBiomeSource(seed, settings, biomeRegistry);
        
        this.climateSampler = new VanillaClimateSampler(this.vanillaBiomeSource, biomeRegistry);
    }

    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.climateSampler.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
    
    @Override
    public RegistryEntry<Biome> getOceanBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.oceanBiomeSource.getBiome(biomeX, biomeY, biomeZ);
    }
    
    @Override
    public RegistryEntry<Biome> getDeepOceanBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.deepOceanBiomeSource.getBiome(biomeX, biomeY, biomeZ);
    }
    
    @Override
    public RegistryEntry<Biome> getBiomeAtBlock(int x, int y, int z) {
        return this.climateSampler.getBiomeAtBlock(x, z);
    }
    
    @Override
    public List<RegistryEntry<Biome>> getBiomesForRegistry() {
        List<RegistryEntry<Biome>> biomeKeys = new ArrayList<>();
        biomeKeys.addAll(this.vanillaBiomeSource.getBiomes());
        biomeKeys.addAll(this.oceanBiomeSource.getBiomes());
        biomeKeys.addAll(this.deepOceanBiomeSource.getBiomes());
        
        return biomeKeys;
    }

    @Override
    public Clime sample(int x, int z) {
        return this.climateSampler.sampleClime(x, z);
    }
    
    private static class VanillaClimateSampler implements BiomeAccess.Storage {
        private static final int BLEND_DIST = 5;
        private static final double BLEND_AREA = Math.pow(BLEND_DIST * 2 + 1, 2);
        
        private final VanillaBiomeSource biomeSource;
        private final BiomeAccess biomeAccess;
        
        private final ChunkCache<LowResBiomeChunk> lowResBiomeCache;
        private final ChunkCache<FullResBiomeChunk> fullResBiomeCache;
        private final ChunkCache<ClimateChunk> climateCache;
        private final BiomeClimateRules climateRules;
        
        @SuppressWarnings("deprecation")
        public VanillaClimateSampler(VanillaBiomeSource biomeSource, Registry<Biome> biomeRegistry) {
            this.biomeSource = biomeSource;
            this.biomeAccess = new BiomeAccess(this, biomeSource.getSeed());
            
            this.lowResBiomeCache = new ChunkCache<>(
                "biome_low_res",
                1536,
                true,
                (chunkX, chunkZ) -> new LowResBiomeChunk(
                    chunkX,
                    chunkZ,
                    (biomeX, biomeZ) -> this.biomeSource.getBiome(biomeX, 0, biomeZ)
                )
            );
            
            this.fullResBiomeCache = new ChunkCache<>(
                "biome_full_res",
                1536,
                true,
                (chunkX, chunkZ) -> new FullResBiomeChunk(
                    chunkX,
                    chunkZ,
                    (x, z) -> this.biomeAccess.getBiome(new BlockPos(x, 0, z))
                )
            );
            
            this.climateCache = new ChunkCache<>(
                "climate",
                1536,
                true,
                (chunkX, chunkZ) -> new ClimateChunk(chunkX, chunkZ, this::blendBiomeClimate)
            );

            this.climateRules = new BiomeClimateRules.Builder()
                .add(biome -> Biome.getCategory(biome) == Category.EXTREME_HILLS, () -> new Clime(1.0, 1.0))
                .add(biome -> Biome.getCategory(biome) == Category.MOUNTAIN, () -> new Clime(1.0, 1.0))
                .add(biome -> Biome.getCategory(biome) == Category.MESA, () -> new Clime(1.0, 1.0))
                .add(biome -> Biome.getCategory(biome) == Category.SWAMP, () -> new Clime(0.0, 0.0))
                .build();
        }
        
        public Clime sampleClime(int x, int z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            
            return this.climateCache.get(chunkX, chunkZ).sampleClime(x, z);
        }

        @Override
        public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
            int chunkX = biomeX >> 2;
            int chunkZ = biomeZ >> 2;
            
            return this.lowResBiomeCache.get(chunkX, chunkZ).sampleBiome(biomeX, biomeZ);
        }
        
        public RegistryEntry<Biome> getBiomeAtBlock(int x, int z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            
            return this.fullResBiomeCache.get(chunkX, chunkZ).sampleBiome(x, z);
        }
        
        private Clime blendBiomeClimate(int x, int z) {
            double temp = 0.0;
            double rain = 0.0;

            // Sample surrounding blocks
            for (int localX = -BLEND_DIST; localX <= BLEND_DIST; ++localX) {
                for (int localZ = -BLEND_DIST; localZ <= BLEND_DIST; ++localZ) {
                    int curX = localX + x;
                    int curZ = localZ + z;
                    
                    int chunkX = curX >> 4;
                    int chunkZ = curZ >> 4;
                    
                    RegistryEntry<Biome> biome = this.fullResBiomeCache.get(chunkX, chunkZ).sampleBiome(curX, curZ);
                    Clime clime = this.climateRules.apply(biome);
                    
                    temp += clime.temp();
                    rain += clime.rain();
                }
            }
            
            // Take simple average of temperature/rainfall values of surrounding blocks.
            temp /= BLEND_AREA;
            rain /= BLEND_AREA;
            
            temp = MathHelper.clamp(temp, 0.0, 1.0);
            rain = MathHelper.clamp(rain, 0.0, 1.0);
            
            return new Clime(temp, rain);
        }
    }
}
