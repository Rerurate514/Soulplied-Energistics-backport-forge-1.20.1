package com.buuz135.industrialforegoingsouls.capabilities;

import com.buuz135.industrialforegoingsouls.block.tile.SoulLaserBaseBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class SoulCapabilityProvider implements ICapabilityProvider {
    private final SoulLaserBaseBlockEntity blockEntity;
    private LazyOptional<SLBSoulCap> capability = LazyOptional.empty();

    public SoulCapabilityProvider(SoulLaserBaseBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == SoulCapabilities.BLOCK) {
            if (side == Direction.UP) {
                if (!capability.isPresent()) {
                    capability = LazyOptional.of(() -> new SLBSoulCap(blockEntity));
                }
                return capability.cast();
            }
        }
        return LazyOptional.empty();
    }

    public void invalidate() {
        capability.invalidate();
    }
}