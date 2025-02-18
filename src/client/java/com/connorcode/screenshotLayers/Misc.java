package com.connorcode.screenshotLayers;

import java.nio.file.Path;

public class Misc {
    public static Path withExtension(Path path, String extension) {
        var fileName = path.getFileName().toString();
        var dot = fileName.indexOf('.');

        if (dot == -1) fileName += "." + extension;
        else fileName = fileName.substring(0, dot) + "." + extension;

        return path.getParent().resolve(fileName);
    }
}
