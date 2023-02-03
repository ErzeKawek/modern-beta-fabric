package mod.bespectacled.modernbeta.config;

import java.util.Map;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import mod.bespectacled.modernbeta.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbeta.util.NbtCompoundBuilder;
import mod.bespectacled.modernbeta.util.NbtTags;
import mod.bespectacled.modernbeta.util.NbtUtil;
import mod.bespectacled.modernbeta.world.biome.ModernBetaBiomes;
import mod.bespectacled.modernbeta.world.biome.provider.climate.BetaClimateMapping.ClimateType;
import net.minecraft.nbt.NbtCompound;

@Config(name = "config_biome")
public class ModernBetaConfigBiome implements ConfigData {
    public String biomeProvider = ModernBetaBuiltInTypes.Biome.BETA.name;
    public String singleBiome = ModernBetaBiomes.ALPHA_ID.toString();
    public boolean replaceOceanBiomes = true;
    
    public float tempNoiseScale = 1.0f;
    public float rainNoiseScale = 1.0f;
    public float detailNoiseScale = 1.0f;
    
    public Map<String, ConfigClimateMapping> climates = createClimateMapping(
        new ConfigClimateMapping(ModernBetaBiomes.BETA_DESERT_ID.toString(), ModernBetaBiomes.BETA_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_FOREST_ID.toString(), ModernBetaBiomes.BETA_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_TUNDRA_ID.toString(), ModernBetaBiomes.BETA_FROZEN_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_PLAINS_ID.toString(), ModernBetaBiomes.BETA_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_RAINFOREST_ID.toString(), ModernBetaBiomes.BETA_WARM_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_SAVANNA_ID.toString(), ModernBetaBiomes.BETA_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_SHRUBLAND_ID.toString(), ModernBetaBiomes.BETA_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_SEASONAL_FOREST_ID.toString(), ModernBetaBiomes.BETA_LUKEWARM_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_SWAMPLAND_ID.toString(), ModernBetaBiomes.BETA_COLD_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_TAIGA_ID.toString(), ModernBetaBiomes.BETA_FROZEN_OCEAN_ID.toString()),
        new ConfigClimateMapping(ModernBetaBiomes.BETA_TUNDRA_ID.toString(), ModernBetaBiomes.BETA_FROZEN_OCEAN_ID.toString())
    );
    
    private static Map<String, ConfigClimateMapping> createClimateMapping(
        ConfigClimateMapping desert,
        ConfigClimateMapping forest,
        ConfigClimateMapping iceDesert,
        ConfigClimateMapping plains,
        ConfigClimateMapping rainforest,
        ConfigClimateMapping savanna,
        ConfigClimateMapping shrubland,
        ConfigClimateMapping seasonal_forest,
        ConfigClimateMapping swampland,
        ConfigClimateMapping taiga,
        ConfigClimateMapping tundra
    ) {
        return Map.ofEntries(
            Map.entry("desert", desert),
            Map.entry("forest", forest),
            Map.entry("ice_desert", iceDesert),
            Map.entry("plains", plains),
            Map.entry("rainforest", rainforest),
            Map.entry("savanna", savanna),
            Map.entry("shrubland", shrubland),
            Map.entry("seasonal_forest", seasonal_forest),
            Map.entry("swampland", swampland),
            Map.entry("taiga", taiga),
            Map.entry("tundra", tundra)
        );
    }
    
    public static class ConfigClimateMapping {
        public String biome;
        public String oceanBiome;
        public String deepOceanBiome;
        
        public ConfigClimateMapping(String biome, String oceanBiome) {
            this(biome, oceanBiome, oceanBiome);
        }
        
        public ConfigClimateMapping(String biome, String oceanBiome, String deepOceanBiome) {
            this.biome = biome;
            this.oceanBiome = oceanBiome;
            this.deepOceanBiome = deepOceanBiome;
        }
        
        public String biomeByClimateType(ClimateType type) {
            return switch(type) {
                case LAND -> this.biome;
                case OCEAN -> this.oceanBiome;
                case DEEP_OCEAN -> this.deepOceanBiome;
            };
        }
        
        public void setBiomeByClimateType(String biome, ClimateType type) {
            switch(type) {
                case LAND -> this.biome = biome;
                case OCEAN -> this.oceanBiome = biome;
                case DEEP_OCEAN -> this.deepOceanBiome = biome;
            }
        }
        
        public NbtCompound toCompound() {
            return new NbtCompoundBuilder()
                .putString(NbtTags.BIOME, this.biome)
                .putString(NbtTags.OCEAN_BIOME, this.oceanBiome)
                .putString(NbtTags.DEEP_OCEAN_BIOME, this.deepOceanBiome)
                .build();
        }
        
        public static ConfigClimateMapping fromCompound(NbtCompound compound) {
            return new ConfigClimateMapping(
                NbtUtil.readStringOrThrow(NbtTags.BIOME, compound),
                NbtUtil.readStringOrThrow(NbtTags.OCEAN_BIOME, compound),
                NbtUtil.readStringOrThrow(NbtTags.DEEP_OCEAN_BIOME, compound)
            );
        }
    }
}
