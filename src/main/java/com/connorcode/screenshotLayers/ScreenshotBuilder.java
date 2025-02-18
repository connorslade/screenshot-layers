package com.connorcode.screenshotLayers;

import mil.nga.tiff.*;
import mil.nga.tiff.util.TiffConstants;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ScreenshotBuilder {
    public boolean wasHudHidden;
    Stack<ScreenshotLayer> stack;

    public ScreenshotBuilder() {
        this.stack = new Stack<>();
    }

    public void pushLayer(ScreenshotLayer layer) {
        this.stack.push(layer);
    }

    public void saveTiff(File file) throws IOException {
        var directories = new ArrayList<FileDirectory>();

        var stack = this.stack.stream().toList();
        if (!wasHudHidden) stack = this.stack.reversed();

        for (var layer : stack) {
            var image = layer.image;
            var rasters = new Rasters(
                    image.getWidth(),
                    image.getHeight(),
                    4, 8, TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT
            );

            for (var x = 0; x < image.getWidth(); x++) {
                for (var y = 0; y < image.getHeight(); y++) {
                    var color = image.getColorArgb(x, y);
                    rasters.setPixelSample(0, x, y, color >> 16 & 0xFF);
                    rasters.setPixelSample(1, x, y, color >> 8 & 0xFF);
                    rasters.setPixelSample(2, x, y, color & 0xFF);
                    rasters.setPixelSample(3, x, y, color >> 24 & 0xFF);
                }
            }

            var directory = new FileDirectory();
            directory.setImageWidth(image.getWidth());
            directory.setImageHeight(image.getHeight());
            directory.setBitsPerSample(8);
            directory.setCompression(TiffConstants.COMPRESSION_DEFLATE);
            directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_RGB);
            directory.setUnsignedIntegerListEntryValue(
                    FieldTagType.ExtraSamples,
                    List.of(TiffConstants.EXTRA_SAMPLES_ASSOCIATED_ALPHA));
            directory.setSamplesPerPixel(4);
            directory.setRowsPerStrip(
                    rasters.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY));
            directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
            directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT);
            directory.setWriteRasters(rasters);

            for (var entry : List.of(
                    new Pair<>(FieldTagType.Software, "Made with screenshot-layers by Connor Slade"),
                    new Pair<>(FieldTagType.PageName, layer.name)
            )) {
                directory.addEntry(new FileDirectoryEntry(entry.getLeft(), FieldType.ASCII, entry.getRight().length(), List.of(entry.getRight())));
            }

            directories.add(directory);
        }

        var image = new TIFFImage(directories);
        TiffWriter.writeTiff(file, image);
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
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
