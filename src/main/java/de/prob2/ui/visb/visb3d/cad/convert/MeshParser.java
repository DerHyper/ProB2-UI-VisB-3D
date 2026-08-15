package de.prob2.ui.visb.visb3d.cad.convert;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Reads a mesh based CAD file (e.g. STL in {@link StlMeshParser}) and returns a 
 * {@link TriangleMeshData}.
 */
public interface MeshParser {

	abstract TriangleMeshData parse(Path file) throws IOException;
}
