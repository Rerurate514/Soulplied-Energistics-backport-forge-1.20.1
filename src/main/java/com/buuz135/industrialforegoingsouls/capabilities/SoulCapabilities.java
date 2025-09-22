package com.buuz135.industrialforegoingsouls.capabilities;

import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SoulCapabilities {

    public static final Capability<ISoulHandler> BLOCK =
            CapabilityManager.get(new CapabilityToken<ISoulHandler>() {});

    public static final ResourceLocation SOUL_CAPABILITY_NAME =
            new ResourceLocation(IndustrialForegoingSouls.MOD_ID, "soul");

}