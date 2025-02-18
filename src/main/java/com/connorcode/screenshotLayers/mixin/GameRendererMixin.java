package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.ScreenshotLayers;
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
        if (this.renderHand) ScreenshotLayers.screenshotLayer();
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
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
