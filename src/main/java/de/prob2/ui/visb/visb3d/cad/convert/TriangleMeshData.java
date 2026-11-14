package de.prob2.ui.visb.visb3d.cad.convert;

/**
 * Data format independent Representation of polygon meshes.
 * Every CAD data format (STL, OBJ, ...) only has to be converted into this format,
 * using a matching {@link CadParser} subclass.
 * The real glTF/GLB export happens in the ({@link GlbBuilder}).
 */
public final class TriangleMeshData {

	/** Interleaved xyz-positions, Length = 3 * #vertices. */
	private final float[] positions;

	/** Interleaved xyz-normals, Length = {@link #positions}. */
	private final float[] normals;

	/** Polygon-indices in vertices, Length = 3 * #polygons. */
	private final int[] indices;

	public TriangleMeshData(float[] positions, float[] normals, int[] indices) {
		if (positions.length != normals.length) {
			throw new IllegalArgumentException(
				"positions and normals must have same length");
		}
		if (positions.length % 3 != 0) {
			throw new IllegalArgumentException(
				"positions.length has to be dividable by 3 (one per polygon vertex)");
		}
		if (indices.length % 3 != 0) {
			throw new IllegalArgumentException(
				"indices.length has to be dividable by 3");
		}
		this.positions = positions;
		this.normals = normals;
		this.indices = indices;
	}

	public float[] getPositions() {
		return positions;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVertexCount() {
		return positions.length / 3;
	}

	public int getTriangleCount() {
		return indices.length / 3;
	}
}
