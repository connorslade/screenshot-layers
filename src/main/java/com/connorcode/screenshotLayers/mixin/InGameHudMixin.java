package com.connorcode.screenshotLayers.mixin;

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
import static com.connorcode.screenshotLayers.Misc.renderGui;
import static com.connorcode.screenshotLayers.ScreenshotLayers.builder;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderMiscOverlays", at = @At("TAIL"))
    void onRenderMiscOverlaysTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (builder == null || !builder.layers.overlay) return;

        renderGui();
        ScreenshotLayers.screenshotLayer("Overlays", 1 + asInt(builder.layers.hand));
    }

    @Inject(method = "renderMainHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/bar/Bar;drawExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V", shift = At.Shift.AFTER))
    void onRenderExperienceLevelTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(builder == null) return;

        renderGui();
        ScreenshotLayers.screenshotLayer("Hotbar", 1 + asInt(builder.layers.hand) + asInt(builder.layers.overlay));
    }
}
