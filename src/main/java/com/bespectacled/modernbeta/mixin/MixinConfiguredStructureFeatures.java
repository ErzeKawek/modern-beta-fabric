package com.bespectacled.modernbeta.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

@Mixin(ConfiguredStructureFeatures.class)
public class MixinConfiguredStructureFeatures {
    /*
    @Inject(method = "registerAll", at = @At("TAIL"))
    private static void addStructuresToBiomes(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> consumer, CallbackInfo info) {
        // Beta Biomes
        
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("beta_cold_ocean"), false);
        OldBiomeStructures.addDesertStructures(consumer, ModernBeta.createId("beta_desert"), true);
        OldBiomeStructures.addForestStructures(consumer, ModernBeta.createId("beta_forest"));
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("beta_frozen_ocean"), false);
        OldBiomeStructures.addDesertStructures(consumer, ModernBeta.createId("beta_ice_desert"), false);
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("beta_lukewarm_ocean"), true);
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("beta_ocean"), false);
        OldBiomeStructures.addPlainsStructures(consumer, ModernBeta.createId("beta_plains"));
        OldBiomeStructures.addRainforestStructures(consumer, ModernBeta.createId("beta_rainforest"));
        OldBiomeStructures.addLowlandStructures(consumer, ModernBeta.createId("beta_savanna"));
        OldBiomeStructures.addSeasonalForest(consumer, ModernBeta.createId("beta_seasonal_forest"));
        OldBiomeStructures.addLowlandStructures(consumer, ModernBeta.createId("beta_shrubland"));
        OldBiomeStructures.addSwamplandStructures(consumer, ModernBeta.createId("beta_swampland"));
        OldBiomeStructures.addTaigaStructures(consumer, ModernBeta.createId("beta_taiga"));
        OldBiomeStructures.addTundraStructures(consumer, ModernBeta.createId("beta_tundra"));
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("beta_warm_ocean"), true);
        
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("beta_sky"));
        
        // PE Biomes
        
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("pe_cold_ocean"), false);
        OldBiomeStructures.addDesertStructures(consumer, ModernBeta.createId("pe_desert"), true);
        OldBiomeStructures.addForestStructures(consumer, ModernBeta.createId("pe_forest"));
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("pe_frozen_ocean"), false);
        OldBiomeStructures.addDesertStructures(consumer, ModernBeta.createId("pe_ice_desert"), false);
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("pe_lukewarm_ocean"), true);
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("pe_ocean"), false);
        OldBiomeStructures.addPlainsStructures(consumer, ModernBeta.createId("pe_plains"));
        OldBiomeStructures.addRainforestStructures(consumer, ModernBeta.createId("pe_rainforest"));
        OldBiomeStructures.addLowlandStructures(consumer, ModernBeta.createId("pe_savanna"));
        OldBiomeStructures.addSeasonalForest(consumer, ModernBeta.createId("pe_seasonal_forest"));
        OldBiomeStructures.addLowlandStructures(consumer, ModernBeta.createId("pe_shrubland"));
        OldBiomeStructures.addSwamplandStructures(consumer, ModernBeta.createId("pe_swampland"));
        OldBiomeStructures.addTaigaStructures(consumer, ModernBeta.createId("pe_taiga"));
        OldBiomeStructures.addTundraStructures(consumer, ModernBeta.createId("pe_tundra"));
        OldBiomeStructures.addOceanStructures(consumer, ModernBeta.createId("pe_warm_ocean"), true);
        
        // Inf Biomes
        
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("alpha"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("alpha_winter"));
        
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_227"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_227_winder"));
        
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_415"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_415_winter"));

        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_420"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_420_winter"));

        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_611"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("infdev_611_winter"));
        
        
        // Indev Biomes
        
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("indev_normal"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("indev_hell"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("indev_paradise"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("indev_snowy"));
        OldBiomeStructures.addCommonStructures(consumer, ModernBeta.createId("indev_woods"));
    }
    */
}
