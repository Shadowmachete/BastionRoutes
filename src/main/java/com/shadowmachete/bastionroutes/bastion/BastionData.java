package com.shadowmachete.bastionroutes.bastion;

import com.google.common.collect.ImmutableList;
import com.shadowmachete.bastionroutes.util.IntBoundingBox;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;

import java.util.List;

public class BastionData {
    private final BastionType type;
    private final IntBoundingBox mainBox;
    private final ImmutableList<IntBoundingBox> componentBoxes;
    private long refreshTime;

    public BastionData(BastionType type, IntBoundingBox mainBox, ImmutableList<IntBoundingBox> componentBoxes) {
        this.type = type;
        this.mainBox = mainBox;
        this.componentBoxes = componentBoxes;
    }

    public BastionData(BastionType type, IntBoundingBox mainBox, ImmutableList<IntBoundingBox> componentBoxes, long refreshTime) {
        this.type = type;
        this.mainBox = mainBox;
        this.componentBoxes = componentBoxes;
        this.refreshTime = refreshTime;
    }

    public BastionType getBastionType()
    {
        return this.type;
    }

    public IntBoundingBox getBoundingBox()
    {
        return this.mainBox;
    }

    public ImmutableList<IntBoundingBox> getComponents()
    {
        return this.componentBoxes;
    }

    public long getRefreshTime()
    {
        return this.refreshTime;
    }

    public static BastionData fromStructureStart(BastionType type, StructureStart structure)
    {
        ImmutableList.Builder<IntBoundingBox> builder = ImmutableList.builder();
        List<StructurePiece> components = structure.getChildren();

        for (StructurePiece component : components)
        {
            builder.add(IntBoundingBox.fromVanillaBox(component.getBoundingBox()));
        }

        return new BastionData(type, IntBoundingBox.fromVanillaBox(structure.getBoundingBox()), builder.build());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass())
        {
            return false;
        }

        BastionData other = (BastionData) obj;

        if (this.componentBoxes == null)
        {
            if (other.componentBoxes != null)
            {
                return false;
            }
        }
        else if (! this.componentBoxes.equals(other.componentBoxes))
        {
            return false;
        }

        if (this.mainBox == null)
        {
            if (other.mainBox != null)
            {
                return false;
            }
        }
        else if (!this.mainBox.equals(other.mainBox))
        {
            return false;
        }

        return this.type == other.type;
    }
}
