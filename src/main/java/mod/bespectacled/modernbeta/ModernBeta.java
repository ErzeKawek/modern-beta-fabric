package mod.bespectacled.modernbeta;

import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import mod.bespectacled.modernbeta.client.color.BlockColors;
import mod.bespectacled.modernbeta.command.DebugProviderSettingsCommand;
import mod.bespectacled.modernbeta.compat.Compat;
import mod.bespectacled.modernbeta.config.ModernBetaConfig;
import mod.bespectacled.modernbeta.config.ModernBetaConfigBiome;
import mod.bespectacled.modernbeta.config.ModernBetaConfigCaveBiome;
import mod.bespectacled.modernbeta.config.ModernBetaConfigCompat;
import mod.bespectacled.modernbeta.config.ModernBetaConfigGeneration;
import mod.bespectacled.modernbeta.config.ModernBetaConfigRendering;
import mod.bespectacled.modernbeta.world.biome.OldBiomeSource;
import mod.bespectacled.modernbeta.world.biome.OldBiomes;
import mod.bespectacled.modernbeta.world.feature.placement.OldPlacementTypes;
import mod.bespectacled.modernbeta.world.gen.OldChunkGenerator;
import mod.bespectacled.modernbeta.world.gen.OldChunkGeneratorSettings;
import mod.bespectacled.modernbeta.world.gen.OldGeneratorType;
import mod.bespectacled.modernbeta.world.structure.OldStructures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class ModernBeta implements ModInitializer {
    public static final String MOD_ID = "modern_beta";
    public static final String MOD_NAME = "Modern Beta";
    public static final int MOD_VERSION = 4;
    
    public static final boolean CLIENT_ENV = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
    
    private static boolean invalidVersion = false;
    
    public static final ModernBetaConfig CONFIG = AutoConfig.register(
        ModernBetaConfig.class, 
        PartitioningSerializer.wrap(GsonConfigSerializer::new)
    ).getConfig();
    
    public static final ModernBetaConfigGeneration GEN_CONFIG = CONFIG.generationConfig;
    public static final ModernBetaConfigBiome BIOME_CONFIG = CONFIG.biomeConfig;
    public static final ModernBetaConfigCaveBiome CAVE_BIOME_CONFIG = CONFIG.caveBiomeConfig;
    public static final ModernBetaConfigRendering RENDER_CONFIG = CONFIG.renderingConfig;
    public static final ModernBetaConfigCompat COMPAT_CONFIG = CONFIG.compatConfig;

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    public static Identifier createId(String name) {
        return new Identifier(MOD_ID, name);
    }
    
    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] {}", message);
    }
    
    public static void validateVersion(Optional<Integer> version) {
        if (!invalidVersion && (version.isEmpty() || version.get() != MOD_VERSION)) {
            log(Level.ERROR, "Opening a world made with a different version of Modern Beta, settings may not properly load!");
            invalidVersion = true;
        }
    }
    
    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing Modern Beta...");
        
        // Register mod stuff
        OldStructures.register();
        OldBiomes.register();
        OldBiomeSource.register();
        OldChunkGenerator.register();
        OldChunkGeneratorSettings.register();
        OldPlacementTypes.register();
        
        // Set up mod compatibility
        Compat.setupCompat();
        
        // Register default providers
        ModernBetaBuiltInProviders.registerChunkProviders();
        ModernBetaBuiltInProviders.registerChunkSettings();
        ModernBetaBuiltInProviders.registerBiomeProviders();
        ModernBetaBuiltInProviders.registerBiomeSettings();
        ModernBetaBuiltInProviders.registerCaveBiomeProvider();
        ModernBetaBuiltInProviders.registerCaveBiomeSettings();
        ModernBetaBuiltInProviders.registerOreVeinRules();
        
        // Register client-only stuff, i.e. GUI, block colors, etc.
        if (CLIENT_ENV) {
            OldGeneratorType.register();
            
            // Register clientside world providers
            ModernBetaBuiltInProviders.registerWorldProviders();
            
            // Register default screen providers
            ModernBetaBuiltInProviders.registerWorldScreens();
            ModernBetaBuiltInProviders.registerBiomeScreens();
            ModernBetaBuiltInProviders.registerCaveBiomeScreens();
            
            // Override default biome grass/foliage colors
            BlockColors.register();
        }
        
        // Register dev-only stuff, i.e. commands, etc.
        if (DEV_ENV) {
            DebugProviderSettingsCommand.register();
            //ModernBetaDataGenerator.generateData();
        }

        // Man, I am not good at this...
    }
}
