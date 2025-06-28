package com.connorcode.screenshotLayers;

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
    // No layering, just take a normal screenshot. This is needed when not in game.
    public boolean basic;

    // Which of the optional layers are needed
    public boolean hand;
    public boolean screen;
    public boolean overlay;

    public Layers() {
        this.basic = client.player == null;

        this.hand = needsHandLayer() && !basic;
        this.screen = needsScreenLayer() && !basic;
        this.overlay = needsOverlayLayer() && !basic;
    }

    public int layerCount() {
        if (this.basic) return 1;
        return 3 + Misc.asInt(hand) + Misc.asInt(screen) + Misc.asInt(overlay);
    }

    private static boolean needsHandLayer() {
        return client.options.getPerspective().isFirstPerson();
    }

    private static boolean needsScreenLayer() {
        var showAutosave = client.options.getShowAutosaveIndicator().getValue() && (client.inGameHud.autosaveIndicatorAlpha > 0.0 || client.inGameHud.lastAutosaveIndicatorAlpha > 0.0);
        return client.currentScreen != null || showAutosave;
    }

    private static boolean needsOverlayLayer() {
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
