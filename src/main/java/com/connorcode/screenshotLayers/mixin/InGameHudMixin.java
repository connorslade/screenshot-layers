package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.Layers;
import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.connorcode.screenshotLayers.Misc.asInt;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderMiscOverlays", at = @At("TAIL"))
    void onRenderMiscOverlaysTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!Layers.needsOverlayLayer()) return;

        context.draw();
        ScreenshotLayers.screenshotLayer("Overlays", 1 + asInt(Layers.needHandLayer()));
    }

    @Inject(method = "renderExperienceLevel", at = @At("TAIL"))
    void onRenderExperienceLevelTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.draw();
        ScreenshotLayers.screenshotLayer("Hotbar", 1 + asInt(Layers.needHandLayer()) + asInt(Layers.needsOverlayLayer()));
    }
}
