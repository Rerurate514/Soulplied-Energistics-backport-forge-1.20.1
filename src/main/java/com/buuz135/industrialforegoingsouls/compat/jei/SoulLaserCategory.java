package com.buuz135.industrialforegoingsouls.compat.jei;

import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import com.buuz135.industrialforegoingsouls.compat.jei.ingredient.SoulIngredient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class SoulLaserCategory implements IRecipeCategory<SoulLaserRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(IndustrialForegoingSouls.MOD_ID, "soul_laser");
    public static final RecipeType<SoulLaserRecipe> RECIPE_TYPE = new RecipeType<>(UID, SoulLaserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SoulLaserCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(IndustrialForegoingSouls.SOUL_LASER_BLOCK.getKey().get()));
    }

    @Override
    public @NotNull RecipeType<SoulLaserRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.industrialforegoingsouls.soul_laser");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SoulLaserRecipe recipe, @NotNull IFocusGroup focuses) {
        // Add Soul Laser Base as hidden input
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
            .addItemStack(new ItemStack(IndustrialForegoingSouls.SOUL_LASER_BLOCK.getKey().get()));
        
        // Lens input
        if (!recipe.getLens().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 40, 22)
                .addItemStack(recipe.getLens());
        }

        // Soul output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 22)
            .addIngredient(SoulIngredient.SOUL_STACK, recipe.getOutput());
    }

    @Override
    public void draw(@NotNull SoulLaserRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Render the entity model
        renderEntity(guiGraphics, 30, 45, recipe.getEntityType());

    }
    
    private void drawArrow(GuiGraphics guiGraphics, int x, int y) {
        // Draw arrow shaft (horizontal line)
        guiGraphics.fill(x, y + 3, x + 44, y + 5, 0xFF8B8B8B);
        
        // Draw arrow head (triangle pointing right)
        guiGraphics.fill(x + 44, y, x + 45, y + 1, 0xFF8B8B8B);
        guiGraphics.fill(x + 45, y + 1, x + 46, y + 2, 0xFF8B8B8B);
        guiGraphics.fill(x + 46, y + 2, x + 47, y + 3, 0xFF8B8B8B);
        guiGraphics.fill(x + 47, y + 3, x + 48, y + 4, 0xFF8B8B8B);
        guiGraphics.fill(x + 48, y + 4, x + 49, y + 5, 0xFF8B8B8B);
        guiGraphics.fill(x + 49, y + 5, x + 50, y + 6, 0xFF8B8B8B);
        guiGraphics.fill(x + 50, y + 6, x + 51, y + 7, 0xFF8B8B8B);
        guiGraphics.fill(x + 51, y + 7, x + 52, y + 8, 0xFF8B8B8B);
        
        guiGraphics.fill(x + 51, y + 8, x + 52, y + 9, 0xFF8B8B8B);
        guiGraphics.fill(x + 50, y + 9, x + 51, y + 10, 0xFF8B8B8B);
        guiGraphics.fill(x + 49, y + 10, x + 50, y + 11, 0xFF8B8B8B);
        guiGraphics.fill(x + 48, y + 11, x + 49, y + 12, 0xFF8B8B8B);
        guiGraphics.fill(x + 47, y + 12, x + 48, y + 13, 0xFF8B8B8B);
        guiGraphics.fill(x + 46, y + 13, x + 47, y + 14, 0xFF8B8B8B);
        guiGraphics.fill(x + 45, y + 14, x + 46, y + 15, 0xFF8B8B8B);
        guiGraphics.fill(x + 44, y + 15, x + 45, y + 16, 0xFF8B8B8B);
    }

    private void renderEntity(GuiGraphics guiGraphics, int x, int y, EntityType<?> entityType) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        try {
            // Create entity instance
            Entity entity = entityType.create(minecraft.level);
            if (entity == null) return;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            
            // Position and scale
            poseStack.translate(x, y, 50.0);
            
            // Calculate scale based on entity size
            float scale = 20.0f / Math.max(entity.getBbHeight(), entity.getBbWidth());
            scale = Math.min(scale, 20.0f); // Cap the scale
            
            poseStack.scale(scale, scale, scale);
            
            // Rotate for better view
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0f));
            
            // Animate rotation
            float rotation = (System.currentTimeMillis() % 3600) / 20.0f;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            // Setup rendering
            EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
            entityRenderDispatcher.setRenderShadow(false);
            
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            
            // Render entity
            entityRenderDispatcher.render(entity, 0, 0, 0, 0.0f, 1.0f, poseStack, bufferSource, 15728880);
            bufferSource.endBatch();
            
            entityRenderDispatcher.setRenderShadow(true);
            
            poseStack.popPose();
        } catch (Exception e) {
            // Silently fail if entity can't be rendered
        }
    }
}