package mod.bespectacled.modernbeta.client.gui.screen.world;

import java.util.function.Consumer;

import mod.bespectacled.modernbeta.client.gui.screen.WorldScreen;
import mod.bespectacled.modernbeta.client.gui.wrapper.BooleanCyclingOptionWrapper;
import mod.bespectacled.modernbeta.util.GUITags;
import mod.bespectacled.modernbeta.util.NbtTags;
import mod.bespectacled.modernbeta.util.NbtUtil;
import mod.bespectacled.modernbeta.util.settings.Settings;
import mod.bespectacled.modernbeta.util.settings.WorldSettings.WorldSetting;
import net.minecraft.nbt.NbtByte;
import net.minecraft.text.TranslatableText;

public class BetaWorldScreen extends OceanWorldScreen {
    protected BetaWorldScreen(WorldScreen parent, WorldSetting worldSetting, Consumer<Settings> consumer, Settings setting) {
        super(parent, worldSetting, consumer, setting);
    }
    
    protected BetaWorldScreen(WorldScreen parent, WorldSetting worldSetting, Consumer<Settings> consumer) {
        super(parent, worldSetting, consumer);
    }

    public static BetaWorldScreen create(WorldScreen worldScreen, WorldSetting worldSetting) {
        return new BetaWorldScreen(
            worldScreen,
            worldSetting,
            settings -> worldScreen.getWorldSettings().replace(worldSetting, settings)
        );
    }
    
    @Override
    protected void init() {
        super.init();
        
        BooleanCyclingOptionWrapper sampleClimate = new BooleanCyclingOptionWrapper(
            GUITags.SAMPLE_CLIMATE_DISPLAY_STRING,
            () -> NbtUtil.toBooleanOrThrow(this.getSetting(NbtTags.SAMPLE_CLIMATE)),
            value -> this.putSetting(NbtTags.SAMPLE_CLIMATE, NbtByte.of(value)),
            this.client.textRenderer.wrapLines(new TranslatableText(GUITags.SAMPLE_CLIMATE_TOOLTIP), 200)
        );
        
        this.addOption(sampleClimate);
    }
}
