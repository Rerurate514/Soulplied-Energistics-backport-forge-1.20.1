package com.buuz135.industrialforegoingsouls.soulplied_energistics.applied;


import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class SoulKey extends AEKey {

    public static ResourceLocation RL = ResourceLocation.fromNamespaceAndPath(IndustrialForegoingSouls.MOD_ID, "soulkey");
    public static SoulKey INSTANCE = new SoulKey();

    public static final MapCodec<SoulKey> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(Codec.STRING.fieldOf("soul_type").forGetter(o -> ""))
                    .apply(builder, t1 -> new SoulKey())
    );
    public static final Codec<SoulKey> CODEC = MAP_CODEC.codec();

    private SoulKey(){

    }

    @Override
    public AEKeyType getType() {
        return SoulAEKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(true, null);
    }

    @Override
    public Object getPrimaryKey() {
        return this;
    }

    @Override
    public ResourceLocation getId() {
        return RL;
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
        data.writeUtf("soul_type");
    }

    @Override
    public String getModId() {
        return IndustrialForegoingSouls.MOD_ID;
    }

    @Override
    protected Component computeDisplayName() {
        return Component.translatable("soulkey.name");
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        // NO-OP
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SoulKey;
    }
}
