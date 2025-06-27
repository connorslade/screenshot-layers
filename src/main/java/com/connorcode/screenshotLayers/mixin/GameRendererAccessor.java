package com.connorcode.screenshotLayers.mixin;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor
    GuiRenderer getGuiRenderer();

    @Accessor
    FogRenderer getFogRenderer();
}
