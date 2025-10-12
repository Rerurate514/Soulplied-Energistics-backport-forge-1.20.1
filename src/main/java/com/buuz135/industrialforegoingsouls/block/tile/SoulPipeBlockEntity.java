package com.buuz135.industrialforegoingsouls.block.tile;

import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class SoulPipeBlockEntity extends NetworkBlockEntity<SoulPipeBlockEntity> {

    @Save
    private boolean wip;
    
    // Cache for adjacent block entities to avoid repeated lookups
    private final Map<Direction, BlockEntity> adjacentCache = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<ISoulHandler>> capabilityCache = new EnumMap<>(Direction.class);
    private int tickCounter = 0;
    private static final int CACHE_REFRESH_INTERVAL = 20; // Refresh cache every second (20 ticks)
    private static final int TRANSFER_RATE = 4; // Souls transferred per operation

    public SoulPipeBlockEntity(BasicTileBlock<SoulPipeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.wip = false;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state, SoulPipeBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        
        // Refresh cache periodically or on first tick
        if (tickCounter % CACHE_REFRESH_INTERVAL == 0) {
            refreshAdjacentCache(level, pos);
        }
        tickCounter++;
        
        // Get network once per tick instead of per direction
        var network = getNetwork();
        if (network == null) {
            return; // Early exit if no network
        }
        
        // Only process if network has capacity
        int maxSouls = network.getMaxSouls();
        int currentSouls = network.getSoulAmount();
        if (currentSouls >= maxSouls) {
            return; // Network is full, skip processing
        }
        
        // Process cached capabilities
        for (Map.Entry<Direction, LazyOptional<ISoulHandler>> entry : capabilityCache.entrySet()) {
            entry.getValue().ifPresent(handler -> {
                var simulated = handler.drain(TRANSFER_RATE, ISoulHandler.Action.SIMULATE);
                if (simulated > 0) {
                    int added = network.addSouls(this.level, simulated);
                    if (added > 0) {
                        handler.drain(added, ISoulHandler.Action.EXECUTE);
                    }
                }
            });
            
            // Early exit if network becomes full
            if (network.getSoulAmount() >= network.getMaxSouls()) {
                break;
            }
        }
    }
    
    /**
     * Refreshes the cache of adjacent block entities and their capabilities
     */
    private void refreshAdjacentCache(Level level, BlockPos pos) {
        // Clear old capabilities
        capabilityCache.clear();
        adjacentCache.clear();
        
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockEntity adjacentEntity = level.getExistingBlockEntity(adjacentPos);
            
            if (adjacentEntity != null) {
                adjacentCache.put(direction, adjacentEntity);
                LazyOptional<ISoulHandler> capability = adjacentEntity.getCapability(
                    SoulCapabilities.BLOCK, 
                    direction.getOpposite()
                );
                
                if (capability.isPresent()) {
                    capabilityCache.put(direction, capability);
                }
            }
        }
    }

    @NotNull
    @Override
    public SoulPipeBlockEntity getSelf() {
        return this;
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        capabilityCache.clear();
        adjacentCache.clear();
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        capabilityCache.clear();
        adjacentCache.clear();
    }

}
