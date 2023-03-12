package mod.bespectacled.modernbeta.world.feature.placed;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbeta.world.feature.configured.OldVegetationConfiguredFeatures;
import mod.bespectacled.modernbeta.world.feature.placement.AlphaNoiseBasedCountPlacementModifier;
import mod.bespectacled.modernbeta.world.feature.placement.BetaNoiseBasedCountPlacementModifier;
import mod.bespectacled.modernbeta.world.feature.placement.HeightmapSpreadDoublePlacementModifier;
import mod.bespectacled.modernbeta.world.feature.placement.Infdev415NoiseBasedCountPlacementModifier;
import mod.bespectacled.modernbeta.world.feature.placement.Infdev420NoiseBasedCountPlacementModifier;
import mod.bespectacled.modernbeta.world.feature.placement.Infdev611NoiseBasedCountPlacementModifier;
import mod.bespectacled.modernbeta.world.feature.placement.OldNoiseBasedCountPlacementModifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceWaterDepthFilterPlacementModifier;

public class OldVegetationPlacedFeatures {
    public static final PlacementModifier SURFACE_WATER_DEPTH_MODIFIER = SurfaceWaterDepthFilterPlacementModifier.of(0);
    public static final PlacementModifier HEIGHTMAP_SPREAD_DOUBLE_MODIFIER = HeightmapSpreadDoublePlacementModifier.of(Heightmap.Type.MOTION_BLOCKING);
    public static final PlacementModifier HEIGHT_RANGE_128 = HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(127));
    public static final PlacementModifier WORLD_SURFACE_WG_HEIGHTMAP = PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP;
    public static final PlacementModifier MOTION_BLOCKING_HEIGHTMAP = PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP;
    
    private static ImmutableList.Builder<PlacementModifier> withBaseTreeModifiers(PlacementModifier modifier) {
        return ImmutableList.<PlacementModifier>builder()
            .add(modifier)
            .add(SquarePlacementModifier.of())
            .add(SURFACE_WATER_DEPTH_MODIFIER)
            .add(PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP)
            .add(BiomePlacementModifier.of());
    }
    
    private static ImmutableList.Builder<PlacementModifier> withBaseModifiers(PlacementModifier modifier) {
        return ImmutableList.<PlacementModifier>builder()
            .add(modifier)
            .add(SquarePlacementModifier.of())
            .add(BiomePlacementModifier.of());
    }

    public static List<PlacementModifier> withTreeModifier(PlacementModifier modifier) {
        return withBaseTreeModifiers(modifier).build();
    }
    
    public static List<PlacementModifier> withCountModifier(int count) {
        return withBaseModifiers(CountPlacementModifier.of(count)).build();
    }
    
    public static List<PlacementModifier> withCountExtraAndTreeModifier(int count, float extraChance, int extraCount) {
        return withTreeModifier(PlacedFeatures.createCountExtraModifier(count, extraChance, extraCount));
    }
    
    public static PlacementModifier withCountExtraModifier(int count, float extraChance, int extraCount) {
        return PlacedFeatures.createCountExtraModifier(count, extraChance, extraCount);
    }
    
    public static List<PlacementModifier> withNoiseBasedCountModifier(String id, OldNoiseBasedCountPlacementModifier modifier) {
        return withBaseTreeModifiers(modifier).build();
    }
    
    // Shrubs
    public static final RegistryEntry<PlacedFeature> PATCH_CACTUS_ALPHA = OldPlacedFeatures.register("patch_cactus", VegetationConfiguredFeatures.PATCH_CACTUS, CountPlacementModifier.of(2), SquarePlacementModifier.of(), HEIGHTMAP_SPREAD_DOUBLE_MODIFIER, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_CACTUS_PE = OldPlacedFeatures.register("patch_cactus_pe", VegetationConfiguredFeatures.PATCH_CACTUS, CountPlacementModifier.of(5), SquarePlacementModifier.of(), HEIGHTMAP_SPREAD_DOUBLE_MODIFIER, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> MUSHROOM_HELL = OldPlacedFeatures.register("mushroom_hell", OldVegetationConfiguredFeatures.MUSHROOM_HELL, CountPlacementModifier.of(1), SquarePlacementModifier.of(), MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    // Flowers
    public static final RegistryEntry<PlacedFeature> PATCH_DANDELION_2 = OldPlacedFeatures.register("patch_dandelion_2", OldVegetationConfiguredFeatures.PATCH_DANDELION, CountPlacementModifier.of(2), SquarePlacementModifier.of(), HEIGHT_RANGE_128, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_DANDELION_3 = OldPlacedFeatures.register("patch_dandelion_3", OldVegetationConfiguredFeatures.PATCH_DANDELION, CountPlacementModifier.of(3), SquarePlacementModifier.of(), HEIGHT_RANGE_128, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_DANDELION_4 = OldPlacedFeatures.register("patch_dandelion_4", OldVegetationConfiguredFeatures.PATCH_DANDELION, CountPlacementModifier.of(4), SquarePlacementModifier.of(), HEIGHT_RANGE_128, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_DANDELION = OldPlacedFeatures.register("patch_dandelion", OldVegetationConfiguredFeatures.PATCH_DANDELION, withCountExtraModifier(0, 0.5f, 1), SquarePlacementModifier.of(), HEIGHT_RANGE_128, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_POPPY = OldPlacedFeatures.register("patch_poppy", OldVegetationConfiguredFeatures.PATCH_POPPY, withCountExtraModifier(0, 0.5f, 1), SquarePlacementModifier.of(), HEIGHT_RANGE_128, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_FLOWER_PARADISE = OldPlacedFeatures.register("patch_flower_paradise", VegetationConfiguredFeatures.FLOWER_DEFAULT, CountPlacementModifier.of(20), SquarePlacementModifier.of(), HEIGHT_RANGE_128, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_DANDELION_INFDEV_227 = OldPlacedFeatures.register("patch_dandelion_infdev_227", OldVegetationConfiguredFeatures.PATCH_DANDELION_INFDEV_227, CountPlacementModifier.of(UniformIntProvider.create(0, 10)), SquarePlacementModifier.of(), SURFACE_WATER_DEPTH_MODIFIER, PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of());
    
    // Grass
    public static final RegistryEntry<PlacedFeature> PATCH_GRASS_PLAINS_10 = OldPlacedFeatures.register("patch_grass_plains_10",OldVegetationConfiguredFeatures.PATCH_GRASS, CountPlacementModifier.of(10), SquarePlacementModifier.of(), WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_GRASS_TAIGA_1 = OldPlacedFeatures.register("patch_grass_taiga_1", OldVegetationConfiguredFeatures.PATCH_GRASS, CountPlacementModifier.of(1), SquarePlacementModifier.of(), WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_GRASS_RAINFOREST_10 = OldPlacedFeatures.register("patch_grass_rainforest_10", OldVegetationConfiguredFeatures.PATCH_GRASS_LUSH, CountPlacementModifier.of(10), SquarePlacementModifier.of(), WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PATCH_GRASS_ALPHA_2 = OldPlacedFeatures.register("patch_grass_alpha_2", OldVegetationConfiguredFeatures.PATCH_GRASS, withCountExtraModifier(0, 0.1f, 1), SquarePlacementModifier.of(), WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    
    // Classic Trees
    public static final RegistryEntry<PlacedFeature> TREES_ALPHA = OldPlacedFeatures.register("trees_alpha", OldVegetationConfiguredFeatures.TREES_ALPHA, withNoiseBasedCountModifier("trees_alpha", AlphaNoiseBasedCountPlacementModifier.of(0, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_611 = OldPlacedFeatures.register("trees_infdev_611", OldVegetationConfiguredFeatures.TREES_INFDEV_611, withNoiseBasedCountModifier("trees_infdev_611", Infdev611NoiseBasedCountPlacementModifier.of(0, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_420 = OldPlacedFeatures.register("trees_infdev_420", OldVegetationConfiguredFeatures.TREES_INFDEV_420, withNoiseBasedCountModifier("trees_infdev_420", Infdev420NoiseBasedCountPlacementModifier.of(0, 0.01f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_415 = OldPlacedFeatures.register("trees_infdev_415", OldVegetationConfiguredFeatures.TREES_INFDEV_415, withNoiseBasedCountModifier("trees_infdev_415", Infdev415NoiseBasedCountPlacementModifier.of(0, 0, 0)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_227 = OldPlacedFeatures.register("trees_infdev_227", OldVegetationConfiguredFeatures.TREES_INFDEV_227, withCountExtraAndTreeModifier(0, 0.1f, 1));
    
    // Classic Trees w/ bees
    public static final RegistryEntry<PlacedFeature> TREES_ALPHA_BEES = OldPlacedFeatures.register("trees_alpha_bees", OldVegetationConfiguredFeatures.TREES_ALPHA_BEES, withNoiseBasedCountModifier("trees_alpha_bees", AlphaNoiseBasedCountPlacementModifier.of(0, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_611_BEES = OldPlacedFeatures.register("trees_infdev_611_bees", OldVegetationConfiguredFeatures.TREES_INFDEV_611_BEES, withNoiseBasedCountModifier("trees_infdev_611_bees", Infdev611NoiseBasedCountPlacementModifier.of(0, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_420_BEES = OldPlacedFeatures.register("trees_infdev_420_bees", OldVegetationConfiguredFeatures.TREES_INFDEV_420_BEES, withNoiseBasedCountModifier("trees_infdev_420_bees", Infdev420NoiseBasedCountPlacementModifier.of(0, 0.01f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_415_BEES = OldPlacedFeatures.register("trees_infdev_415_bees", OldVegetationConfiguredFeatures.TREES_INFDEV_415_BEES, withNoiseBasedCountModifier("trees_infdev_415_bees", Infdev415NoiseBasedCountPlacementModifier.of(0, 0, 0)));
    public static final RegistryEntry<PlacedFeature> TREES_INFDEV_227_BEES = OldPlacedFeatures.register("trees_infdev_227_bees", OldVegetationConfiguredFeatures.TREES_INFDEV_227_BEES, withCountExtraAndTreeModifier(0, 0.1f, 1));
    
    // Beta Trees
    public static final RegistryEntry<PlacedFeature> TREES_BETA_FOREST = OldPlacedFeatures.register("trees_beta_forest", OldVegetationConfiguredFeatures.TREES_BETA_FOREST, withNoiseBasedCountModifier("trees_beta_forest", BetaNoiseBasedCountPlacementModifier.of(5, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_RAINFOREST = OldPlacedFeatures.register("trees_beta_rainforest", OldVegetationConfiguredFeatures.TREES_BETA_RAINFOREST, withNoiseBasedCountModifier("trees_beta_rainforest", BetaNoiseBasedCountPlacementModifier.of(5, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_SEASONAL_FOREST = OldPlacedFeatures.register("trees_beta_seasonal_forest", OldVegetationConfiguredFeatures.TREES_BETA_SEASONAL_FOREST, withNoiseBasedCountModifier("trees_beta_seasonal_forest", BetaNoiseBasedCountPlacementModifier.of(2, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_TAIGA = OldPlacedFeatures.register("trees_beta_taiga", OldVegetationConfiguredFeatures.TREES_BETA_TAIGA, withNoiseBasedCountModifier("trees_beta_taiga", BetaNoiseBasedCountPlacementModifier.of(5, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_SPARSE = OldPlacedFeatures.register("trees_beta_sparse", OldVegetationConfiguredFeatures.TREES_BETA_SPARSE, withCountExtraAndTreeModifier(0, 0.1f, 1));

    // Beta Trees w/ bees
    public static final RegistryEntry<PlacedFeature> TREES_BETA_FOREST_BEES = OldPlacedFeatures.register("trees_beta_forest_bees", OldVegetationConfiguredFeatures.TREES_BETA_FOREST_BEES, withNoiseBasedCountModifier("trees_beta_forest_bees", BetaNoiseBasedCountPlacementModifier.of(5, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_RAINFOREST_BEES = OldPlacedFeatures.register("trees_beta_rainforest_bees", OldVegetationConfiguredFeatures.TREES_BETA_RAINFOREST_BEES, withNoiseBasedCountModifier("trees_beta_rainforest_bees", BetaNoiseBasedCountPlacementModifier.of(5, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_SEASONAL_FOREST_BEES = OldPlacedFeatures.register("trees_beta_seasonal_forest_bees", OldVegetationConfiguredFeatures.TREES_BETA_SEASONAL_FOREST_BEES, withNoiseBasedCountModifier("trees_beta_seasonal_forest_bees", BetaNoiseBasedCountPlacementModifier.of(2, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_BETA_SPARSE_BEES = OldPlacedFeatures.register("trees_beta_sparse_bees", OldVegetationConfiguredFeatures.TREES_BETA_SPARSE_BEES, withCountExtraAndTreeModifier(0, 0.1f, 1));

    // PE Trees
    public static final RegistryEntry<PlacedFeature> TREES_PE_TAIGA = OldPlacedFeatures.register("trees_pe_taiga", OldVegetationConfiguredFeatures.TREES_PE_TAIGA, withNoiseBasedCountModifier("trees_pe_taiga", BetaNoiseBasedCountPlacementModifier.of(1, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_PE_SPARSE = OldPlacedFeatures.register("trees_pe_sparse", OldVegetationConfiguredFeatures.TREES_PE_SPARSE, withCountExtraAndTreeModifier(0, 0.1f, 1));
    
    // PE Trees w/ bees
    public static final RegistryEntry<PlacedFeature> TREES_PE_FOREST_BEES = OldPlacedFeatures.register("trees_pe_forest_bees", OldVegetationConfiguredFeatures.TREES_PE_FOREST_BEES, withNoiseBasedCountModifier("trees_pe_forest_bees", BetaNoiseBasedCountPlacementModifier.of(2, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_PE_RAINFOREST_BEES = OldPlacedFeatures.register("trees_pe_rainforest_bees", OldVegetationConfiguredFeatures.TREES_PE_RAINFOREST_BEES, withNoiseBasedCountModifier("trees_pe_forest_bees", BetaNoiseBasedCountPlacementModifier.of(2, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_PE_SEASONAL_FOREST_BEES = OldPlacedFeatures.register("trees_pe_seasonal_forest_bees", OldVegetationConfiguredFeatures.TREES_PE_SEASONAL_FOREST_BEES, withNoiseBasedCountModifier("trees_pe_forest_bees", BetaNoiseBasedCountPlacementModifier.of(1, 0.1f, 1)));
    public static final RegistryEntry<PlacedFeature> TREES_PE_SPARSE_BEES = OldPlacedFeatures.register("trees_pe_sparse_bees", OldVegetationConfiguredFeatures.TREES_PE_SPARSE_BEES, withCountExtraAndTreeModifier(0, 0.1f, 1));
   
    // Indev Trees
    public static final RegistryEntry<PlacedFeature> TREES_INDEV = OldPlacedFeatures.register("trees_indev", OldVegetationConfiguredFeatures.TREES_INDEV, RarityFilterPlacementModifier.of(3), withCountExtraModifier(5, 0.1f, 1), SquarePlacementModifier.of(), SURFACE_WATER_DEPTH_MODIFIER, PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> TREES_INDEV_WOODS = OldPlacedFeatures.register("trees_indev_woods", OldVegetationConfiguredFeatures.TREES_INDEV_WOODS, withCountExtraModifier(30, 0.1f, 1), SquarePlacementModifier.of(), SURFACE_WATER_DEPTH_MODIFIER, PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of());
    
    // Indev Trees w/ bees
    public static final RegistryEntry<PlacedFeature> TREES_INDEV_BEES = OldPlacedFeatures.register("trees_indev_bees", OldVegetationConfiguredFeatures.TREES_INDEV_BEES, RarityFilterPlacementModifier.of(3), withCountExtraModifier(5, 0.1f, 1), SquarePlacementModifier.of(), SURFACE_WATER_DEPTH_MODIFIER, PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> TREES_INDEV_WOODS_BEES = OldPlacedFeatures.register("trees_indev_woods_bees", OldVegetationConfiguredFeatures.TREES_INDEV_WOODS_BEES, withCountExtraModifier(30, 0.1f, 1), SquarePlacementModifier.of(), SURFACE_WATER_DEPTH_MODIFIER, PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of());    
}
