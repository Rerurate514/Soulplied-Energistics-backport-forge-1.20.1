package com.buuz135.industrialforegoingsouls.soulplied_energistics.applied.strategies;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.buuz135.industrialforegoingsouls.soulplied_energistics.applied.SoulKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class SoulExternalStorageStrategy implements ExternalStorageStrategy {

    private ServerLevel level;
    private BlockPos fromPos;
    private Direction fromSide;

    public SoulExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
        this.fromSide = fromSide;
    }

//    @Override
//    public @Nullable MEStorage createWrapper(boolean b, Runnable runnable) {
//        var cap = this.level.getCapability(SoulCapabilities.BLOCK, fromSide);
//
//        if (cap.isPresent()) {
//            ISoulHandler handler = cap.orElse(null);
//            return new SoulLaserDrillMEStorage(handler);
//        }
//
//        return null;
//    }

    @Override
    public @Nullable MEStorage createWrapper(boolean extractOnly, Runnable onContentsChanged) {
        System.out.println("=== createWrapper DEBUG ===");
        System.out.println("Position: " + fromPos);
        System.out.println("Side: " + fromSide);
        System.out.println("ExtractOnly: " + extractOnly);

        var blockEntity = this.level.getBlockEntity(fromPos);
        System.out.println("BlockEntity: " + blockEntity);
        System.out.println("BlockEntity class: " + (blockEntity != null ? blockEntity.getClass().getSimpleName() : "null"));

        if (blockEntity == null) {
            System.out.println("BlockEntity is null!");
            return null;
        }

        System.out.println("Requesting capability: " + SoulCapabilities.BLOCK.getName());
        var cap = blockEntity.getCapability(SoulCapabilities.BLOCK, fromSide);
        System.out.println("Capability present: " + cap.isPresent());

        if (cap.isPresent()) {
            ISoulHandler handler = cap.orElse(null);
            System.out.println("SoulHandler: " + handler);
            return new SoulLaserDrillMEStorage(handler);
        } else {
            System.out.println("=== CAPABILITY NOT FOUND ===");
            System.out.println("Available capabilities on blockEntity:");
            return null;
        }
    }

    private record SoulLaserDrillMEStorage(ISoulHandler baseBlock) implements MEStorage {

        @Override
        public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
            return MEStorage.super.isPreferredStorageFor(what, source);
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            return baseBlock.fill((int) amount, mode.isSimulate() ? ISoulHandler.Action.SIMULATE : ISoulHandler.Action.EXECUTE);
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            return baseBlock.drain((int) amount, mode.isSimulate() ? ISoulHandler.Action.SIMULATE : ISoulHandler.Action.EXECUTE);
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            for (int i = 0; i < baseBlock.getSoulTanks(); i++) {
                out.add(SoulKey.INSTANCE, baseBlock.getSoulInTank(i));
            }
        }

        @Override
        public Component getDescription() {
            return Component.translatable("soulstorage.description");
        }

        @Override
        public KeyCounter getAvailableStacks() {
            return MEStorage.super.getAvailableStacks();
        }
    }
}
