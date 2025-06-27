package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.Layers;
import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.connorcode.screenshotLayers.Misc.asInt;
import static com.connorcode.screenshotLayers.ScreenshotLayers.client;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    void onRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (ScreenshotLayers.builder == null) return;
        ScreenshotLayers.builder.wasHudHidden = client.options.hudHidden;
        client.options.hudHidden = false;
    }

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    void onRenderWorldBeforeHand(CallbackInfo ci) {
        ScreenshotLayers.screenshotLayer("World", 0);
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        if (Layers.needHandLayer()) ScreenshotLayers.screenshotLayer("Player Hand", 1);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V", ordinal = 0))
    void onFirstDrawContextDraw(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        ScreenshotLayers.screenshotLayer("In Game HUD", 2 + asInt(Layers.needHandLayer()) + asInt(Layers.needsOverlayLayer()));
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderInGameHud(CallbackInfo ci) {
        if (Layers.needsScreenLayer())
            ScreenshotLayers.screenshotLayer("Screen", 3 + asInt(Layers.needHandLayer()) + asInt(Layers.needsOverlayLayer()));
    }
}
