package mod.bespectacled.modernbeta.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbeta.ModernBeta;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.report.WorldgenProvider;

public class ModernBetaDataGenerator {
    private static final Path PATH = Paths.get("..\\src\\main\\resources\\data\\generated");
    
    public static void generateData() {
        DataGenerator dataGenerator = new DataGenerator(PATH, List.of());
        dataGenerator.addProvider(new WorldgenProvider(dataGenerator));

        try {
            dataGenerator.run();
        } catch (IOException e) {
            ModernBeta.log(Level.ERROR, "Failed to generate data, " + e);
        }
    }
}
