package com.bespectacled.modernbeta.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bespectacled.modernbeta.ModernBeta;
import com.bespectacled.modernbeta.api.registry.BuiltInTypes;
import com.bespectacled.modernbeta.api.registry.Registries;
import com.bespectacled.modernbeta.api.world.biome.BiomeBlockResolver;
import com.bespectacled.modernbeta.api.world.biome.BiomeProvider;
import com.bespectacled.modernbeta.api.world.biome.OceanBiomeResolver;
import com.bespectacled.modernbeta.api.world.cavebiome.CaveBiomeProvider;
import com.bespectacled.modernbeta.util.NbtTags;
import com.bespectacled.modernbeta.util.NbtUtil;
import com.bespectacled.modernbeta.world.cavebiome.provider.settings.CaveBiomeProviderSettings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class OldBiomeSource extends BiomeSource {
    public static final Codec<OldBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(
            Codec.LONG.fieldOf("seed").stable().forGetter(biomeSource -> biomeSource.seed),
            RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(biomeSource -> biomeSource.biomeRegistry),
            NbtCompound.CODEC.fieldOf("provider_settings").forGetter(biomeSource -> biomeSource.biomeSettings),
            NbtCompound.CODEC.optionalFieldOf("cave_provider_settings").forGetter(biomeSource -> biomeSource.caveBiomeSettings)
        ).apply(instance, (instance).stable(OldBiomeSource::new)));
    
    private final long seed;
    private final Registry<Biome> biomeRegistry;
    private final NbtCompound biomeSettings;
    private final Optional<NbtCompound> caveBiomeSettings;
    
    private final BiomeProvider biomeProvider;
    private final CaveBiomeProvider caveBiomeProvider;

    private static List<Biome> getBiomesForRegistry(
        long seed,
        Registry<Biome> biomeRegistry, 
        NbtCompound biomeSettings, 
        Optional<NbtCompound> caveBiomeSettings
    ) {
        NbtCompound caveSettings = caveBiomeSettings.orElse(
            CaveBiomeProviderSettings.createSettingsDefault(BuiltInTypes.CaveBiome.NONE.name)
        );
        
        List<Biome> mainBiomes = Registries.BIOME.get(NbtUtil.readStringOrThrow(NbtTags.BIOME_TYPE, biomeSettings))
            .apply(seed, biomeSettings, biomeRegistry)
            .getBiomesForRegistry();
        
        List<Biome> caveBiomes = Registries.CAVE_BIOME.get(NbtUtil.readStringOrThrow(NbtTags.CAVE_BIOME_TYPE, caveSettings))
            .apply(seed, caveSettings, biomeRegistry)
            .getBiomesForRegistry();
        
        List<Biome> biomes = new ArrayList<>();
        biomes.addAll(mainBiomes);
        biomes.addAll(caveBiomes);
        
        return biomes;
    }
    
    public OldBiomeSource(long seed, Registry<Biome> biomeRegistry, NbtCompound biomeSettings, Optional<NbtCompound> caveBiomeSettings) {
        super(getBiomesForRegistry(seed, biomeRegistry, biomeSettings, caveBiomeSettings));
        
        this.seed = seed;
        this.biomeRegistry = biomeRegistry;
        this.biomeSettings = biomeSettings;
        this.caveBiomeSettings = caveBiomeSettings;
        
        NbtCompound caveSettings = this.caveBiomeSettings.orElse(
            CaveBiomeProviderSettings.createSettingsDefault(BuiltInTypes.CaveBiome.NONE.name)
        );
        
        this.biomeProvider = Registries.BIOME
            .get(NbtUtil.readStringOrThrow(NbtTags.BIOME_TYPE, biomeSettings))
            .apply(seed, biomeSettings, biomeRegistry);
        this.caveBiomeProvider = Registries.CAVE_BIOME
            .get(NbtUtil.readStringOrThrow(NbtTags.CAVE_BIOME_TYPE, caveSettings))
            .apply(seed, caveSettings, biomeRegistry);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public BiomeSource withSeed(long seed) {
        return new OldBiomeSource(seed, this.biomeRegistry, this.biomeSettings, this.caveBiomeSettings);
    }
    
    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return OldBiomeSource.CODEC;
    }
    
    public Biome getBiome(int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {    
        return this.biomeProvider.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    public Biome getOceanBiome(int biomeX, int biomeY, int biomeZ) {
        if (this.biomeProvider instanceof OceanBiomeResolver oceanBiomeResolver)
            return oceanBiomeResolver.getOceanBiomeForNoiseGen(biomeX, biomeY, biomeZ);
        
        return this.biomeProvider.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
    
    public Biome getDeepOceanBiome(int biomeX, int biomeY, int biomeZ) {
        if (this.biomeProvider instanceof OceanBiomeResolver oceanBiomeResolver)
            return oceanBiomeResolver.getDeepOceanBiomeForNoiseGen(biomeX, biomeY, biomeZ);
        
        return this.biomeProvider.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
    
    public Biome getCaveBiome(int biomeX, int biomeY, int biomeZ) {
        return this.caveBiomeProvider.getBiome(biomeX, biomeY, biomeZ);
    }
    
    public Biome getBiomeForSurfaceGen(int x, int y, int z) {
        if (this.biomeProvider instanceof BiomeBlockResolver biomeResolver) {
            return biomeResolver.getBiomeAtBlock(x, y, z);
        }
        
        return this.biomeProvider.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2);
    }
    
    public Biome getBiomeForSurfaceGen(ChunkRegion region, BlockPos pos) {
        if (this.biomeProvider instanceof BiomeBlockResolver biomeResolver)
            return biomeResolver.getBiomeAtBlock(pos.getX(), pos.getY(), pos.getZ());
        
        return region.getBiome(pos);
    }
    
    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }
    
    public BiomeProvider getBiomeProvider() {
        return this.biomeProvider;
    }
    
    public CaveBiomeProvider getCaveBiomeProvider() {
        return this.caveBiomeProvider;
    }
    
    public NbtCompound getBiomeSettings() {
        return new NbtCompound().copyFrom(this.biomeSettings);
    }
    
    public NbtCompound getCaveBiomeSettings() {
        if (this.caveBiomeSettings.isPresent())
          return new NbtCompound().copyFrom(this.caveBiomeSettings.get());
        
        return new NbtCompound();
    }

    public static void register() {
        Registry.register(Registry.BIOME_SOURCE, ModernBeta.createId("old"), CODEC);
    }
}