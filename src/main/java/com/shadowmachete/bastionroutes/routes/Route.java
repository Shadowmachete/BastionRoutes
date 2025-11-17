package com.shadowmachete.bastionroutes.routes;

import com.shadowmachete.bastionroutes.bastion.BastionType;
import com.shadowmachete.bastionroutes.waypoints.Waypoint;
import net.minecraft.util.BlockRotation;

import java.util.List;
import java.util.UUID;

public class Route {
    public String name;
    public UUID uuid;
    public List<Waypoint> waypoints;
    public BlockRotation rotation;
    public BastionType bastionType;

    public Route(String name, List<Waypoint> waypoints, BlockRotation rotation, BastionType type) {
        this.name = name;
        this.waypoints = waypoints;
        this.uuid = genUUID();
        this.rotation = rotation;
        this.bastionType = type;
    }

    private static UUID genUUID() {
        return UUID.randomUUID();
    }
}
