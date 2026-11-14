package de.prob2.ui.visb.visb3d.cad.convert;

import java.nio.file.Path;
import java.util.Locale;

public final class ConversionUtils {
    
    /**
     * @param file Path to a file (e.g. "C:\Models\MyModel.stl")
     * @return File extention (e.g. "stl")
     */
    public static String extractExtension(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * @param fileName Name of file (e.g. "MyModel.stl")
     * @return Name of file without extension (e.g. "MyModel")
     */
    public static String stripExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex < 0 ? fileName : fileName.substring(0, dotIndex);
    }
}
