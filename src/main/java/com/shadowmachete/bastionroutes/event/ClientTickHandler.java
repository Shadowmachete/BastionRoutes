package com.shadowmachete.bastionroutes.event;

import com.shadowmachete.bastionroutes.bastion.BastionStorage;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler
{
    public static void onClientTick(MinecraftClient mc)
    {
        if (mc.world != null && mc.player != null && mc.world.getTime() % 20 == 0)
        {
            BastionStorage.getInstance().updateStructureData();
        }
    }
}