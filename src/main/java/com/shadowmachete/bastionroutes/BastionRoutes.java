package com.shadowmachete.bastionroutes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.shadowmachete.bastionroutes.bastion.BastionStorage;
import com.shadowmachete.bastionroutes.render.RenderQueue;
import com.shadowmachete.bastionroutes.routes.RouteManager;
import com.shadowmachete.bastionroutes.utils.FileUtils;
import com.shadowmachete.bastionroutes.waypoints.WaypointManager;
import net.fabricmc.api.ClientModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class BastionRoutes implements ClientModInitializer {
    public static final String MOD_ID = "bastion-waypoints";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static boolean inLBP = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting up...");
        BastionStorage.getInstance().reset();
        RouteManager.loadRoutes();
        registerRenderers();

        try {
            FileUtils.createDefaultPath();
        } catch (IOException e) {
            LOGGER.error("Ran into error while creating default path: {0}", e);
        }
    }

    private void registerRenderers() {
        RenderQueue.getInstance().add("hand", matrixStack -> {
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.peek().getModel());
            GlStateManager.disableTexture();
            GlStateManager.disableDepthTest();
            RenderSystem.defaultBlendFunc();

            WaypointManager.renderWaypoints();

            RenderSystem.popMatrix();
        });
    }

    public static boolean inLBP() {
        return inLBP;
    }

    public static void setInLBP(boolean LBP) {
        inLBP = LBP;
    }
}