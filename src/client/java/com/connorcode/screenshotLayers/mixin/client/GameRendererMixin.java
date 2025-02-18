package com.connorcode.screenshotLayers.mixin.client;

import com.connorcode.screenshotLayers.ScreenshotBuilder;
import com.connorcode.screenshotLayers.client.ScreenshotLayersClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.util.ScreenshotRecorder.takeScreenshot;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        if (ScreenshotLayersClient.builder == null) return;

        var client = MinecraftClient.getInstance();
        var layer = new ScreenshotBuilder.ScreenshotLayer(takeScreenshot(client.getFramebuffer()), "Name");
        ScreenshotLayersClient.builder.pushLayer(layer);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V", ordinal = 1))
    void onRenderInGameHud(CallbackInfo ci) {
        if (ScreenshotLayersClient.builder == null) return;

        var client = MinecraftClient.getInstance();
        var layer = new ScreenshotBuilder.ScreenshotLayer(takeScreenshot(client.getFramebuffer()), "Name");
        ScreenshotLayersClient.builder.pushLayer(layer);
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderTail(CallbackInfo ci) {
        if (ScreenshotLayersClient.builder != null)
        {
            ScreenshotLayersClient.builder.saveTiff();
            ScreenshotLayersClient.builder = null;
        }
    }
}
