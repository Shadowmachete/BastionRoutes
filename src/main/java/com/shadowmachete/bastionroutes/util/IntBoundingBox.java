package com.shadowmachete.bastionroutes.util;

import net.minecraft.util.math.Vec3i;

public class IntBoundingBox {
    public static final IntBoundingBox ORIGIN = new IntBoundingBox(0, 0, 0, 0, 0, 0);

    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public IntBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public int getMinX()
    {
        return this.minX;
    }

    public int getMinY()
    {
        return this.minY;
    }

    public int getMinZ()
    {
        return this.minZ;
    }

    public int getMaxX()
    {
        return this.maxX;
    }

    public int getMaxY()
    {
        return this.maxY;
    }

    public int getMaxZ()
    {
        return this.maxZ;
    }

    public boolean contains(Vec3i pos)
    {
        return pos.getX() >= this.minX &&
                pos.getX() <= this.maxX &&
                pos.getZ() >= this.minZ &&
                pos.getZ() <= this.maxZ &&
                pos.getY() >= this.minY &&
                pos.getY() <= this.maxY;
    }

    public boolean intersects(IntBoundingBox box)
    {
        return this.maxX >= box.minX &&
                this.minX <= box.maxX &&
                this.maxZ >= box.minZ &&
                this.minZ <= box.maxZ &&
                this.maxY >= box.minY &&
                this.minY <= box.maxY;
    }

    public Vec3i getCornerMin()
    {
        return new Vec3i(this.minX, this.minY, this.minZ);
    }

    public Vec3i getCornerMax()
    {
        return new Vec3i(this.maxX, this.maxY, this.maxZ);
    }

    public static IntBoundingBox fromVanillaBox(net.minecraft.util.math.BlockBox box)
    {
        return new IntBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }
}
