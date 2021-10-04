package com.bespectacled.modernbeta.api.world;

import java.util.function.Consumer;

import com.bespectacled.modernbeta.api.client.gui.screen.WorldScreen;
import com.bespectacled.modernbeta.api.registry.Registries;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;

public final class WorldProvider {
    private final String chunkProvider;
    private final String chunkGenSettings;
    private final String worldScreen;

    private final String biomeProvider;
    private final String caveBiomeProvider;
    private final String singleBiome;
    
    private final boolean generateOceanShrines;
    private final boolean generateMonuments;

    public WorldProvider(
        String chunkProvider,
        String chunkGenSettings,
        String biomeProvider,
        String caveBiomeProvider,
        String singleBiome,
        String worldScreen,
        boolean generateOceanShrines,
        boolean generateMonuments
    ) {
        this.chunkProvider = chunkProvider;
        this.chunkGenSettings = chunkGenSettings;
        this.worldScreen = worldScreen;
        
        this.biomeProvider = biomeProvider;
        this.caveBiomeProvider = caveBiomeProvider;
        this.singleBiome = singleBiome;
        
        this.generateOceanShrines = generateOceanShrines;
        this.generateMonuments = generateMonuments;
    }
    
    public String getChunkProvider() {
        return this.chunkProvider;
    }
    
    public String getChunkGenSettings() {
        return this.chunkGenSettings;
    }
    
    public String getBiomeProvider() {
        return this.biomeProvider;
    }
    
    public String getCaveBiomeProvider() {
        return this.caveBiomeProvider;
    }
    
    public String getSingleBiome() {
        return this.singleBiome;
    }
    
    public String getWorldScreen() {
        return this.worldScreen;
    }
    
    public boolean generateOceanShrines() {
        return this.generateOceanShrines;
    }
    
    public boolean generateMonuments() {
        return this.generateMonuments;
    }
    
    public WorldScreen createWorldScreen(
        CreateWorldScreen parent,
        WorldSettings worldSettings,
        Consumer<WorldSettings> consumer
    ) {
        return Registries.WORLD_SCREEN
            .getOrDefault(this.worldScreen)
            .apply(parent, worldSettings, consumer);
    }
    
    @Override
    public String toString() {
        return this.chunkProvider;
    }
    
    public String asString() {
        return String.format(
            "[World Provider]\n" +
                "* Chunk Provider: %s\n" +
                "* Chunk Generator Settings: %s\n" +
                "* Biome Provider: %s\n" +
                "* Cave Biome Provider: %s\n" +
                "* Single Biome: %s\n" +
                "* World Screen: %s",
            this.chunkProvider, 
            this.chunkGenSettings, 
            this.biomeProvider, 
            this.caveBiomeProvider, 
            this.singleBiome, 
            this.worldScreen
        );
    }
}
