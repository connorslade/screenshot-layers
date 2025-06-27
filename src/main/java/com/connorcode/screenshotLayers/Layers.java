package com.connorcode.screenshotLayers;

import com.connorcode.screenshotLayers.mixin.GameRendererAccessor;
import com.connorcode.screenshotLayers.mixin.InGameHudAccessor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;

import static com.connorcode.screenshotLayers.ScreenshotLayers.client;

// Layer ordering:
// 1. World
// 2. Player Hand (Optional)
// 3. Overlays (Optional)
// 4. Hotbar
// 5. In Game GUI
// 6. Screen (Optional)
public class Layers {
    public static int layerCount() {
        return 3 + Misc.asInt(needHandLayer()) + Misc.asInt(needsScreenLayer()) + Misc.asInt(needsOverlayLayer());
    }

    public static boolean needHandLayer() {
        return ((GameRendererAccessor) client.gameRenderer).getRenderHand();
    }

    public static boolean needsScreenLayer() {
        var inGameHud = (InGameHudAccessor) client.inGameHud;
        var showAutosave = client.options.getShowAutosaveIndicator().getValue() && (inGameHud.getAutosaveIndicatorAlpha() > 0.0 || inGameHud.getLastAutosaveIndicatorAlpha() > 0.0);
        return client.currentScreen != null || showAutosave;
    }

    // todo: dont recompute these during a screenshot operations (TOC-TOU also just performance)
    public static boolean needsOverlayLayer() {
        if (client.player == null) return false;
        if (client.options.getPerspective().isFirstPerson()) {
            if (client.player.isUsingSpyglass()) return true;
            for (var slot : EquipmentSlot.values()) {
                var equippableComponent = client.player.getEquippedStack(slot).get(DataComponentTypes.EQUIPPABLE);
                if (equippableComponent != null && equippableComponent.slot() == slot && equippableComponent.cameraOverlay().isPresent())
                    return true;
            }

            return client.player.getFrozenTicks() > 0 || client.player.nauseaIntensity > 0.0;
        }

        return false;
    }
}
