package com.connorcode.screenshotLayers;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import net.minecraft.client.texture.NativeImage;

import java.util.Stack;

public class ScreenshotBuilder {
    Stack<ScreenshotLayer> stack;

    public ScreenshotBuilder() {
        this.stack = new Stack<>();
    }

    public void pushLayer(ScreenshotLayer layer) {
        this.stack.push(layer);
    }

    public void saveTiff() {
        var imageStack = new ImageStack();

        for (var layer : this.stack) {
            var width = layer.image.getWidth();
            var height = layer.image.getHeight();
            var data = layer.image.copyPixelsArgb();

            var image = new ColorProcessor(width, height, data);
            imageStack.addSlice(layer.name, image);
        }

        IJ.saveAsTiff(new ImagePlus("title", imageStack), "out.tiff");
    }

    public static class ScreenshotLayer {
        NativeImage image;
        String name;

        public ScreenshotLayer(NativeImage image, String name) {
            this.image = image;
            this.name = name;
        }
    }
}
