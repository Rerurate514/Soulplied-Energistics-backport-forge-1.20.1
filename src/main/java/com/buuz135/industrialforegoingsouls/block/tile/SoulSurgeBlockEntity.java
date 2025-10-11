package com.buuz135.industrialforegoingsouls.block.tile;

import com.buuz135.industrialforegoingsouls.block.SoulSurgeBlock;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.buuz135.industrialforegoingsouls.tag.SoulTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

public class SoulSurgeBlockEntity extends NetworkBlockEntity<SoulSurgeBlockEntity> {

    @Save
    private int tickingTime;
    
    // Cache to avoid repeated calculations
    private BlockPos cachedTargetPos;
    private int cacheUpdateCounter = 0;
    private static final int CACHE_UPDATE_INTERVAL = 100; // Update cache every 5 seconds

    public SoulSurgeBlockEntity(BasicTileBlock<SoulSurgeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }

    @NotNull
    @Override
    public SoulSurgeBlockEntity getSelf() {
        return this;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, SoulSurgeBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        
        // Early exit if not enabled
        if (!state.getValue(SoulSurgeBlock.ENABLED)) {
            return;
        }
        
        // Consume soul if needed
        if (tickingTime <= 0) {
            var network = getNetwork();
            if (network != null && network.getSoulAmount() > 0) {
                network.useSoul(this.level);
                tickingTime = ConfigSoulSurge.SOUL_TIME;
            }
        }
        
        // Process acceleration if we have ticking time
        if (tickingTime > 0) {
            // Update cached target position periodically
            if (cachedTargetPos == null || cacheUpdateCounter % CACHE_UPDATE_INTERVAL == 0) {
                cachedTargetPos = pos.relative(state.getValue(RotatableBlock.FACING_ALL).getOpposite());
            }
            cacheUpdateCounter++;
            
            var targetingState = level.getBlockState(cachedTargetPos);
            
            // Check if target is not air and can be accelerated
            if (!targetingState.is(Blocks.AIR) && 
                !targetingState.is(SoulTags.Blocks.CANT_ACCELERATE) && 
                !targetingState.is(SoulTags.Blocks.FORGE_CANT_ACCELERATE)) {
                
                BlockEntity targetingTile = level.getBlockEntity(cachedTargetPos);
                if (targetingTile != null) {
                    BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) targetingState.getTicker(this.level, targetingTile.getType());
                    if (ticker != null) {
                        for (int i = 0; i < ConfigSoulSurge.ACCELERATION_TICK; i++) {
                            ticker.tick(level, cachedTargetPos, targetingState, targetingTile);
                        }
                        --tickingTime;
                    }
                } else if (level instanceof ServerLevel serverLevel) {
                    if (serverLevel.random.nextDouble() < ConfigSoulSurge.RANDOM_TICK_ACCELERATION_CHANCE) {
                        for (int i = 0; i < ConfigSoulSurge.BLOCK_ACCELERATION_TICK; i++) {
                            targetingState.randomTick(serverLevel, cachedTargetPos, serverLevel.random);
                        }
                    }
                    --tickingTime;
                }
            } else if (targetingState.is(Blocks.AIR)) {
                // Accelerate entities in the target position
                var box = Shapes.box(0, 0, 0, 1, 1, 1).move(cachedTargetPos.getX(), cachedTargetPos.getY(), cachedTargetPos.getZ());
                var entities = level.getEntitiesOfClass(LivingEntity.class, box.bounds());
                for (LivingEntity entity : entities) {
                    if (!entity.getType().is(SoulTags.Entities.CANT_ACCELERATE)) {
                        for (int i = 0; i < ConfigSoulSurge.ENTITIES_ACCELERATION_TICK; i++) {
                            entity.tick();
                        }
                        --tickingTime;
                    }
                }
            }
        }
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        cachedTargetPos = null;
    }
}
