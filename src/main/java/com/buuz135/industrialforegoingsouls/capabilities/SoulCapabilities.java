package com.buuz135.industrialforegoingsouls.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SoulCapabilities {

    public static final Capability<ISoulHandler> BLOCK =
            CapabilityManager.get(new CapabilityToken<ISoulHandler>() {});
}