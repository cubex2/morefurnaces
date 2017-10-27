package cubex2.mods.morefurnaces;

import cubex2.mods.morefurnaces.blocks.BlockMoreFurnaces;
import cubex2.mods.morefurnaces.items.ItemMoreFurnaces;
import cubex2.mods.morefurnaces.items.ItemUpgrade;
import cubex2.mods.morefurnaces.proxies.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION,
        acceptedMinecraftVersions = "[1.12,)",
        dependencies = "required-after:cxlibrary@[1.5.0,)")
public class MoreFurnaces
{
    public static BlockMoreFurnaces blockFurnaces;
    private static ItemMoreFurnaces itemBlock;
    public static ItemUpgrade upgrade;

    @SidedProxy(clientSide = "cubex2.mods.morefurnaces.proxies.ClientProxy", serverSide = "cubex2.mods.morefurnaces.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(ModInformation.ID)
    public static MoreFurnaces instance;

    public MoreFurnaces()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Config.init(event.getSuggestedConfigurationFile());

        blockFurnaces = new BlockMoreFurnaces();
        itemBlock = (ItemMoreFurnaces) new ItemMoreFurnaces(blockFurnaces).setRegistryName(blockFurnaces.getRegistryName());
        upgrade = new ItemUpgrade();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockFurnaces);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(upgrade, itemBlock);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        proxy.registerRenderInformation();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent evt)
    {
        for (FurnaceType typ : FurnaceType.values())
        {
            GameRegistry.registerTileEntity(typ.clazz, "CubeX2 " + typ.friendlyName);
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
    }
}
