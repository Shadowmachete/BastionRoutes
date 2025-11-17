package com.shadowmachete.bastionroutes.bastion;

import static com.shadowmachete.bastionroutes.BastionRoutes.LOGGER;

import com.shadowmachete.bastionroutes.routes.RouteManager;
import com.shadowmachete.bastionroutes.waypoints.WaypointManager;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BastionStorage {
    private static final BastionStorage INSTANCE = new BastionStorage();

    private boolean bastionsNeedUpdating;
    private BastionData currentBastion;
    private BlockPos lastBastionUpdatePos;

    private final BastionPieceCollector bridgeCollector = new BastionPieceCollector();
    private final BastionPieceCollector stablesCollector = new BastionPieceCollector();
    private final BastionPieceCollector housingCollector = new BastionPieceCollector();
    private final BastionPieceCollector treasureCollector = new BastionPieceCollector();

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static BastionStorage getInstance() {
        return INSTANCE;
    }

    public void reset() {
        this.bastionsNeedUpdating = true;
        this.currentBastion = null;
        this.lastBastionUpdatePos = null;

        this.bridgeCollector.clear();
        this.stablesCollector.clear();
        this.housingCollector.clear();
        this.treasureCollector.clear();
    }

    public void setBastionsNeedUpdating() {
        this.bastionsNeedUpdating = true;
    }

    public BastionData getCurrentBastion() {
        return this.currentBastion;
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
            final int maxChunkRange = 6; // get this from config

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

        for (int cz = minCZ; cz <= maxCZ; ++cz) {
            for (int cx = minCX; cx <= maxCX; ++cx) {
                // Don't load the chunk
                Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);

                if (chunk != null) {
                    StructureStart start = chunk.getStructureStart(StructureFeature.BASTION_REMNANT);

                    if (start != null) {
                        if (this.isStructureWithinRange(start.getBoundingBox(), playerPos, maxChunkRange << 4)) {
                            if (!WaypointManager.shouldRenderGlobally) {
                                WaypointManager.shouldRenderGlobally = true;
                            }
                            LOGGER.info("Found bastion at chunk {}, {}", cx, cz);
                            LOGGER.info(" - Bounding box: {}", start.getBoundingBox());
                            StructurePiece firstPiece = (StructurePiece) start.getChildren().get(0);
                            String location_tag = firstPiece.getTag().getCompound("pool_element").getString("location");
                            BastionType bastionType = getBastionType(location_tag);
                            BastionData bastion = BastionData.fromStructureStart(bastionType, start);

                            if (Objects.equals(bastion, this.currentBastion)) {
                                LOGGER.info(" - Bastion already known, skipping.");
                                continue;
                            }

                            int distanceOfNewBastion = distanceToPlayer(bastion.getBastionAnchor(), playerPos);
                            LOGGER.info(" - Player is at: {}", playerPos);
                            LOGGER.info(" - Bastion type: {}", bastionType);
                            LOGGER.info(" - New bastion distance to player: {}", distanceOfNewBastion);
                            LOGGER.info(" - Closest bastion distance to player: {}", closestBastionDistance);
                            if (this.currentBastion == null || distanceOfNewBastion < closestBastionDistance) {
                                setBastion(bastion);
                                closestBastionDistance = distanceOfNewBastion;
                            }
                        }
                    }
                }
            }
        }
        if (closestBastionDistance == Integer.MAX_VALUE && this.currentBastion != null) {
            LOGGER.info("No bastions found within range. Clearing current bastion.");
            this.currentBastion = null;
            WaypointManager.globalAnchorPos = null;
            WaypointManager.shouldRenderGlobally = false;
        }
    }

    public void setBastion(BastionData bastion) {
        this.currentBastion = bastion;
        WaypointManager.globalAnchorPos = bastion.getBastionAnchor();
        LOGGER.info("Set current bastion to type {} at anchor {}", bastion.getBastionType(), bastion.getBastionAnchor());
    }

    public void addBastionDataFromStructureBlock(StructureBlockBlockEntity blockEntity) {
        // Only designed to collect bastion pieces from structure blocks in the order used by Llama's Bastion Practice map
//        LOGGER.info("Structure Block LOAD action triggered at {}", blockEntity.getPos());
//        LOGGER.info("Name: {}", blockEntity.getStructureName());
//        LOGGER.info("Bastion Type: {}", BastionStorage.getBastionType(blockEntity.getStructureName()));
//        LOGGER.info("Rotation: {}", blockEntity.toTag(new CompoundTag()).getString("rotation"));
        BastionType bastionType = BastionStorage.getBastionType(blockEntity.getStructureName());
        if (bastionType == BastionType.UNKNOWN) {
            LOGGER.warn("Unknown bastion type for structure name: {}", blockEntity.getStructureName());
            WaypointManager.shouldRenderGlobally = false;
            WaypointManager.globalAnchorPos = null;
        } else if (bastionType == BastionType.BRIDGE) {
            this.collectBridgePieces(blockEntity);
        } else if (bastionType == BastionType.STABLES) {
            this.collectStablesPieces(blockEntity);
        } else if (bastionType == BastionType.TREASURE) {
            this.collectTreasurePieces(blockEntity);
        } else if (bastionType == BastionType.HOUSING) {
            this.collectHousingPieces(blockEntity);
        }
    }

    public void updateRoutesBasedOnBastion(BastionType bastionType) {
        if (RouteManager.currentRoute != null) {
            if (bastionType != RouteManager.currentRoute.bastionType) {
                LOGGER.info("Disabling current route because bastion type changed.");
                WaypointManager.shouldRenderGlobally = false;
                WaypointManager.clearWaypoints();
            } else {
                LOGGER.info("Current route bastion type matches detected bastion type.");
                WaypointManager.shouldRenderGlobally = true;
                WaypointManager.populateFromRoute(RouteManager.currentRoute);
            }
        }
    }

    private void collectBridgePieces(StructureBlockBlockEntity blockEntity) {
        String structureName = blockEntity.getStructureName();

        if (structureName.contains("entrance_base")) { // new bridge bastion
            updateRoutesBasedOnBastion(BastionType.BRIDGE);
            LOGGER.info("New bastion anchor pos: {}", blockEntity.getPos());
            this.bridgeCollector.clear();
            BlockRotation rotation = blockEntity.getRotation();
            this.bridgeCollector.setRotation(rotation);
            this.bridgeCollector.setBastionAnchor(blockEntity.getPos());
        }

        String[] parts = structureName.split("/");
        String last_segment = parts[parts.length - 1];
        this.bridgeCollector.addPiece(blockEntity, last_segment);

        if (this.bridgePiecesDoneCollecting()) {
            // bridge pieces done collecting
            LOGGER.info("Full bridge bastion pieces collected");
            setBastion(bridgeCollector.toBastionData("BRIDGE"));
        }
    }

    private boolean bridgePiecesDoneCollecting() {
        // relying on llama bastion practice's generation order
        // the practice map generates legs last for bridge bastions
        long legCount = bridgeCollector.getPieces().stream()
                .filter(s -> s.startsWith("leg_"))
                .count();
        return legCount == 2;
    }

    private void collectStablesPieces(StructureBlockBlockEntity blockEntity) {
        String structureName = blockEntity.getStructureName();

        if (structureName.contains("lower/lower_")) { // new stables bastion
            updateRoutesBasedOnBastion(BastionType.STABLES);
            LOGGER.info("New bastion anchor pos: {}", blockEntity.getPos());
            this.stablesCollector.clear();
            BlockRotation rotation = blockEntity.getRotation();
            this.stablesCollector.setRotation(rotation);
            this.stablesCollector.setBastionAnchor(blockEntity.getPos());
        }

        String[] parts = structureName.split("/");
        String last_segment = parts[parts.length - 1];
        this.stablesCollector.addPiece(blockEntity, last_segment);

        if (this.stablesPiecesDoneCollecting(last_segment)) {
            // stables pieces done collecting
            LOGGER.info("Full stables bastion pieces collected");
            setBastion(stablesCollector.toBastionData("STABLES"));
        }
    }

    private boolean stablesPiecesDoneCollecting(String last_segment) {
        // relying on llama bastion practice's generation order
        // the practice map generates ramparts last for stables bastions
        if (!last_segment.contains("rampart")) {
            return false;
        }

        long rampartCount = stablesCollector.getPieces().stream()
                .filter(s -> s.startsWith("ramparts_"))
                .count();

        if (last_segment.contains("rampart_") && rampartCount == 3) { // detect rampart plate placement of last rampart
            return true;
        }

        int number = Integer.parseInt(last_segment.substring("ramparts_".length()));
        return (number == 2 || number == 3) && rampartCount == 3; // check that this is last rampart piece
    }

    private void collectTreasurePieces(StructureBlockBlockEntity blockEntity) {
        String structureName = blockEntity.getStructureName();

        if (structureName.contains("lower/lower_")) { // new treasure bastion
            updateRoutesBasedOnBastion(BastionType.TREASURE);
            LOGGER.info("New bastion anchor pos: {}", blockEntity.getPos());
            this.treasureCollector.clear();
            BlockRotation rotation = blockEntity.getRotation();
            this.treasureCollector.setRotation(rotation);
            this.treasureCollector.setBastionAnchor(blockEntity.getPos());
        }

        String[] parts = structureName.split("/");
        String last_segment = parts[parts.length - 1];
        this.treasureCollector.addPiece(blockEntity, last_segment);

        if (this.treasurePiecesDoneCollecting(last_segment)) {
            // treasure pieces done collecting
            LOGGER.info("Full treasure bastion pieces collected");
            setBastion(treasureCollector.toBastionData("TREASURE"));
        }
    }

    private boolean treasurePiecesDoneCollecting(String last_segment) {
        // relying on llama bastion practice's generation order
        // the practice map generates entrance last for treasure bastions
        return last_segment.contains("entrance_");
    }

    private void collectHousingPieces(StructureBlockBlockEntity blockEntity) {
        String structureName = blockEntity.getStructureName();

        if (structureName.contains("lower/lower_")) { // new housing bastion
            updateRoutesBasedOnBastion(BastionType.HOUSING);
            LOGGER.info("New bastion anchor pos: {}", blockEntity.getPos());
            this.housingCollector.clear();
            BlockRotation rotation = blockEntity.getRotation();
            this.housingCollector.setRotation(rotation);
            this.housingCollector.setBastionAnchor(blockEntity.getPos());
        }

        String[] parts = structureName.split("/");
        String last_segment = parts[parts.length - 1];
        this.housingCollector.addPiece(blockEntity, last_segment);

        if (this.housingPiecesDoneCollecting(last_segment)) {
            // housing pieces done collecting
            LOGGER.info("Full housing bastion pieces collected");
            setBastion(housingCollector.toBastionData("HOUSING"));
        }
    }

    private boolean housingPiecesDoneCollecting(String last_segment) {
        // relying on llama bastion practice's generation order
        // the practice map generates rampart_plate last for housing bastions
        return last_segment.contains("plate_");
    }

    private static @NotNull BastionType getBastionType(String location_tag) {
        if (location_tag.contains("bridge")) {
            return BastionType.BRIDGE;
        } else if (location_tag.contains("hoglin_stable")) {
            return BastionType.STABLES;
        } else if (location_tag.contains("units")) {
            return BastionType.HOUSING;
        } else if (location_tag.contains("treasure")) {
            return BastionType.TREASURE;
        } else {
            return BastionType.UNKNOWN;
        }
    }

    private static int distanceToPlayer(Vec3i bastionAnchor, BlockPos playerPos) {
        int dx = bastionAnchor.getX() - playerPos.getX();
        int dz = bastionAnchor.getZ() - playerPos.getZ();

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
