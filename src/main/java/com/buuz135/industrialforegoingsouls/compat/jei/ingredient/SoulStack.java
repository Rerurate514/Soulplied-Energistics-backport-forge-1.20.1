package com.buuz135.industrialforegoingsouls.compat.jei.ingredient;

import net.minecraft.world.entity.EntityType;

public class SoulStack {
    private final EntityType<?> entityType;
    private final int amount;

    public SoulStack(EntityType<?> entityType, int amount) {
        this.entityType = entityType;
        this.amount = amount;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public int getAmount() {
        return amount;
    }
}
