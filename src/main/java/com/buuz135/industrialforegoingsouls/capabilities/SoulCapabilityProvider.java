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
        System.out.println("=== SoulCapabilityProvider.getCapability ===");
        System.out.println("Requested capability: " + cap.getName());
        System.out.println("SoulCapabilities.BLOCK: " + SoulCapabilities.BLOCK.getName());
        System.out.println("Side: " + side);
        System.out.println("BlockEntity: " + blockEntity);

        if (cap == SoulCapabilities.BLOCK) {
            System.out.println("Capability match found!");
            if (side == Direction.UP) {
                System.out.println("Side is UP, providing capability");
                if (!capability.isPresent()) {
                    capability = LazyOptional.of(() -> new SLBSoulCap(blockEntity));
                    System.out.println("Created new SLBSoulCap");
                }
                return capability.cast();
            } else {
                System.out.println("Side is not UP, denying capability");
            }
        } else {
            System.out.println("Capability does not match");
        }
        return LazyOptional.empty();
    }

    public void invalidate() {
        capability.invalidate();
    }
}