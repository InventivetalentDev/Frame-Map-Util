package org.inventivetalent.frameutil;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.inventivetalent.boundingbox.BoundingBox;
import org.inventivetalent.boundingbox.BoundingBoxAPI;
import org.inventivetalent.vectors.d2.Vector2DDouble;
import org.inventivetalent.vectors.d3.Vector3DDouble;
import org.inventivetalent.vectors.d3.Vector3DInt;

import java.util.Objects;

/**
 * Base frame class
 * <p>
 * Calculates position, absolute frame bounding box, bounding box including the blocks the frames are placed on and the {@link MapFacing}
 */
public abstract class BaseFrameMapAbstract {

    @Expose
    protected String worldName;
    @Expose
    protected Vector3DDouble blockBaseVector;
    @Expose
    protected Vector3DDouble baseVector;
    @Expose
    protected BoundingBox boundingBox;
    @Expose
    protected BoundingBox blockBoundingBox;
    @Expose
    protected MapFacing facing;

    @Expose
    protected Vector2DDouble minCorner2d;
    @Expose
    protected Vector2DDouble maxCorner2d;

    protected BaseFrameMapAbstract() {
    }

    public BaseFrameMapAbstract(ItemFrame baseFrame, Vector3DDouble firstCorner, Vector3DDouble secondCorner) {
        Vector3DDouble diffCheck = firstCorner.subtract(secondCorner);
        //		if (Math.abs(diffCheck.getX()) > 1 && Math.abs(diffCheck.getZ()) > 1 || Math.abs(diffCheck.getX()) > 1 && Math.abs(diffCheck.getY()) > 1 || Math.abs(diffCheck.getZ()) > 1 && Math.abs(diffCheck.getY()) > 1) {
        //			// The frames are 3-dimensional, not on a single plane
        //			throw new IllegalArgumentException("Invalid frame position dimensions");
        //		}

        this.worldName = baseFrame.getWorld().getName();
        this.facing = MapFacing.getForItemFrame(baseFrame);
        this.blockBaseVector = new Vector3DDouble(baseFrame.getLocation().getBlock().getRelative(this.facing.getFrameDirection().getOppositeFace()).getLocation());
        this.baseVector = new Vector3DDouble(baseFrame.getLocation().getBlockX(), baseFrame.getLocation().getBlockY(), baseFrame.getLocation().getBlockZ());

        Vector3DDouble firstVector = new Vector3DDouble(new Vector3DInt(firstCorner));
        Vector3DDouble secondVector = new Vector3DDouble(new Vector3DInt(secondCorner));
        this.boundingBox = this.facing.createBoundingBox(firstVector, secondVector);

        // Combine with the base-block vector to expand it into the block
        this.blockBoundingBox = this.boundingBox.combine(BoundingBoxAPI.getAbsoluteBoundingBox(this.blockBaseVector.toBukkitLocation(getWorld()).getBlock()));

        this.minCorner2d = this.facing.getPlane().to2D(boundingBox.getMinVector());
        this.maxCorner2d = this.facing.getPlane().to2D(boundingBox.getMaxVector());
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public int getBlockWidth() {
        return this.maxCorner2d.getX().intValue() - this.minCorner2d.getX().intValue();
    }

    public int getBlockHeight() {
        return this.maxCorner2d.getY().intValue() - this.minCorner2d.getY().intValue();
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public boolean isOnBlock(Vector3DDouble blockVector) {
        blockVector = blockVector.add(new Vector3DDouble(.5, .5, .5));

        return blockBoundingBox.contains(blockVector);
    }

    public String getWorldName() {
        return worldName;
    }

    public Vector3DDouble getBlockBaseVector() {
        return blockBaseVector;
    }

    public Vector3DDouble getBaseVector() {
        return baseVector;
    }

    public BoundingBox getBlockBoundingBox() {
        return blockBoundingBox;
    }

    public MapFacing getFacing() {
        return facing;
    }

    public Vector2DDouble getMinCorner2d() {
        return minCorner2d;
    }

    public Vector2DDouble getMaxCorner2d() {
        return maxCorner2d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseFrameMapAbstract that = (BaseFrameMapAbstract) o;
        return Objects.equals(worldName, that.worldName) && Objects.equals(blockBaseVector, that.blockBaseVector) && Objects.equals(baseVector, that.baseVector) && Objects.equals(boundingBox, that.boundingBox) && Objects.equals(blockBoundingBox, that.blockBoundingBox) && facing == that.facing && Objects.equals(minCorner2d, that.minCorner2d) && Objects.equals(maxCorner2d, that.maxCorner2d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, blockBaseVector, baseVector, boundingBox, blockBoundingBox, facing, minCorner2d, maxCorner2d);
    }

    @Override
    public String toString() {
        return "BaseFrameMapAbstract{" +
                "worldName='" + worldName + '\'' +
                ", blockBaseVector=" + blockBaseVector +
                ", baseVector=" + baseVector +
                ", boundingBox=" + boundingBox +
                ", blockBoundingBox=" + blockBoundingBox +
                ", facing=" + facing +
                ", minCorner2d=" + minCorner2d +
                ", maxCorner2d=" + maxCorner2d +
                '}';
    }
}
