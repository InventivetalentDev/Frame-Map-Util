package org.inventivetalent.frameutil;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.inventivetalent.boundingbox.BoundingBox;
import org.inventivetalent.vectors.Vectors;
import org.inventivetalent.vectors.d2.Vector2DDouble;
import org.inventivetalent.vectors.d3.Vector3DDouble;

public enum MapFacing {

	// @formatter:off
	//		faceX	faceZ	frameX	frameZ									inverted
	NORTH(	+0, 	-1, 	-1, 	+0, 	BlockFace.NORTH, 	Plane.X,	true	),
	EAST(	+1, 	+0, 	+0, 	-1, 	BlockFace.EAST, 	Plane.Z,	true	),
	SOUTH(	+0, 	+1, 	+1,		+0, 	BlockFace.SOUTH, 	Plane.X,	false	),
	WEST(	-1, 	+0, 	+0, 	+1, 	BlockFace.WEST, 	Plane.Z,	false	);
	// @formatter:on

	// Mod in the direction the frame is facing
	private int xFaceMod;
	private int zFaceMod;

	// Mod on the frame pane
	private int xFrameMod;
	private int zFrameMod;

	// Facing of the frame
	private BlockFace frameDirection;

	private Plane plane;

	private boolean frameModInverted;

	MapFacing(int xFaceMod, int zFaceMod, int xFrameMod, int zFrameMod, BlockFace frameDirection, Plane plane, boolean frameModInverted) {
		this.xFaceMod = xFaceMod;
		this.zFaceMod = zFaceMod;

		this.xFrameMod = xFrameMod;
		this.zFrameMod = zFrameMod;

		this.frameDirection = frameDirection;
		this.plane = plane;
		this.frameModInverted = frameModInverted;
	}

	/**
	 * @return The modification factor for the X-direction the frame is facing
	 */
	public int getFaceModX() {
		return xFaceMod;
	}

	/**
	 * @return The modification factor for the Z-direction the frame is facing
	 */
	public int getFaceModZ() {
		return zFaceMod;
	}

	/**
	 * @return The X modification factor on the pane the frame is placed
	 */
	public int getFrameModX() {
		return xFrameMod;
	}

	/**
	 * @return The Z modification factor on the pane the frame is placed
	 */
	public int getFrameModZ() {
		return zFrameMod;
	}

	/**
	 * @return the {@link BlockFace} in which the frame is facing
	 */
	public BlockFace getFrameDirection() {
		return frameDirection;
	}

	/**
	 * @return the {@link Plane} the frame is placed on
	 */
	public Plane getPlane() {
		return plane;
	}

	//	public static MapFacing getForBoundingBox(BoundingBox boundingBox) {
	//		Vector a = new Vector(boundingBox.minX, 0, boundingBox.minZ);
	//		Vector b = new Vector(boundingBox.maxX, 0, boundingBox.maxZ);
	//
	//	}

	public boolean isFrameModInverted() {
		return frameModInverted;
	}

	/**
	 * Gets the facing for an ItemFrame
	 *
	 * @param itemFrame ItemFrame to check
	 * @return the frame's {@link MapFacing}
	 */
	public static MapFacing getForItemFrame(ItemFrame itemFrame) {
		switch (itemFrame.getFacing()) {
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
			default:
				throw new RuntimeException("Invalid frame facing: " + itemFrame.getFacing());
		}
	}

	/**
	 * Creates a BoundingBox for this facing and expands it to fit the size of ItemFrame entities
	 *
	 * @param a first box corner
	 * @param b second box corner
	 * @return a new {@link BoundingBox}
	 */
	public BoundingBox createBoundingBox(Vector3DDouble a, Vector3DDouble b) {
		Vector3DDouble min = Vectors.min(a, b);
		Vector3DDouble max = Vectors.max(a, b);

		if (min.getX() < 0) {
			min = min.subtract(1, 0, 0);
		}
		if (min.getZ() < 0) {
			min = min.subtract(0, 0, 1);
		}

		if (max.getX() < 0) {
			max = max.subtract(1, 0, 0);
		}
		if (max.getZ() < 0) {
			max = max.subtract(0, 0, 1);
		}

		Plane plane = getPlane();
		if (plane == Plane.X) {
			if (getFaceModZ() > 0) {
				max = max.add(1, 1, 0.0625);
			}
			if (getFaceModZ() < 0) {
				min = min.add(0, 0, 0.9375);
				max = max.add(1, 1, 0.9375);
			}
		}
		if (plane == Plane.Z) {
			if (getFaceModX() > 0) {
				max = max.add(0.0625, 1, 1);
			}
			if (getFaceModX() < 0) {
				min = min.add(0.9375, 0, 0);
				max = max.add(0.9375, 1, 1);
			}
		}

		return new BoundingBox(min, max);
	}

	public enum Plane {
		/**
		 * X-Plane, maps facing south or north
		 */
		X,
		/**
		 * Z-Plane, maps facing east or west
		 */
		Z;

		/**
		 * Converts a 3D vector to a 2D plane vector
		 *
		 * @param vector3d vector to convert
		 * @return the converted vector
		 */
		public Vector2DDouble to2D(Vector3DDouble vector3d) {
			if (this == X) {
				return new Vector2DDouble(vector3d.getX(), vector3d.getY());
			}
			if (this == Z) {
				return new Vector2DDouble(vector3d.getZ(), vector3d.getY());
			}

			// No idea how it would ever get this far...
			return Vector2DDouble.ZERO;
		}

		/**
		 * Converts a plane 2D vector to 3D
		 *
		 * @param vector2d vector to convert
		 * @return converted vector
		 * @see #to3D(Vector2DDouble, double)
		 * @see #to3D(Vector2DDouble, double, double)
		 */
		public Vector3DDouble to3D(Vector2DDouble vector2d) {
			return to3D(vector2d, 0);
		}

		/**
		 * Converts a plane 2D vector to 3D and replaces the unassigned axis (X or Z) with the specified number
		 *
		 * @param vector2d vector to convert
		 * @param xOrZ     value for the unassigned axis
		 * @return the converted vector
		 */
		public Vector3DDouble to3D(Vector2DDouble vector2d, double xOrZ) {
			return to3D(vector2d, xOrZ, xOrZ);
		}

		/**
		 * Converts a plane 2D vector to 3D and replaces the axis values if they are unassigned
		 *
		 * @param vector2d vector to convert
		 * @param altX     value for X
		 * @param altZ     value for Z
		 * @return the converted vector
		 */
		public Vector3DDouble to3D(Vector2DDouble vector2d, double altX, double altZ) {
			if (this == X) {
				return new Vector3DDouble(vector2d.getX(), vector2d.getY(), altZ);
			}
			if (this == Z) {
				return new Vector3DDouble(altX, vector2d.getY(), vector2d.getX());
			}

			return Vector3DDouble.ZERO;
		}

	}

}
