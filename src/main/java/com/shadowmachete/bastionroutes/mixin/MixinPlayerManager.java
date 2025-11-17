package com.shadowmachete.bastionroutes.mixin;

import static com.shadowmachete.bastionroutes.BastionRoutes.LOGGER;

import com.shadowmachete.bastionroutes.BastionRoutes;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.SaveProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        SaveProperties saveProperties = player.getServer().getSaveProperties();
        if (saveProperties != null) {
            String levelName = saveProperties.getLevelName();
            levelName = levelName.toLowerCase();
            // Check for keywords indicating Bastion/LBP world
            // This is probably bad because you can rename the map and it won't work
            if (levelName.contains("bastion") || levelName.contains("lbp") || levelName.contains("llama")) {
                LOGGER.info("Detected Bastion/LBP world. Setting inLBP to true.");
                BastionRoutes.setInLBP(true);
            } else {
                LOGGER.info("Non-Bastion/LBP world detected. Setting inLBP to false.");
                BastionRoutes.setInLBP(false);
            }
        }
    }
}
