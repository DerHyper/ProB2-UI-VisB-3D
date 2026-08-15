package de.prob2.ui.visb.visb3d.cad.convert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Reads a STL file and returns a {@link TriangleMeshData}.
 */
final class StlMeshParser implements MeshParser {

	/**
	 * Reads a STL file and returns a {@link TriangleMeshData}. 
	 * Currently no vertex welding, every vertex of a polygon is saved.
	 * This needs more memory.
	 */
	@Override
	public TriangleMeshData parse(Path file) throws IOException {
		byte[] bytes = Files.readAllBytes(file);
		if (isBinaryStl(bytes)) {
			return parseBinary(bytes);
		}
		return parseAscii(bytes);
	}

	/**
	 * Checks if data matches binary STL format: <br>
	 * - 80 byte header <br>
	 * - 4 byte polygon counter <br>
	 * - n * 50 byte polygon data (normal, v1, v2, v3).
	 */
	private static boolean isBinaryStl(byte[] bytes) {
		if (bytes.length < 84) {
			return false;
		}
		long triangleCount = readUInt32LE(bytes, 80);
		long expectedBinarySize = 80L + 4L + triangleCount * 50L;
		if (expectedBinarySize == bytes.length) {
			return true;
		}

		// Fallback: Check if data does not start with "solid"
		// This is not used before, because some binary STL headers might 
		// start with "solid"
		String head = new String(bytes, 0, Math.min(5, bytes.length),
			StandardCharsets.US_ASCII).trim().toLowerCase(Locale.ROOT);
		return !head.equals("solid");
	}

	private static long readUInt32LE(byte[] bytes, int offset) {
		return ((bytes[offset] & 0xFFL))
			| ((bytes[offset + 1] & 0xFFL) << 8)
			| ((bytes[offset + 2] & 0xFFL) << 16)
			| ((bytes[offset + 3] & 0xFFL) << 24);
	}

	private static TriangleMeshData parseBinary(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(80);
		int triangleCount = buffer.getInt();

		float[] positions = new float[triangleCount * 3 * 3];
		float[] normals = new float[triangleCount * 3 * 3];
		int[] indices = new int[triangleCount * 3];

		int vertexCursor = 0;
		for (int t = 0; t < triangleCount; t++) {
			float nx = buffer.getFloat();
			float ny = buffer.getFloat();
			float nz = buffer.getFloat();

			for (int corner = 0; corner < 3; corner++) {
				float x = buffer.getFloat();
				float y = buffer.getFloat();
				float z = buffer.getFloat();

				int base = vertexCursor * 3;
				positions[base] = x;
				positions[base + 1] = y;
				positions[base + 2] = z;
				normals[base] = nx;
				normals[base + 1] = ny;
				normals[base + 2] = nz;
				indices[vertexCursor] = vertexCursor;
				vertexCursor++;
			}
			buffer.getShort(); // attribute byte count is ignored
		}
		return new TriangleMeshData(positions, normals, indices);
	}

	private static TriangleMeshData parseAscii(byte[] bytes) {
		String content = new String(bytes, StandardCharsets.US_ASCII);

		List<Float> positionList = new ArrayList<>();
		List<Float> normalList = new ArrayList<>();
		float currentNx = 0f;
		float currentNy = 0f;
		float currentNz = 0f;

		for (String rawLine : content.split("\\r?\\n")) {
			String line = rawLine.trim();
			if (line.startsWith("facet normal")) {
				StringTokenizer tok = new StringTokenizer(line);
				tok.nextToken(); // "facet"
				tok.nextToken(); // "normal"
				currentNx = Float.parseFloat(tok.nextToken());
				currentNy = Float.parseFloat(tok.nextToken());
				currentNz = Float.parseFloat(tok.nextToken());
			} else if (line.startsWith("vertex")) {
				StringTokenizer tok = new StringTokenizer(line);
				tok.nextToken(); // "vertex"
				positionList.add(Float.valueOf(tok.nextToken()));
				positionList.add(Float.valueOf(tok.nextToken()));
				positionList.add(Float.valueOf(tok.nextToken()));
				normalList.add(currentNx);
				normalList.add(currentNy);
				normalList.add(currentNz);
			}
		}

		float[] positions = toFloatArray(positionList);
		float[] normals = toFloatArray(normalList);
		int vertexCount = positions.length / 3;
		int[] indices = new int[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			indices[i] = i;
		}
		return new TriangleMeshData(positions, normals, indices);
	}

	private static float[] toFloatArray(List<Float> list) {
		float[] array = new float[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
}
