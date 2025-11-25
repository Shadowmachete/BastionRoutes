package com.shadowmachete.bastionroutes.routes;

import static com.shadowmachete.bastionroutes.BastionRoutes.LOGGER;

import com.google.gson.Gson;
import com.shadowmachete.bastionroutes.bastion.BastionStorage;
import com.shadowmachete.bastionroutes.bastion.BastionType;
import com.shadowmachete.bastionroutes.utils.FileUtils;
import com.shadowmachete.bastionroutes.waypoints.CoordinateType;
import com.shadowmachete.bastionroutes.waypoints.Coordinates;
import com.shadowmachete.bastionroutes.waypoints.Waypoint;
import com.shadowmachete.bastionroutes.waypoints.WaypointManager;

import java.nio.file.Path;
import java.util.*;

public class RouteManager {
    public static List<Route> routes = new java.util.ArrayList<>();
    public static Path currentSavePath;
    public static Route currentRoute = null;
    public static boolean recording = false;

    private static final List<Waypoint> recordedWaypoints = new java.util.ArrayList<>();
    public static String newRouteName;

    public static void loadRoutes() {
        currentSavePath = FileUtils.getSavePath().resolve("bastion_routes.json");
        try {
            String json = FileUtils.readFile(currentSavePath);
            Gson gson = new Gson();
            Route[] loadedRoutes = gson.fromJson(json, Route[].class);
            routes = new java.util.ArrayList<>(java.util.Arrays.asList(loadedRoutes));
        } catch (Exception e) {
            LOGGER.error("Ran into error while loading routes: {}", String.valueOf(e));
        }
    }

    public static void saveRoutes() {
        Gson gson = new Gson();
        String json = gson.toJson(routes);

        try {
            FileUtils.writeFile(currentSavePath, json);
        } catch (Exception e) {
            LOGGER.error("Ran into error while saving routes: {}", String.valueOf(e));
        }
    }

    public static void addRoute(Route route) {
        routes.add(route);

        saveRoutes();
    }

    public static void removeRoute(Route route) {
        routes.remove(route);

        saveRoutes();
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
            addRoute(new Route(newRouteName, new java.util.ArrayList<>(recordedWaypoints), BastionStorage.getInstance().getCurrentBastion().getRotation(), BastionStorage.getInstance().getCurrentBastion().getBastionType()));
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

    public static void updateWaypointManager(Route route) {
        // Clear existing waypoints
        WaypointManager.clearWaypoints();

        if (!Objects.equals(currentRoute, route)) {
            currentRoute = route;

            // Add waypoints from the selected route
            WaypointManager.populateFromRoute(route);
            WaypointManager.currentWaypointIndex = 0;
        } else {
            currentRoute = null;
        }
    }

    public static Optional<Route> getRouteByUUID(UUID uuid) {
        return routes.stream().filter(w -> Objects.equals(w.uuid, uuid)).findFirst();
    }

    public static Optional<Route> getRouteByName(String name) {
        return routes.stream().filter(w -> Objects.equals(w.name, name)).findFirst();
    }

    public static String[] getRoutes() {
        return routes.stream().map(route -> {
            if (route.bastionType == BastionStorage.getInstance().getCurrentBastion().getBastionType()) return route.name;
            else return null;
        }).filter(Objects::nonNull).toArray(String[]::new);
    }

    public static String[] getRoutes(String strBastionType) {
        BastionType bastionType = BastionStorage.getBastionType(strBastionType);
        if (bastionType == BastionType.UNKNOWN) {
            return new String[0];
        }
        return routes.stream().map(route -> {
            if (route.bastionType == bastionType) return route.name;
            else return null;
        }).filter(Objects::nonNull).toArray(String[]::new);
    }
}
