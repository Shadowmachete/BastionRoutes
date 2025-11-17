package com.shadowmachete.bastionroutes.bastion;

import net.minecraft.util.math.Vec3i;

public enum BastionType {
    BRIDGE,
    HOUSING,
    STABLES,
    TREASURE,
    UNKNOWN;

    // TODO: These offsets need to be determined accurately in game
    // Need to add in for different rotations too
    // Least of my worries for now since LBP is more important
    public Vec3i getOffsetQuadrantToStructure() {
        if (this == BRIDGE) {
            return new Vec3i(0, 0, 0);
        } else if (this == HOUSING) {
            return new Vec3i(0, 0, 0);
        } else if (this == STABLES) {
            return new Vec3i(0, 0, 0);
        } else if (this == TREASURE) {
            return new Vec3i(0, 0, 0);
        } else {
            return new Vec3i(0, 0, 0);
        }
    }
}
