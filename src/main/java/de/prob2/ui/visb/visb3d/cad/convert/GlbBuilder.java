package de.prob2.ui.visb.visb3d.cad.convert;

import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfModelWriter;
import de.javagl.jgltf.obj.model.ObjGltfModelCreator;
import de.javagl.obj.FloatTuples;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjFaces;
import de.javagl.obj.ObjWriter;
import de.javagl.obj.Objs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts {@link TriangleMeshData} into a .glb file.
 * 
 * To do that the {@link TriangleMeshData} is converted into a temporary .obj file, 
 * which is then converted into a .glb file via the jgltf library. 
 * 
 * This has to be done this way because direct glb conversion is not jet supported in jgltf.
 */
final class GlbBuilder {

	static void writeGlb(TriangleMeshData mesh, Path outputGlbFile) throws IOException {
		Path tempObjFile = Files.createTempFile("cadconvert", ".obj");
		try {
			writeAsObj(mesh, tempObjFile);
			GltfModel gltfModel = createGltfModel(tempObjFile.toUri());
			new GltfModelWriter().writeBinary(gltfModel, outputGlbFile.toFile());
		} finally {
			Files.deleteIfExists(tempObjFile);
		}
	}

	/**
	 * Creates a {@link GltfModel} from a .obj file.
	 */
	static GltfModel createGltfModel(URI objFileUri) throws IOException {
		ObjGltfModelCreator creator = new ObjGltfModelCreator();
		// GL_UNSIGNED_INT because very big models may occur due use of 
		// tesselation on geometry based CAD formats
		creator.setIndicesComponentType(GltfConstants.GL_UNSIGNED_INT);
		return creator.create(objFileUri);
	}

	private static void writeAsObj(TriangleMeshData mesh, Path outputObjFile)
			throws IOException {
		Obj obj = Objs.create();

		float[] positions = mesh.getPositions();
		for (int i = 0; i < positions.length; i += 3) {
			obj.addVertex(FloatTuples.create(
				positions[i], positions[i + 1], positions[i + 2]));
		}

		float[] normals = mesh.getNormals();
		for (int i = 0; i < normals.length; i += 3) {
			obj.addNormal(FloatTuples.create(
				normals[i], normals[i + 1], normals[i + 2]));
		}

		int[] indices = mesh.getIndices();
		for (int i = 0; i < indices.length; i += 3) {
			int[] vertexIndices = {indices[i], indices[i + 1], indices[i + 2]};
			// Normalen index = vertex index, since StlParser uses 3 vertices 
			// and 3 normals per polygon (no vertex sharing)
			int[] normalIndices = vertexIndices;
			obj.addFace(ObjFaces.create(vertexIndices, null, normalIndices));
		}

		try (OutputStream out = Files.newOutputStream(outputObjFile)) {
			ObjWriter.write(obj, out);
		}
	}
}
