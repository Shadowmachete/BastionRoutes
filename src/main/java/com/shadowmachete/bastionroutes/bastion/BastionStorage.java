package com.shadowmachete.bastionroutes.bastion;

import static com.shadowmachete.bastionroutes.BastionRoutes.LOGGER;

import com.shadowmachete.bastionroutes.util.IntBoundingBox;
import com.shadowmachete.bastionroutes.waypoints.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BastionStorage {
    private static final BastionStorage INSTANCE = new BastionStorage();

    private boolean bastionsNeedUpdating;
    private BastionData currentBastion;
    private int currentBastionDistance;
    private BlockPos lastBastionUpdatePos;

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static BastionStorage getInstance() {
        return INSTANCE;
    }

    public void reset() {
        this.bastionsNeedUpdating = true;
        this.currentBastion = null;
        this.currentBastionDistance = Integer.MAX_VALUE;
        this.lastBastionUpdatePos = null;
    }

    public void setBastionsNeedUpdating() {
        this.bastionsNeedUpdating = true;
    }

    public void updateStructureData() {
        if (this.mc.isIntegratedServerRunning()) {
            BlockPos playerPos = this.mc.player.getBlockPos();
            if (this.bastionsNeedUpdating(playerPos, 32)) {
                this.updateStructureDataFromIntegratedServer(playerPos);
            }
        }
    }

    private boolean bastionsNeedUpdating(BlockPos playerPos, int threshold) {
        return this.bastionsNeedUpdating || this.lastBastionUpdatePos == null ||
                Math.abs(playerPos.getX() - this.lastBastionUpdatePos.getX()) >= threshold ||
                Math.abs(playerPos.getY() - this.lastBastionUpdatePos.getY()) >= threshold ||
                Math.abs(playerPos.getZ() - this.lastBastionUpdatePos.getZ()) >= threshold;
    }

    private void updateStructureDataFromIntegratedServer(final BlockPos playerPos) {
        final RegistryKey<World> worldKey = this.mc.player.getEntityWorld().getRegistryKey();
        final ServerWorld world = this.mc.getServer().getWorld(worldKey);

        if (worldKey != World.NETHER) {
            return;
        }

        if (world != null) {
            MinecraftServer server = this.mc.getServer();
            final int maxChunkRange = this.mc.options.viewDistance + 2;

            server.send(new ServerTask(server.getTicks(), () ->
            {
                this.addBastionDataFromGenerator(world, playerPos, maxChunkRange);
            }));
        } else {
            this.currentBastion = null;
        }

        this.lastBastionUpdatePos = playerPos;
        this.bastionsNeedUpdating = false;
    }

    private void addBastionDataFromGenerator(ServerWorld world, BlockPos playerPos, int maxChunkRange) {
        int minCX = (playerPos.getX() >> 4) - maxChunkRange;
        int minCZ = (playerPos.getZ() >> 4) - maxChunkRange;
        int maxCX = (playerPos.getX() >> 4) + maxChunkRange;
        int maxCZ = (playerPos.getZ() >> 4) + maxChunkRange;

        int closestBastionDistance = Integer.MAX_VALUE;

        for (int cz = minCZ; cz <= maxCZ; ++cz)
        {
            for (int cx = minCX; cx <= maxCX; ++cx)
            {
                // Don't load the chunk
                Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);

                if (chunk != null)
                {
                    StructureStart start = chunk.getStructureStart(StructureFeature.BASTION_REMNANT);

                    if (start != null)
                    {
                        if (this.isStructureWithinRange(start.getBoundingBox(), playerPos, maxChunkRange << 4))
                        {
                            LOGGER.info("Found bastion at chunk {}, {}", cx, cz);
                            LOGGER.info(" - Bounding box: {}", start.getBoundingBox());
                            StructurePiece firstPiece = (StructurePiece) start.getChildren().get(0);
                            String location_tag = firstPiece.getTag().getCompound("pool_element").getString("location");
                            BastionType bastionType = getBastionType(location_tag);
                            BastionData bastion = BastionData.fromStructureStart(bastionType, start);

                            int distanceOfNewBastion = distanceToPlayer(bastion.getBoundingBox(), playerPos);
                            LOGGER.info(" - Player is at: {}", playerPos);
                            LOGGER.info(" - Bastion type: {}", bastionType);
                            LOGGER.info(" - New bastion distance to player: {}", distanceOfNewBastion);
                            LOGGER.info(" - Closest bastion distance to player: {}", closestBastionDistance);
                            if (this.currentBastion == null || distanceOfNewBastion < closestBastionDistance) {
                                this.currentBastion = bastion;
                                closestBastionDistance = distanceOfNewBastion;
                                WaypointManager.globalAnchorPos = bastion.getBoundingBox().getCornerMin();
                                LOGGER.info(" - New closest bastion of type {} at distance {}", bastionType, distanceOfNewBastion);
                            }
                        }
                    }
                }
            }
        }
    }

    private static @NotNull BastionType getBastionType(String location_tag) {
        if (location_tag.contains("bridge")) {
            return BastionType.BRIDGE;
        } else if (location_tag.contains("hoglin_stable")) {
            return BastionType.STABLES;
        } else if (location_tag.contains("housing")) {
            return BastionType.HOUSING;
        } else if (location_tag.contains("treasure")) {
            return BastionType.TREASURE;
        } else {
            return BastionType.UNKNOWN;
        }
    }

    private static int distanceToPlayer(IntBoundingBox bb, BlockPos playerPos) {
        int dx = 0;
        if (playerPos.getX() < bb.minX) {
            dx = bb.minX - playerPos.getX();
        } else if (playerPos.getX() > bb.maxX) {
            dx = playerPos.getX() - bb.maxX;
        }

        int dz = 0;
        if (playerPos.getZ() < bb.minZ) {
            dz = bb.minZ - playerPos.getZ();
        } else if (playerPos.getZ() > bb.maxZ) {
            dz = playerPos.getZ() - bb.maxZ;
        }

        return (int) Math.sqrt(dx * dx + dz * dz);
    }

    private boolean isStructureWithinRange(@Nullable BlockBox bb, BlockPos playerPos, int maxRange) {
        return bb != null &&
                playerPos.getX() >= (bb.minX - maxRange) &&
                playerPos.getX() <= (bb.maxX + maxRange) &&
                playerPos.getZ() >= (bb.minZ - maxRange) &&
                playerPos.getZ() <= (bb.maxZ + maxRange);
    }
}
