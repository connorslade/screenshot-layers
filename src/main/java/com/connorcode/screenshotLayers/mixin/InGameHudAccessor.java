package com.connorcode.screenshotLayers.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Accessor
    float getAutosaveIndicatorAlpha();

    @Accessor
    float getLastAutosaveIndicatorAlpha();
}
