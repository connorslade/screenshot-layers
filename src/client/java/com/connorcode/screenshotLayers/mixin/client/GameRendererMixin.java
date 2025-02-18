package com.connorcode.screenshotLayers.mixin.client;

import com.connorcode.screenshotLayers.client.ScreenshotLayersClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    void onRenderWorldTail(CallbackInfo ci) {
        var client = MinecraftClient.getInstance();

        if (ScreenshotLayersClient.takingScreenshot)
            ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), (message) -> client.execute(() -> client.inGameHud.getChatHud().addMessage(message)));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V", ordinal = 1))
    void onRenderInGameHud(CallbackInfo ci) {
        var client = MinecraftClient.getInstance();

        if (ScreenshotLayersClient.takingScreenshot)
            ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), (message) -> client.execute(() -> client.inGameHud.getChatHud().addMessage(message)));
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRenderTail(CallbackInfo ci) {
        ScreenshotLayersClient.takingScreenshot = false;
    }
}
