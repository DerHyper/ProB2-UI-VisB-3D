package de.prob2.ui.visb.visb3d.cad.convert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Central entry point for CAD file conversion. 
 * <br>
 * New converters can be added by adding a {@link CadToGlbConverter} implementation
 * into the constructor. To add new mesh based file formats see {@link MeshFileToGlbConverter}.
 */
public final class CadConverterRegistry {
 
	/**
	 * Collection of all the CAD file formats that can currently be converted to .glb
	 */
	private final List<CadToGlbConverter> converters = new ArrayList<>();
 
	public CadConverterRegistry() {
		register(new MeshFileToGlbConverter());
		// Register other CAD file formats (e.g. "Geometry based") HERE:
		// register(new GeometryFileToGlbConverter());
	}
 
	public void register(CadToGlbConverter converter) {
		converters.add(converter);
	}
 
	/**
	 * Converts the {@code inputFile} to a .glb file.
	 * @return Path to the exported .glb file.
	 * @throws UnsupportedOperationException When no converter is registered for the file extension
	 */
	public Path convert(Path inputFile, Path outputDirectory) throws IOException {
		String extension = extractExtension(inputFile);
 
		for (CadToGlbConverter converter : converters) {
			if (converter.supports(extension)) {
				return converter.convert(inputFile, outputDirectory);
			}
		}
		throw new UnsupportedOperationException(
			"No converter currently exists for ." + extension + " files. (File: " + inputFile + ")");
	}
 
	private static String extractExtension(Path file) {
		String fileName = file.getFileName().toString();
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
			throw new IllegalArgumentException("File without extension: " + file);
		}
		return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
	}
}