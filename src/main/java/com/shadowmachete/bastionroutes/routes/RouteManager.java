package com.shadowmachete.bastionroutes.routes;

import com.shadowmachete.bastionroutes.waypoints.CoordinateType;
import com.shadowmachete.bastionroutes.waypoints.Coordinates;
import com.shadowmachete.bastionroutes.waypoints.Waypoint;
import com.shadowmachete.bastionroutes.waypoints.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3i;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RouteManager {
    public static List<Route> routes = new java.util.ArrayList<>();
    public static Path currentSavePath;
    public static Route currentRoute = null;
    public static boolean recording = false;

    private static final List<Waypoint> recordedWaypoints = new java.util.ArrayList<>();
    public static String newRouteName;

    public static void loadRoutes() {}
    public static void saveRoutes() {}

    public static void addRoute(Route route) {
        routes.add(route);
    }

    public static void removeRoute(Route route) {
        routes.remove(route);
    }

    public static void recordRoute(String name) {
        if (recording) {
            throw new IllegalStateException("Already recording a route");
        } else {
            recording = true;
            newRouteName = name;
            recordedWaypoints.clear();
        }
    }

    public static void recordRoute() {
        if (recording) {
            throw new IllegalStateException("Already recording a route");
        } else {
            recording = true;
            newRouteName = "Route " + (routes.size() + 1);
            recordedWaypoints.clear();
        }
    }

    public static void recordWaypoint(Waypoint waypoint) {
        if (!recording) {
            throw new IllegalStateException("Not currently recording a route");
        } else {
            recordedWaypoints.add(waypoint);
        }
    }

    public static void recordWaypoint(Coordinates coords) {
        if (!recording) {
            throw new IllegalStateException("Not currently recording a route");
        } else {
            if (WaypointManager.globalAnchorPos != null) {
                int x = coords.getPos().getX() - WaypointManager.globalAnchorPos.getX();
                int y = coords.getPos().getY() - WaypointManager.globalAnchorPos.getY();
                int z = coords.getPos().getZ() - WaypointManager.globalAnchorPos.getZ();
                coords = new Coordinates(x, y, z, CoordinateType.OFFSET);
            }
            recordedWaypoints.add(new Waypoint(coords, "Waypoint " + (recordedWaypoints.size() + 1)));
        }
    }

    public static void recordWaypoint(Coordinates coords, String name) {
        if (!recording) {
            throw new IllegalStateException("Not currently recording a route");
        } else {
            if (WaypointManager.globalAnchorPos != null) {
                int x = coords.getPos().getX() - WaypointManager.globalAnchorPos.getX();
                int y = coords.getPos().getY() - WaypointManager.globalAnchorPos.getY();
                int z = coords.getPos().getZ() - WaypointManager.globalAnchorPos.getZ();
                coords = new Coordinates(x, y, z, CoordinateType.OFFSET);
            }
            recordedWaypoints.add(new Waypoint(coords, name));
        }
    }

    public static void stopRecordingRoute() {
        if (!recording) {
            throw new IllegalStateException("Not currently recording a route");
        } else {
            recording = false;
            routes.add(new Route(newRouteName, new java.util.ArrayList<>(recordedWaypoints)));
            recordedWaypoints.clear();
        }
    }

    public static void setCurrentRoute(UUID uuid) {
        Optional<Route> routeOpt = getRouteByUUID(uuid);
        routeOpt.ifPresent(RouteManager::updateWaypointManager);
    }

    public static void setCurrentRoute(String name) {
        Optional<Route> routeOpt = getRouteByName(name);
        routeOpt.ifPresent(RouteManager::updateWaypointManager);
    }

    public static void setCurrentRoute(Route route) {
        if (routes.contains(route)) {
            updateWaypointManager(route);
        }
    }

    private static void updateWaypointManager(Route route) {
        // Clear existing waypoints
        WaypointManager.clearWaypoints();

        if (!Objects.equals(currentRoute, route)) {
            currentRoute = route;

            // Add waypoints from the selected route
            for (Waypoint waypoint : route.waypoints) {
                WaypointManager.addWaypoint(waypoint);
            }
        } else {
            currentRoute = null;
        }
    }

    public static Optional<Route> getRouteByUUID(UUID uuid){
        return routes.stream().filter(w -> Objects.equals(w.uuid, uuid)).findFirst();
    }

    public static Optional<Route> getRouteByName(String name){
        return routes.stream().filter(w -> Objects.equals(w.name, name)).findFirst();
    }
}
