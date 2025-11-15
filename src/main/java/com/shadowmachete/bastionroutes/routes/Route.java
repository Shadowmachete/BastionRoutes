package com.shadowmachete.bastionroutes.routes;

import com.shadowmachete.bastionroutes.waypoints.Waypoint;

import java.util.List;
import java.util.UUID;

public class Route {
    public String name;
    public UUID uuid;
    public List<Waypoint> waypoints;

    public Route(String name, List<Waypoint> waypoints) {
        this.name = name;
        this.waypoints = waypoints;
        this.uuid = genUUID();
    }

    private static UUID genUUID(){
        return UUID.randomUUID();
    }
}
