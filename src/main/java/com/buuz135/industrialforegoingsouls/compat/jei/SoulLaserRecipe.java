package com.buuz135.industrialforegoingsouls.compat.jei;

import com.buuz135.industrialforegoingsouls.compat.jei.ingredient.SoulStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class SoulLaserRecipe {
    private final EntityType<?> entityType;
    private final ItemStack lens;
    private final SoulStack output;

    public SoulLaserRecipe(EntityType<?> entityType, ItemStack lens, int soulAmount) {
        this.entityType = entityType;
        this.lens = lens;
        this.output = new SoulStack(entityType, soulAmount);
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public ItemStack getLens() {
        return lens;
    }

    public SoulStack getOutput() {
        return output;
    }
}