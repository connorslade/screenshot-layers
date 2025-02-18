package com.connorcode.screenshotLayers.mixin.client;

import com.connorcode.screenshotLayers.client.ScreenshotLayersClient;
import net.minecraft.client.render.GameRenderer;
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

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    void onRenderWorldBeforeHand(CallbackInfo ci) {
        if (this.renderHand) ScreenshotLayersClient.screenshotLayer();
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        ScreenshotLayersClient.screenshotLayer();
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderInGameHud(CallbackInfo ci) {
        ScreenshotLayersClient.screenshotLayer();

        var builder = ScreenshotLayersClient.builder;
        if (builder != null && !builder.isEmpty()) {
            builder.saveTiff(screenshotFilename());
            ScreenshotLayersClient.builder = null;
        }
    }
}
