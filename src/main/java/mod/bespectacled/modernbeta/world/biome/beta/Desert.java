package mod.bespectacled.modernbeta.world.biome.beta;

import mod.bespectacled.modernbeta.world.biome.OldBiomeColors;
import mod.bespectacled.modernbeta.world.biome.OldBiomeFeatures;
import mod.bespectacled.modernbeta.world.biome.OldBiomeMobs;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class Desert {
    public static final Biome BIOME = create();
    
    private static Biome create() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        OldBiomeMobs.addDesertMobs(spawnSettings);
        OldBiomeMobs.addSquid(spawnSettings);
        
        GenerationSettings.Builder genSettings = new GenerationSettings.Builder();
        OldBiomeFeatures.addDesertFeatures(genSettings, false);
        
        return (new Biome.Builder())
            .precipitation(Biome.Precipitation.NONE)
            .category(Biome.Category.DESERT)
            .temperature(1.0F)
            .downfall(0.0F)
            .effects((new BiomeEffects.Builder())
                .skyColor(OldBiomeColors.BETA_WARM_SKY_COLOR)
                .fogColor(OldBiomeColors.BETA_FOG_COLOR)
                .waterColor(OldBiomeColors.VANILLA_WATER_COLOR)
                .waterFogColor(OldBiomeColors.VANILLA_WATER_FOG_COLOR)
                .build())
            .spawnSettings(spawnSettings.build())
            .generationSettings(genSettings.build())
            .build();
    }
}
