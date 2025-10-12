package com.buuz135.industrialforegoingsouls.compat.jei;

import com.buuz135.industrialforegoingsouls.compat.jei.ingredient.SoulIngredient;
import com.buuz135.industrialforegoingsouls.compat.jei.ingredient.SoulRenderer;
import com.buuz135.industrialforegoingsouls.compat.jei.ingredient.SoulStack;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulLaserBase;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.buuz135.industrial.module.ModuleCore.LASER_LENS;
import static com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls.MOD_ID;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.tryParse(MOD_ID);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        // Register the custom Soul ingredient type
        registration.register(
            SoulIngredient.SOUL_STACK,
            List.of(new SoulStack(EntityType.WARDEN, ConfigSoulLaserBase.SOULS_PER_OPERATION)),
            new SoulIngredient(),
            new SoulRenderer()
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeManager recipeManager = jeiRuntime.getRecipeManager();
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        // Register the Soul Laser category
        registration.addRecipeCategories(new SoulLaserCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Create recipes for the Soul Laser Base
        List<SoulLaserRecipe> recipes = new ArrayList<>();
        
        // Basic recipe: Warden -> Soul (without lens)
        recipes.add(new SoulLaserRecipe(
            EntityType.WARDEN,
            new ItemStack(LASER_LENS[DyeColor.BLUE.getId()].get()),
            ConfigSoulLaserBase.SOULS_PER_OPERATION
        ));
        
        // Register the recipes
        registration.addRecipes(SoulLaserCategory.RECIPE_TYPE, recipes);
    }
}
