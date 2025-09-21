package com.buuz135.industrialforegoingsouls;

import com.buuz135.industrialforegoingsouls.block.SoulLaserBaseBlock;
import com.buuz135.industrialforegoingsouls.block.SoulPipeBlock;
import com.buuz135.industrialforegoingsouls.block.SoulSurgeBlock;
import com.buuz135.industrialforegoingsouls.block.tile.SoulLaserBaseBlockEntity;
import com.buuz135.industrialforegoingsouls.block_network.DefaultSoulNetworkElement;
import com.buuz135.industrialforegoingsouls.block_network.SoulNetwork;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilityProvider;
import com.buuz135.industrialforegoingsouls.data.*;
import com.buuz135.industrialforegoingsouls.soulplied_energistics.AppliedHelper;
import com.buuz135.industrialforegoingsouls.soulplied_energistics.cap.InterfaceSoulCap;
import com.buuz135.industrialforegoingsouls.soulplied_energistics.client.ClientAppliedHelper;
import com.hrznstudio.titanium.block_network.NetworkRegistry;
import com.hrznstudio.titanium.block_network.element.NetworkElementRegistry;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.datagenerator.model.BlockItemModelGeneratorProvider;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;
import appeng.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IndustrialForegoingSouls.MOD_ID)
public class IndustrialForegoingSouls extends ModuleController {

    public final static String MOD_ID = "industrialforegoingsouls";
    public static NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    public static TitaniumTab TAB = new TitaniumTab(new ResourceLocation(MOD_ID, "main"));

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SOUL_LASER_BLOCK = null;
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SOUL_PIPE_BLOCK = null;
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> SOUL_SURGE_BLOCK = null;

    public IndustrialForegoingSouls() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onClient);
        NetworkRegistry.INSTANCE.addFactory(SoulNetwork.SOUL_NETWORK, new SoulNetwork.Factory());
        NetworkElementRegistry.INSTANCE.addFactory(DefaultSoulNetworkElement.ID, new DefaultSoulNetworkElement.Factory());

        EventManager.mod(RegisterCapabilitiesEvent.class).process(event -> {
            event.register(SoulCapabilities.BLOCK.getClass());
        });

        modEventBus.addListener(this::registerCapabilities);

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);

    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(SoulCapabilities.BLOCK.getClass());
        System.out.println("Soul capabilities registered for " + MOD_ID);
    }

    @Override
    protected void initModules() {
        this.addCreativeTab("main", () -> new ItemStack(SOUL_LASER_BLOCK.getKey().get()), MOD_ID, TAB);
        SOUL_LASER_BLOCK = this.getRegistries().registerBlockWithTile("soul_laser_base", SoulLaserBaseBlock::new, TAB);
        SOUL_PIPE_BLOCK = this.getRegistries().registerBlockWithTile("soul_network_pipe", SoulPipeBlock::new, TAB);
        SOUL_SURGE_BLOCK = this.getRegistries().registerBlockWithTile("soul_surge", SoulSurgeBlock::new, TAB);
    }

    @OnlyIn(Dist.CLIENT)
    public void onClient() {

    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        NonNullLazy<List<Block>> blocksToProcess = NonNullLazy.of(() ->
                ForgeRegistries.BLOCKS.getValues()
                        .stream()
                        .filter(basicBlock -> Optional.ofNullable(ForgeRegistries.BLOCKS.getKey(basicBlock))
                                .map(ResourceLocation::getNamespace)
                                .filter(MOD_ID::equalsIgnoreCase)
                                .isPresent())
                        .collect(Collectors.toList())
        );
        event.getGenerator().addProvider(true, new IFSoulsLangProvider(event.getGenerator().getPackOutput(), MOD_ID, "en_us"));
        event.getGenerator().addProvider(true, new BlockItemModelGeneratorProvider(event.getGenerator(), MOD_ID, blocksToProcess));
        event.getGenerator().addProvider(true, new TitaniumLootTableProvider(event.getGenerator(), blocksToProcess));
        event.getGenerator().addProvider(true, new IFSoulsRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new IFSoulsSerializableRecipe(event.getGenerator(), MOD_ID));
        event.getGenerator().addProvider(true, new IFSoulsTagProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new IFSoulsBlockstateProvider(event.getGenerator().getPackOutput(), MOD_ID, event.getExistingFileHelper()));
    }

    public void initAppliedSouls(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);

        modEventBus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                AppliedHelper.INSTANCE.runRegister();
            }
        });

        EventManager.mod(RegisterCapabilitiesEvent.class).process(event -> {
            event.register(SoulCapabilities.BLOCK.getClass());
        });

        EventManager.forge(AttachCapabilitiesEvent.class).process(this::attachCapabilities);
    }

    private void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        var blockEntity = event.getObject();

        var genericInvCap = blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, null);
        if (genericInvCap.isPresent()) {
            event.addCapability(new ResourceLocation(MOD_ID, "soul_capability"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                    if (capability == SoulCapabilities.BLOCK) {
                        if (side == Direction.UP) {
                            return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                                    .lazyMap(InterfaceSoulCap::new)
                                    .cast();
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
