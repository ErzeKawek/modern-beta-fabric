package mod.bespectacled.modernbeta.world.biome.provider;

import java.util.List;

import mod.bespectacled.modernbeta.api.world.biome.BiomeProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class SingleBiomeProvider extends BiomeProvider {
    private final RegistryKey<Biome> biomeKey;
    
    public SingleBiomeProvider(NbtCompound settings, RegistryEntryLookup<Biome> biomeRegistry) {
        super(settings, biomeRegistry);
        
        this.biomeKey = RegistryKey.of(RegistryKeys.BIOME, new Identifier(this.settings.singleBiome));
    }

    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeRegistry.getOrThrow(this.biomeKey);
    }
    
    @Override
    public List<RegistryEntry<Biome>> getBiomesForRegistry() {
        return List.of(this.biomeRegistry.getOrThrow(this.biomeKey));
    }
}
