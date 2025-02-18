package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.connorcode.screenshotLayers.Misc.screenshotFilename;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    private boolean renderHand;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    void onRenderWorldBeforeHand(CallbackInfo ci) {
        if (this.renderHand) ScreenshotLayers.screenshotLayer();
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        ScreenshotLayers.screenshotLayer();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(I)V", ordinal = 1))
    void onFirstDrawContextDraw(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        var inGameHud = (InGameHudAccessor) client.inGameHud;
        var showAutosave = client.options.getShowAutosaveIndicator().getValue() && (inGameHud.getAutosaveIndicatorAlpha() > 0.0 || inGameHud.getLastAutosaveIndicatorAlpha() > 0.0);
        if (client.getOverlay() != null || client.currentScreen != null || showAutosave)
            ScreenshotLayers.screenshotLayer();
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderInGameHud(CallbackInfo ci) {
        ScreenshotLayers.screenshotLayer();

        var builder = ScreenshotLayers.builder;
        if (builder != null && !builder.isEmpty()) {
            builder.saveTiff(screenshotFilename());
            ScreenshotLayers.builder = null;
        }
    }
}
