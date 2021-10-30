package com.bespectacled.modernbeta.api.world.biome.climate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class ClimateBiomeRules {
    private final List<ClimateBiomeRule> rules;

    private ClimateBiomeRules(List<ClimateBiomeRule> rules) {
        this.rules = rules;
    }
    
    public Clime apply(Biome biome) {
        for (ClimateBiomeRule rule : this.rules) {
            Clime clime = rule.apply(biome);
            
            if (clime != null)
                return clime;
        }
        
        double temp = MathHelper.clamp(biome.getTemperature(), 0.0, 1.0);
        double rain = MathHelper.clamp(biome.getDownfall(), 0.0, 1.0);
        
        return new Clime(temp, rain);
    }
    
    public static class Builder {
        private final List<ClimateBiomeRule> rules;
        
        public Builder() {
            this.rules = new ArrayList<>();
        }
        
        public Builder add(Predicate<Biome> rule, Supplier<Clime> supplier) {
            this.rules.add(new ClimateBiomeRule(rule, supplier));
            
            return this;
        }
        
        public ClimateBiomeRules build() {
            return new ClimateBiomeRules(this.rules);
        }
    }
}
