package com.connorcode.screenshotLayers;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import net.minecraft.client.texture.NativeImage;

import java.util.Stack;

public class ScreenshotBuilder {
    Stack<NativeImage> stack;

    public ScreenshotBuilder() {
        this.stack = new Stack<>();
    }

    public void pushLayer(NativeImage layer) {
        this.stack.push(layer);
    }

    public void saveTiff(String path) {
        var imageStack = new ImageStack();

        for (var layer : this.stack) {
            var width = layer.getWidth();
            var height = layer.getHeight();
            var data = layer.copyPixelsArgb();

            var image = new ColorProcessor(width, height, data);
            imageStack.addSlice("", image);
        }

        IJ.saveAsTiff(new ImagePlus("", imageStack), path);
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }
}
