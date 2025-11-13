package com.shadowmachete.bastionroutes.waypoints;

import com.shadowmachete.bastionroutes.render.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class Waypoint {
    public Coordinates coords;
    public String name;
    public boolean shouldRender;
    public String uuid;
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

        BlockPos pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
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

        BlockPos pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
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

        BlockPos pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos.getX(), pos.getY(), pos.getZ(), type);
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

        BlockPos pos = client.player.getBlockPos();
        Coordinates coords = new Coordinates(pos.getX(), pos.getY(), pos.getZ(), type);
        this.coords = coords;
        this.name = name;
        this.shouldRender = true;
        this.uuid = genUUID();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private static String genUUID(){
        return UUID
               .randomUUID()
               .toString()
               .replace("-", "")
               .substring(0, 8);
    }
}
