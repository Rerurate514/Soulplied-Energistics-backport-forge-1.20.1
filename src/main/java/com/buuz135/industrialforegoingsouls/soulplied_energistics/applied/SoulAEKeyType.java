package com.buuz135.industrialforegoingsouls.soulplied_energistics.applied;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SoulAEKeyType extends AEKeyType {

    public static final SoulAEKeyType TYPE = new SoulAEKeyType();

    public SoulAEKeyType() {
        super(ResourceLocation.fromNamespaceAndPath(IndustrialForegoingSouls.MOD_ID, "soul"), SoulKey.class, Component.translatable("soulkey.description"));
    }

    @Override
    public int getAmountPerByte() {
        return 1;
    }

    @Override
    public @Nullable AEKey readFromPacket(FriendlyByteBuf input) {
        return SoulKey.INSTANCE;
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(CompoundTag tag) {
        try {
            if (tag.contains("soul_type")) {
                return SoulKey.INSTANCE;
            }
            return SoulKey.INSTANCE;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public @Nullable String getUnitSymbol() {
        return "s";
    }
}
