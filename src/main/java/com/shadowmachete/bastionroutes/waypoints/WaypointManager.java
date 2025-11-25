package com.shadowmachete.bastionroutes.waypoints;

import com.shadowmachete.bastionroutes.bastion.BastionStorage;
import com.shadowmachete.bastionroutes.render.Color;
import com.shadowmachete.bastionroutes.routes.Route;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class WaypointManager {
    public static List<Waypoint> waypoints = new java.util.ArrayList<>();
    public static Vec3i globalAnchorPos = null;
    public static int currentWaypointIndex = -1;

    public static boolean shouldRenderGlobally = true;

    public static void populateFromRoute(Route route) {
        clearWaypoints();
        for (Waypoint waypoint : route.waypoints) {
            if (route.rotation != BastionStorage.getInstance().getCurrentBastion().getRotation()) {
                Waypoint rotatedWaypoint = waypoint.rotateAroundAnchor(route.rotation, BastionStorage.getInstance().getCurrentBastion().getRotation());
                WaypointManager.addWaypoint(rotatedWaypoint);
            } else {
                WaypointManager.addWaypoint(waypoint);
            }
        }
    }

    public static void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public static void removeWaypoint(Waypoint waypoint) {
        waypoints.remove(waypoint);
    }

    public static void clearWaypoints() {
        waypoints.clear();
    }

    public static void updateCurrentWaypointIndex(MinecraftClient client) {
        // if client is at the same block or within +- 0.5 of the current waypoint, increment the index
        if (currentWaypointIndex == -1) {
            return; // no current waypoint to check
        }

        if (currentWaypointIndex > waypoints.size() - 1) {
            currentWaypointIndex = -1; // reset waypoint if we have reached the end
            return;
        }

        if (client.player != null) {
            Waypoint currentWaypoint = waypoints.get(currentWaypointIndex);
            Vec3i waypointPos;
            if (currentWaypoint.coords.getType() == CoordinateType.OFFSET) {
                waypointPos = currentWaypoint.coords.getPos(globalAnchorPos);
            } else {
                waypointPos = currentWaypoint.coords.getPos();
            }
            Vec3d playerPos = client.player.getPos();
            if (Math.abs(playerPos.x - (waypointPos.getX() + 1.0)) <= 1.0 &&
                Math.abs(playerPos.y - (waypointPos.getY() + 1.0)) <= 1.0 &&
                Math.abs(playerPos.z - (waypointPos.getZ() + 1.0)) <= 1.0) {
                currentWaypointIndex++;
            }
        }
    }

    public static void renderWaypoints() {
        if (!shouldRenderGlobally) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        Vec3d camPos = client.gameRenderer.getCamera().getPos();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint waypoint = waypoints.get(i);
            if (i < currentWaypointIndex) {
                waypoint.setColor(Color.GRAY);
            } else if (i == currentWaypointIndex) {
                waypoint.setColor(Color.AQUA);
            } else {
                waypoint.setColor(Color.WHITE);
            }

            if (!waypoint.shouldRender || waypoint.coords.getType() == CoordinateType.OFFSET && globalAnchorPos == null) {
                continue;
            }

            renderWaypoint(waypoint, bufferBuilder, camPos);
        }
    }

    public static void renderWaypoint(@NotNull Waypoint waypoint, BufferBuilder bufferBuilder, Vec3d camPos) {
        Coordinates coords = waypoint.coords;
        Vec3i pos;
        if (coords.getType() == CoordinateType.OFFSET) {
            pos = coords.getPos(globalAnchorPos);
        } else {
            pos = coords.getPos();
        }

        drawCircle(bufferBuilder, camPos, pos, 0.4, 0.0, waypoint.color);
    }

    private static void drawCircle(@NotNull BufferBuilder bufferBuilder, Vec3d camPos, Vec3i pos, double radius, double y, Color color) {
        bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);

        int segments = 32;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2.0 * Math.PI * i / segments);
            float angle2 = (float) (2.0 * Math.PI * (i + 1) / segments);

            float x1 = (float) (Math.cos(angle1) * radius);
            float z1 = (float) (Math.sin(angle1) * radius);
            float x2 = (float) (Math.cos(angle2) * radius);
            float z2 = (float) (Math.sin(angle2) * radius);

            bufferBuilder.vertex(x1 + pos.getX() - camPos.x + 0.5, (float) y + pos.getY() - camPos.y, z1 + pos.getZ() - camPos.z + 0.5).color(color.getRedF(), color.getGreenF(), color.getBlueF(), 1.0f).next();
            bufferBuilder.vertex(x2 + pos.getX() - camPos.x + 0.5, (float) y + pos.getY() - camPos.y, z2 + pos.getZ() - camPos.z + 0.5).color(color.getRedF(), color.getGreenF(), color.getBlueF(), 1.0f).next();
        }

        Tessellator.getInstance().draw();
    }

    public static void toggleGlobalVisibility() {
        shouldRenderGlobally = !shouldRenderGlobally;
    }

    public static Optional<Waypoint> getWaypointByUUID(UUID uuid) {
        return waypoints.stream().filter(w -> Objects.equals(w.uuid, uuid)).findFirst();
    }

    public static Optional<Waypoint> getWaypointByName(String name) {
        return waypoints.stream().filter(w -> Objects.equals(w.name, name)).findFirst();
    }
}
