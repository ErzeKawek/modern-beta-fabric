package mod.bespectacled.modernbeta.world.blocksource;

import mod.bespectacled.modernbeta.api.world.blocksource.BlockSource;
import mod.bespectacled.modernbeta.settings.ModernBetaSettingsChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class BlockSourceDeepslate implements BlockSource {
    private final int minY;
    private final int maxY;
    private final boolean useDeepslate;
    private final BlockState deepslateBlock;
    private final RandomSplitter randomSplitter;
    
    public BlockSourceDeepslate(ModernBetaSettingsChunk chunkSettings, RandomSplitter randomSplitter) {
        this.minY = chunkSettings.deepslateMinY;
        this.maxY = chunkSettings.deepslateMaxY;
        this.useDeepslate = chunkSettings.useDeepslate;
        this.deepslateBlock = Registries.BLOCK.getOrThrow(keyOf(chunkSettings.deepslateBlock)).getDefaultState();
        this.randomSplitter = randomSplitter;
    }
    
    @Override
    public BlockState apply(int x, int y, int z) {
        if (!this.useDeepslate || y >= maxY)
            return null;
        
        if (y <= minY)
            return this.deepslateBlock;
        
        double yThreshold = MathHelper.lerp(MathHelper.getLerpProgress(y, minY, maxY), 1.0, 0.0);
        Random random = this.randomSplitter.split(x, y, z);
        
        return (double)random.nextFloat() < yThreshold ? this.deepslateBlock : null;
    }

    private static RegistryKey<Block> keyOf(String block) {
        return RegistryKey.of(RegistryKeys.BLOCK, new Identifier(block));
    }
}
