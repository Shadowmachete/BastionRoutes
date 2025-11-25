package com.shadowmachete.bastionroutes.waypoints;

import net.minecraft.util.math.Vec3i;

public class Coordinates {
    private final int x;
    private final int y;
    private final int z;
    public CoordinateType type = CoordinateType.ABSOLUTE;

    public Coordinates(Vec3i pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Coordinates(Vec3i pos, CoordinateType type) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.type = type;
    }

    public Coordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinates(int x, int y, int z, CoordinateType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public Vec3i toVec3i() {
        return new Vec3i(x, y, z);
    }

    // Get absolute position
    public Vec3i getPos() {
        if (type == CoordinateType.OFFSET) {
            throw new IllegalStateException("Cannot get absolute position from OFFSET type");
        }

        return toVec3i();
    }

    // Get absolute position based on an anchor position
    public Vec3i getPos(Vec3i anchorPos) {
        if (type == CoordinateType.ABSOLUTE) {
            throw new IllegalStateException("Cannot get offset position from ABSOLUTE type");
        }

        return new Vec3i(
                anchorPos.getX() + this.x,
                anchorPos.getY() + this.y,
                anchorPos.getZ() + this.z
        );
    }

    public CoordinateType getType() {
        return type;
    }

}