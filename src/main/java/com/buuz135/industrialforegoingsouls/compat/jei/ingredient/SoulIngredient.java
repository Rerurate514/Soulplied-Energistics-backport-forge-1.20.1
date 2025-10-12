package com.buuz135.industrialforegoingsouls.compat.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import static com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls.MOD_ID;

public class SoulIngredient implements IIngredientHelper<SoulStack> {
    
    public static final IIngredientType<SoulStack> SOUL_STACK = () -> SoulStack.class;
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "soul");

    @Override
    public IIngredientType<SoulStack> getIngredientType() {
        return SOUL_STACK;
    }

    @Override
    public String getDisplayName(SoulStack ingredient) {
        return ingredient.getEntityType().getDescription().getString() + " Soul";
    }

    @Override
    public String getUniqueId(SoulStack ingredient, UidContext context) {
        return ingredient.getEntityType().toString();
    }

    @Override
    public ResourceLocation getResourceLocation(SoulStack ingredient) {
        return UID;
    }

    @Override
    public SoulStack copyIngredient(SoulStack ingredient) {
        return new SoulStack(ingredient.getEntityType(), ingredient.getAmount());
    }

    @Override
    public String getErrorInfo(@Nullable SoulStack ingredient) {
        if (ingredient == null) {
            return "null";
        }
        return ingredient.getEntityType().toString();
    }
}
