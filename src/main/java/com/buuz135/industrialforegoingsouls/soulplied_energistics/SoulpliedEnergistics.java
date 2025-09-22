//package com.buuz135.industrialforegoingsouls.soulplied_energistics;
//
//import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
//import com.buuz135.soulplied_energistics.cap.InterfaceSoulCap;
//import com.buuz135.soulplied_energistics.client.ClientAppliedHelper;
//import com.hrznstudio.titanium.event.handler.EventManager;
//import com.mojang.logging.LogUtils;
//import net.minecraft.ChatFormatting;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.network.chat.Component;;
//import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
//import net.minecraftforge.eventbus.api.EventPriority;
//import net.minecraftforge.eventbus.api.IEventBus;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.event.entity.player.ItemTooltipEvent;
//import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.registries.RegisterEvent;
//import org.slf4j.Logger;
//
//// The value here should match an entry in the META-INF/mods.toml file
//@Mod(SoulpliedEnergistics.MODID)
//public class SoulpliedEnergistics {
//
//    /**
//     * Clean Code
//     * Localization
//     * Add tooltips
//     * Change interface filtering to use souls items
//     */
//
//    public static final String MODID = "soulplied_energistics";
//    private static final Logger LOGGER = LogUtils.getLogger();
//
//    public SoulpliedEnergistics() {
//        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//
//        modEventBus.addListener(this::commonSetup);
//        modEventBus.addListener(this::onClientSetup);
//
//        modEventBus.addListener((RegisterEvent e) -> {
//            if (e.getRegistryKey().equals(Registries.BLOCK)) {
//                AppliedHelper.INSTANCE.runRegister();
//            }
//        });
//
////        EventManager.mod(RegisterCapabilitiesEvent.class, EventPriority.LOWEST).process(event -> {
////            for (var block : BuiltInRegistries.BLOCK) {
////                if (event.isBlockRegistered(AECapabilities.GENERIC_INTERNAL_INV, block)) {
////                    event.registerBlock(SoulCapabilities.BLOCK, (level, pos, state, tile, side) -> {
////                                var genericInv = level.getCapability(AECapabilities.GENERIC_INTERNAL_INV, pos, state, tile, side);
////                                if (genericInv != null) {
////                                    return new InterfaceSoulCap(genericInv);
////                                }
////                                return null;
////                            },
////                            block
////                    );
////                }
////            }
////        }).subscribe();
////
////        EventManager.forge(ItemTooltipEvent.class).process(event -> {
////            if (BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).getNamespace().equals("industrialforegoingsouls")){
////                event.getToolTip().add(Component.translatable("soulpliedenergistics.can_be_used_filter").withStyle(ChatFormatting.GRAY));
////            }
////            if (event.getItemStack().getItem().equals(IndustrialForegoingSouls.SOUL_LASER_BLOCK.asItem())){
////                event.getToolTip().add(Component.translatable("soulpliedenergistics.storage_bus").withStyle(ChatFormatting.GRAY));
////
////            }
////        }).subscribe();
//
//    }
//
//    private void commonSetup(final FMLCommonSetupEvent event) {
//        AppliedHelper.INSTANCE.init();
//    }
//
//    private void onClientSetup(final FMLClientSetupEvent event) {
//        ClientAppliedHelper.INSTANCE.init();
//    }
//}