package com.shadowmachete.bastionroutes.utils;

import net.minecraft.util.BlockRotation;

public class RotationUtils {
    public static BlockRotation calculateRelativeRotation(BlockRotation from, BlockRotation to) {
        if (from == to) {
            return BlockRotation.NONE;
        }

        if (from == BlockRotation.NONE) {
            return to;
        }

        if (from == BlockRotation.CLOCKWISE_90) {
            if (to == BlockRotation.NONE) {
                return BlockRotation.COUNTERCLOCKWISE_90;
            } else if (to == BlockRotation.CLOCKWISE_180) {
                return BlockRotation.CLOCKWISE_90;
            } else if (to == BlockRotation.COUNTERCLOCKWISE_90) {
                return BlockRotation.CLOCKWISE_180;
            }
        }

        if (from == BlockRotation.CLOCKWISE_180) {
            if (to == BlockRotation.NONE) {
                return BlockRotation.CLOCKWISE_180;
            } else if (to == BlockRotation.CLOCKWISE_90) {
                return BlockRotation.COUNTERCLOCKWISE_90;
            } else if (to == BlockRotation.COUNTERCLOCKWISE_90) {
                return BlockRotation.CLOCKWISE_90;
            }
        }

        if (from == BlockRotation.COUNTERCLOCKWISE_90) {
            if (to == BlockRotation.NONE) {
                return BlockRotation.CLOCKWISE_90;
            } else if (to == BlockRotation.CLOCKWISE_90) {
                return BlockRotation.CLOCKWISE_180;
            } else if (to == BlockRotation.CLOCKWISE_180) {
                return BlockRotation.COUNTERCLOCKWISE_90;
            }
        }

        return from;
    }
}
