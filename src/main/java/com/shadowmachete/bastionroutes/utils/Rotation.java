package com.shadowmachete.bastionroutes.utils;

import net.minecraft.util.BlockRotation;

public enum Rotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    COUNTERCLOCKWISE_90;

    public static Rotation from(BlockRotation blockRotation) {
        if (blockRotation == BlockRotation.NONE) {
            return Rotation.NONE;
        } else if (blockRotation == BlockRotation.CLOCKWISE_90) {
            return Rotation.CLOCKWISE_90;
        } else if (blockRotation == BlockRotation.CLOCKWISE_180) {
            return Rotation.CLOCKWISE_180;
        } else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
            return Rotation.COUNTERCLOCKWISE_90;
        } else {
            return Rotation.NONE;
        }
    }

    public Rotation calculateRelativeRotation(Rotation to) {
        if (this == to) {
            return Rotation.NONE;
        }

        if (this == Rotation.NONE) {
            return to;
        }

        if (this == Rotation.CLOCKWISE_90) {
            if (to == Rotation.NONE) {
                return Rotation.COUNTERCLOCKWISE_90;
            } else if (to == Rotation.CLOCKWISE_180) {
                return Rotation.CLOCKWISE_90;
            } else if (to == Rotation.COUNTERCLOCKWISE_90) {
                return Rotation.CLOCKWISE_180;
            }
        }

        if (this == Rotation.CLOCKWISE_180) {
            if (to == Rotation.NONE) {
                return Rotation.CLOCKWISE_180;
            } else if (to == Rotation.CLOCKWISE_90) {
                return Rotation.COUNTERCLOCKWISE_90;
            } else if (to == Rotation.COUNTERCLOCKWISE_90) {
                return Rotation.CLOCKWISE_90;
            }
        }

        if (this == Rotation.COUNTERCLOCKWISE_90) {
            if (to == Rotation.NONE) {
                return Rotation.CLOCKWISE_90;
            } else if (to == Rotation.CLOCKWISE_90) {
                return Rotation.CLOCKWISE_180;
            } else if (to == Rotation.CLOCKWISE_180) {
                return Rotation.COUNTERCLOCKWISE_90;
            }
        }

        return this;
    }
}
