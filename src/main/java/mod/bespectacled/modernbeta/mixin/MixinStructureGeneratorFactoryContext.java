package mod.bespectacled.modernbeta.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.bespectacled.modernbeta.world.gen.OldChunkGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(StructureGeneratorFactory.Context.class)
public class MixinStructureGeneratorFactoryContext {
    @Shadow private ChunkGenerator comp_306;
    @Shadow private ChunkPos comp_309;
    @Shadow private HeightLimitView comp_311;
    @Shadow private Predicate<RegistryEntry<Biome>> comp_312;
    
    @Inject(method = "isBiomeValid", at = @At("HEAD"), cancellable = true)
    private void injectCheckForBiomeOnTop(Heightmap.Type heightmap, CallbackInfoReturnable<Boolean> info) {
        if (this.comp_306 instanceof OldChunkGenerator oldChunkGenerator) {
            int x = this.comp_309.getCenterX();
            int z = this.comp_309.getCenterZ();
            int y = this.comp_306.getHeightInGround(x, z, heightmap, this.comp_311);
            
            RegistryEntry<Biome> biome = oldChunkGenerator.getInjectedBiomeAtBlock(x, y, z);
            
            info.setReturnValue(this.comp_312.test(biome));
        }
    }
}
