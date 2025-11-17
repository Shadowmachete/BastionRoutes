package com.shadowmachete.bastionroutes.bastion;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class BastionPieceCollector {
    private final List<StructureBlockBlockEntity> collector = new ArrayList<>();
    private final List<String> pieces = new ArrayList<>();
    private BlockRotation rotation = BlockRotation.NONE;
    private Vec3i bastionAnchor = new Vec3i(0, 0, 0);

    public void addPiece(StructureBlockBlockEntity structureBlock, String pieceName) {
        collector.add(structureBlock);
        pieces.add(pieceName);
    }

    public void clear() {
        collector.clear();
        pieces.clear();
        rotation = BlockRotation.NONE;
    }

    public List<StructureBlockBlockEntity> getCollector() {
        return collector;
    }

    public List<String> getPieces() {
        return pieces;
    }

    public BlockRotation getRotation() {
        return rotation;
    }

    public void setRotation(BlockRotation rotation) {
        this.rotation = rotation;
    }

    public Vec3i getBastionAnchor() {
        return bastionAnchor;
    }

    public void setBastionAnchor(Vec3i bastionAnchor) {
        this.bastionAnchor = bastionAnchor;
    }

    public BastionData toBastionData(String bastionType) {
        return new BastionData(
                BastionType.valueOf(bastionType),
                bastionAnchor,
                rotation
        );
    }
}
