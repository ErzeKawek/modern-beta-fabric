package com.bespectacled.modernbeta.client.gui.screen.world;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.bespectacled.modernbeta.ModernBeta;
import com.bespectacled.modernbeta.api.client.gui.wrapper.DoubleOptionWrapper;
import com.bespectacled.modernbeta.api.registry.BuiltInTypes;
import com.bespectacled.modernbeta.api.world.WorldSettings;
import com.bespectacled.modernbeta.util.NbtTags;
import com.bespectacled.modernbeta.util.NbtUtil;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class PreInfWorldScreen extends InfWorldScreen {
    private static final String LEVEL_WIDTH_DISPLAY_STRING = "createWorld.customize.indev.levelWidth";
    private static final String LEVEL_LENGTH_DISPLAY_STRING = "createWorld.customize.indev.levelLength";
    private static final String LEVEL_HEIGHT_DISPLAY_STRING = "createWorld.customize.indev.levelHeight";
    private static final String CAVE_RADIUS_DISPLAY_STRING = "createWorld.customize.indev.caveRadius";
    
    private static final String LEVEL_HEIGHT_TOOLTIP = "createWorld.customize.indev.levelHeight.tooltip";
    private static final String CAVE_RADIUS_TOOLTIP = "createWorld.customize.indev.caveRadius.tooltip";
    
    public PreInfWorldScreen(
        CreateWorldScreen parent,
        WorldSettings worldSettings,
        Consumer<WorldSettings> consumer
    ) {
        super(parent, worldSettings, consumer);  
    }
    
    @Override
    protected void init() {
        super.init();
        
        Supplier<ChunkGeneratorSettings> chunkGenSettings = () -> this.registryManager
            .<ChunkGeneratorSettings>get(Registry.CHUNK_GENERATOR_SETTINGS_KEY)
            .get(ModernBeta.createId(BuiltInTypes.Chunk.INDEV.name));
            
        int topY = chunkGenSettings.get().getGenerationShapeConfig().getHeight() + chunkGenSettings.get().getGenerationShapeConfig().getMinimumY();
        
        DoubleOptionWrapper<Integer> levelWidth = new DoubleOptionWrapper<>(
            LEVEL_WIDTH_DISPLAY_STRING,
            "blocks",
            128D, 1024D, 128f,
            () -> NbtUtil.toIntOrThrow(this.getChunkSetting(NbtTags.LEVEL_WIDTH)),
            value -> this.putChunkSetting(NbtTags.LEVEL_WIDTH, NbtInt.of(value.intValue()))
        );
        
        DoubleOptionWrapper<Integer> levelLength = new DoubleOptionWrapper<>(
            LEVEL_LENGTH_DISPLAY_STRING,
            "blocks",
            128D, 1024D, 128f,
            () -> NbtUtil.toIntOrThrow(this.getChunkSetting(NbtTags.LEVEL_LENGTH)),
            value -> this.putChunkSetting(NbtTags.LEVEL_LENGTH, NbtInt.of(value.intValue()))
        );
        
        DoubleOptionWrapper<Integer> levelHeight = new DoubleOptionWrapper<>(
            LEVEL_HEIGHT_DISPLAY_STRING, 
            "blocks",
            64D, (double)topY, 64F,
            () -> NbtUtil.toIntOrThrow(this.getChunkSetting(NbtTags.LEVEL_HEIGHT)),
            value -> this.putChunkSetting(NbtTags.LEVEL_HEIGHT, NbtInt.of(value.intValue())),
            this.client.textRenderer.wrapLines(new TranslatableText(LEVEL_HEIGHT_TOOLTIP), 200)
        );
        
        DoubleOptionWrapper<Float> caveRadius = new DoubleOptionWrapper<>(
            CAVE_RADIUS_DISPLAY_STRING,
            "",
            1D, 3D, 0.1f,
            () -> NbtUtil.toFloatOrThrow(this.getChunkSetting(NbtTags.LEVEL_CAVE_RADIUS)),
            value -> this.putChunkSetting(NbtTags.LEVEL_CAVE_RADIUS, NbtFloat.of(value.floatValue())),
            this.client.textRenderer.wrapLines(new TranslatableText(CAVE_RADIUS_TOOLTIP), 200)
        );
        
        this.addOption(levelWidth);
        this.addOption(levelLength);
        this.addOption(levelHeight);
        this.addOption(caveRadius);
    }
}
