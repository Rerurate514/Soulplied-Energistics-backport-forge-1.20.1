package com.buuz135.industrialforegoingsouls.block.tile;

import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulPipeBlockEntity extends NetworkBlockEntity<SoulPipeBlockEntity> {

    @Save
    private boolean wip;

    public SoulPipeBlockEntity(BasicTileBlock<SoulPipeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.wip = false;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state, SoulPipeBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        for (Direction value : Direction.values()) {
            BlockPos adjacentPos = pos.relative(value);

            var adjacentEntity = level.getBlockEntity(adjacentPos);

            if (adjacentEntity != null) {
                var capability = adjacentEntity.getCapability(SoulCapabilities.BLOCK, value.getOpposite());
                var network = getNetwork();

                capability.ifPresent(handler -> {
                    if (network != null) {
                        var simulated = handler.drain(4, ISoulHandler.Action.SIMULATE);
                        handler.drain(network.addSouls(this.level, simulated), ISoulHandler.Action.EXECUTE);
                    }
                });
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
    }

}
