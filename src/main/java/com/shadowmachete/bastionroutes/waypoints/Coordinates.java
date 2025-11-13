package com.shadowmachete.bastionroutes.waypoints;

import net.minecraft.util.math.Vec3i;

public class Coordinates {
    private Vec3i pos;
    public CoordinateType type = CoordinateType.ABSOLUTE;

    public Coordinates(int x, int y, int z){
        this.pos = new Vec3i(x, y, z);
    }

    public Coordinates(int x, int y, int z, CoordinateType type){
        this.pos = new Vec3i(x, y, z);
        this.type = type;
    }

    // Get absolute position
    public Vec3i getPos() {
        if (type == CoordinateType.OFFSET) {
            throw new IllegalStateException("Cannot get absolute position from OFFSET type");
        }

        return this.pos;
    }

    // Get absolute position based on an anchor position
    public Vec3i getPos(Vec3i anchorPos) {
        if (type == CoordinateType.ABSOLUTE) {
            throw new IllegalStateException("Cannot get offset position from ABSOLUTE type");
        }

        return new Vec3i(
                anchorPos.getX() + this.pos.getX(),
                anchorPos.getY() + this.pos.getY(),
                anchorPos.getZ() + this.pos.getZ()
        );
    }

    public CoordinateType getType() {
        return type;
    }

}