package com.bespectacled.modernbeta.world.cavebiome.provider;

import java.util.List;

import com.bespectacled.modernbeta.api.world.cavebiome.CaveBiomeProvider;
import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class NoCaveBiomeProvider extends CaveBiomeProvider {
    public NoCaveBiomeProvider(long seed, NbtCompound settings) {
        super(seed, settings);
    }

    @Override
    public Biome getBiomeForNoiseGen(Registry<Biome> biomeRegistry, int biomeX, int biomeY, int biomeZ) {
        return null;
    }

    @Override
    public List<RegistryKey<Biome>> getBiomesForRegistry() {
        return new ImmutableList.Builder<RegistryKey<Biome>>().build();
    }

}
