package mod.bespectacled.modernbeta.world.cavebiome.provider;

import java.util.List;

import mod.bespectacled.modernbeta.api.world.cavebiome.CaveBiomeProvider;
import mod.bespectacled.modernbeta.util.settings.Settings;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class NoCaveBiomeProvider extends CaveBiomeProvider {
    public NoCaveBiomeProvider(long seed, Settings settings, Registry<Biome> biomeRegistry) {
        super(seed, settings, biomeRegistry);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int biomeX, int biomeY, int biomeZ) {
        return null;
    }

    @Override
    public List<RegistryEntry<Biome>> getBiomesForRegistry() {
        return List.of();
    }
}
