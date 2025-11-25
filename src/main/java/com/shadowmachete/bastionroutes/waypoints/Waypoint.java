package com.shadowmachete.bastionroutes.waypoints;

import com.shadowmachete.bastionroutes.render.Color;
import com.shadowmachete.bastionroutes.utils.RotationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3i;

import java.util.UUID;

public class Waypoint {
    public Coordinates coords;
    public String name;
    public boolean shouldRender;
    public UUID uuid;
    public Color color = Color.WHITE; // Default white colour

    // Create waypoint from coordinates and dimension
    public Waypoint(Coordinates coords, String name) {
        this.coords = coords;
        this.name = name;
        this.shouldRender = true;
        this.uuid = genUUID();
    }

    // Create absolute waypoint from current location with default name
    public Waypoint(MinecraftClient client) {
        if (client.player == null) {
            throw new IllegalStateException("Client player is null");
        }

        Vec3i pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos);
        // this.name = WaypointManager.getNewPlaceholderName();
        String name = "PLACEHOLDER_NAME"; // TODO: setup the placeholder name creation / automatic naming when recording new waypoint group
        this.coords = coords;
        this.name = name;
        this.shouldRender = true;
        this.uuid = genUUID();
    }

    // Create absolute waypoint from current location with given name
    public Waypoint(MinecraftClient client, String name) {
        if (client.player == null) {
            throw new IllegalStateException("Client player is null");
        }

        Vec3i pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos);
        this.coords = coords;
        this.name = name;
        this.shouldRender = true;
        this.uuid = genUUID();
    }

    // Create any type of waypoint from current location with default name
    public Waypoint(MinecraftClient client, CoordinateType type) {
        if (client.player == null) {
            throw new IllegalStateException("Client player is null");
        }

        Vec3i pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos, type);
        // this.name = WaypointManager.getNewPlaceholderName();
        String name = "PLACEHOLDER_NAME"; // TODO: setup the placeholder name creation / automatic naming when recording new waypoint group
        this.coords = coords;
        this.name = name;
        this.shouldRender = true;
        this.uuid = genUUID();
    }

    // Create any type of waypoint from current location with given name
    public Waypoint(MinecraftClient client, String name, CoordinateType type) {
        if (client.player == null) {
            throw new IllegalStateException("Client player is null");
        }

        Vec3i pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos, type);
        this.coords = coords;
        this.name = name;
        this.shouldRender = true;
        this.uuid = genUUID();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private static UUID genUUID() {
        return UUID.randomUUID();
    }

    public Waypoint rotateAroundAnchor(BlockRotation from, BlockRotation to) {
        if (this.coords.getType() == CoordinateType.ABSOLUTE) {
            throw new IllegalStateException("Cannot rotate ABSOLUTE waypoint");
        }

        BlockRotation relative = RotationUtils.calculateRelativeRotation(from, to);

        Vec3i pos = this.coords.getPos(WaypointManager.globalAnchorPos);
        int x = pos.getX() - WaypointManager.globalAnchorPos.getX();
        int y = pos.getY() - WaypointManager.globalAnchorPos.getY();
        int z = pos.getZ() - WaypointManager.globalAnchorPos.getZ();

        int rx = x;
        int rz = z;

        if (relative == BlockRotation.CLOCKWISE_90) {
            rx = -z;
            rz = x;
        } else if (relative == BlockRotation.COUNTERCLOCKWISE_90) {
            rx = z;
            rz = -x;
        } else if (relative == BlockRotation.CLOCKWISE_180) {
            rx = -x;
            rz = -z;
        }

        return new Waypoint(new Coordinates(rx, y, rz, CoordinateType.OFFSET), this.name);
    }
}
