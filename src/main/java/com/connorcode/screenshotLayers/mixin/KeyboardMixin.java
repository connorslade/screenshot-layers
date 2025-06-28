package com.connorcode.screenshotLayers.mixin;

import com.connorcode.screenshotLayers.ScreenshotBuilder;
import com.connorcode.screenshotLayers.ScreenshotLayers;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.connorcode.screenshotLayers.ScreenshotLayers.captureKeybinding;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z", ordinal = 1))
    void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (captureKeybinding.matchesKey(key, scancode)) {
            ScreenshotLayers.builder = new ScreenshotBuilder();
            if (ScreenshotLayers.builder.layers.basic) ScreenshotLayers.screenshotLayer("Main", 0);
        }
    }
}
