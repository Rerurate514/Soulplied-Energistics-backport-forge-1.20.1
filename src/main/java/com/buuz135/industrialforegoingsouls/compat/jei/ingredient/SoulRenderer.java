package com.buuz135.industrialforegoingsouls.compat.jei.ingredient;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class SoulRenderer implements IIngredientRenderer<SoulStack> {
    
    private final List<GuiParticle> particleList = new ArrayList<>();
    private long lastCheckedForParticle = 0;
    private final int x = 0;
    private final int y = 0;
    
    @Override
    public void render(GuiGraphics guiGraphics, SoulStack soulStack) {
        guiGraphics.pose().pushPose();
        var warden_rl = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden.png");
        var warden_hear = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_heart.png");
        guiGraphics.blit(warden_rl, x ,y , 12,14,16,16,128,128);

        guiGraphics.pose().pushPose();
        var heart_timing = 30f;
        heart_timing = 1 - ((Minecraft.getInstance().level.getGameTime() % heart_timing) / heart_timing);
        RenderSystem.setShaderColor(heart_timing, heart_timing, heart_timing, heart_timing);
        guiGraphics.blit(warden_hear, x - 1,y -1, 11,13,18,18,128,128);
        RenderSystem.setShaderColor(1,1,1, 1f);
        guiGraphics.pose().popPose();

        var rotation = Minecraft.getInstance().level.getGameTime() % 160 - 80;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x,y - 1, 100);
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(rotation));
        guiGraphics.blit(warden_rl, 0,0, 91,13,17,18,128,128);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 16,y + 17,100);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(180));
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(rotation));

        guiGraphics.blit(warden_rl, 0,0, 91,13,17,18,128,128);
        guiGraphics.pose().popPose();

        guiGraphics.pose().scale(0.75F, 0.75F, 0.75F);
        var fullAmount = 0.05;
        var xSize = 8;
        var ySize = 6;
        var currentTime = Minecraft.getInstance().level.getGameTime();
        if (this.lastCheckedForParticle != currentTime) {
            if (Minecraft.getInstance().level.random.nextDouble() <= fullAmount) {
                particleList.add(new GuiParticle(Minecraft.getInstance().level.random.nextInt(xSize), ySize  - Minecraft.getInstance().level.random.nextInt(3), currentTime));
            }
            this.lastCheckedForParticle = currentTime;
        }
        var ageTick = 3;
        if (currentTime % ageTick == 0) {
            particleList.removeIf(guiParticle -> ((currentTime - guiParticle.age) / ageTick) > 10);
        }
        guiGraphics.pose().translate(0,0,200);

        for (int i = particleList.size() - 1; i >= 0; i--) {
            GuiParticle guiParticle = particleList.get(i);
            double particleAge = (currentTime - guiParticle.age) / (double) ageTick;
            double extraY = ((ySize - 32) / 20.0D) * particleAge;

            int textureIndex = Math.max(0, Math.min(10, (int) particleAge));

            ResourceLocation texture = ResourceLocation.withDefaultNamespace("textures/particle/sculk_soul_" + textureIndex + ".png");

            int drawX = (int) ((x + guiParticle.x) * (1 / 0.75f));
            int drawY = (int) ((y + guiParticle.y + extraY) * (1 / 0.75f));


            guiGraphics.blit(texture, drawX, drawY, 0, 0, 16, 16, 16, 16);
        }
        guiGraphics.pose().popPose();
    }

    @Override
    public List<Component> getTooltip(SoulStack soulStack, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("soulkey.name"));
        tooltip.add(Component.translatable("soulkey.tooltip").append(Component.literal(String.valueOf(soulStack.getAmount()))));
        return tooltip;
    }

    private static class GuiParticle {
        private final int x;
        private final int y;
        private final long age;

        public GuiParticle(int x, int y, long age) {
            this.x = x;
            this.y = y;
            this.age = age;
        }
    }
}
