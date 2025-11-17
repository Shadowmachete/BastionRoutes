package com.shadowmachete.bastionroutes.bastion;

import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class BastionData {
    private final BastionType type;
    private Vec3i bastionAnchor;
    private long refreshTime;
    private BlockRotation rotation = BlockRotation.NONE;

    public BastionData(BastionType type, Vec3i bastionAnchor, BlockRotation rotation) {
        this.type = type;
        this.bastionAnchor = bastionAnchor;
        this.rotation = rotation;
    }

    public BastionData(BastionType type, Vec3i bastionAnchor, long refreshTime, BlockRotation rotation) {
        this.type = type;
        this.bastionAnchor = bastionAnchor;
        this.refreshTime = refreshTime;
        this.rotation = rotation;
    }

    public BastionType getBastionType() {
        return this.type;
    }

    public Vec3i getBastionAnchor() {
        return this.bastionAnchor;
    }

    public long getRefreshTime() {
        return this.refreshTime;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public static BastionData fromStructureStart(BastionType type, StructureStart structure) {
        List<StructurePiece> components = structure.getChildren();

        BlockRotation rotation = components.get(0).getRotation();

        Vec3i offset = type.getOffsetQuadrantToStructure();
        BlockBox quadrantAnchor = structure.getBoundingBox();
        Vec3i llamaAnchor = new Vec3i(
                quadrantAnchor.minX + offset.getX(),
                quadrantAnchor.minY + offset.getY(),
                quadrantAnchor.minZ + offset.getZ()
        );

        // TODO: Adjust llamaAnchor offsets based on rotation
        // Not so important to fix for now since LBP is the main focus

        return new BastionData(type, llamaAnchor, rotation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        BastionData other = (BastionData) obj;

        if (this.bastionAnchor == null) {
            if (other.bastionAnchor != null) {
                return false;
            }
        } else if (!this.bastionAnchor.equals(other.bastionAnchor)) {
            return false;
        }

        return this.type == other.type;
    }
}
