package com.buuz135.industrialforegoingsouls.soulplied_energistics;

import appeng.capabilities.Capabilities;
import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import com.buuz135.industrialforegoingsouls.block.tile.SoulLaserBaseBlockEntity;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilityProvider;
import com.buuz135.industrialforegoingsouls.soulplied_energistics.cap.InterfaceSoulCap;
import com.buuz135.industrialforegoingsouls.soulplied_energistics.client.ClientAppliedHelper;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls.SOUL_LASER_BLOCK;

public class SoulpliedEnergistics {
    public void initAppliedSouls(IEventBus modEventBus) {
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, EventPriority.LOWEST, this::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);

        modEventBus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                AppliedHelper.INSTANCE.runRegister();
            }
        });

        EventManager.forge(AttachCapabilitiesEvent.class).process(this::attachCapabilities);

        modEventBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            commonSetup(event);
        }));
        modEventBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            onClientSetup(event);
        }));
    }

    private void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        var blockEntity = event.getObject();

        if (blockEntity instanceof SoulLaserBaseBlockEntity soulBlock) {
            SoulCapabilityProvider provider = new SoulCapabilityProvider(soulBlock);
            event.addCapability(new ResourceLocation(IndustrialForegoingSouls.MOD_ID, "soul"), provider);
        } else if (blockEntity.getClass().getName().startsWith("appeng.")) {
            event.addCapability(new ResourceLocation(IndustrialForegoingSouls.MOD_ID, "ae2_soul"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                    if (capability == SoulCapabilities.BLOCK) {
                        var genericInv = blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side);
                        if (genericInv.isPresent()) {
                            return LazyOptional.of(() -> new InterfaceSoulCap(genericInv.orElse(null))).cast();
                        }
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

    private void onItemTooltip(ItemTooltipEvent event) {
        if (SOUL_LASER_BLOCK != null && event.getItemStack().getItem().equals(SOUL_LASER_BLOCK.getKey().get().asItem())) {
            event.getToolTip().add(Component.translatable("soulpliedenergistics.storage_bus")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AppliedHelper.INSTANCE.init();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ClientAppliedHelper.INSTANCE.init();
    }
}
