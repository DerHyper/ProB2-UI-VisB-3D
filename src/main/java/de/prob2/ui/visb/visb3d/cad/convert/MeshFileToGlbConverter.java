package de.prob2.ui.visb.visb3d.cad.convert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link CadToGlbConverter} for all mesh based file formats (e.g. STL, OBJ, ...).
 * <br>
 * Mesh files are parsed into {@link TriangleMeshData} using a {@link MeshParser}
 * that matches the file format. 
 * The resulting {@link TriangleMeshData} is then exported into a .glb file using
 * the {@link GlbBuilder}.
 * <br>
 * To add a new mesh file format (e.g. OBJ) a new {@link MeshParser} must be 
 * implemented and registered in the constructor.
 */
public final class MeshFileToGlbConverter implements CadToGlbConverter {

	/**
	 * Maps file extention strings (e.g. "stl", "obj") to 
	 * registered {@link MeshParser}. 
	 */
	private final Map<String, MeshParser> parsersByExtension = new HashMap<>();

	public MeshFileToGlbConverter() {
		parsersByExtension.put("stl", new StlMeshParser());
		// Register other mesh file formats (e.g. "obj") HERE:
		// parsersByExtension.put("obj", new ObjMeshParser());
	}

	@Override
	public boolean supports(String fileExtensionLowerCase) {
		return parsersByExtension.containsKey(fileExtensionLowerCase);
	}

	@Override
	public Path convert(Path inputFile, Path outputDirectory) throws IOException {
		String extension = ConversionUtils.extractExtension(inputFile);
		MeshParser parser = parsersByExtension.get(extension);
		if (parser == null) {
			throw new IllegalArgumentException(
					"Kein MeshParser fuer ." + extension + " registriert");
		}

		TriangleMeshData mesh = parser.parse(inputFile);

		String baseName = ConversionUtils.stripExtension(inputFile.getFileName().toString());
		Path outputFile = outputDirectory.resolve(baseName + ".glb");
		GlbBuilder.writeGlb(mesh, outputFile);
		return outputFile;
	}

	@Override
	public List<String> supportedFileExtensions() {
		return new ArrayList<>(parsersByExtension.keySet());
	}
}