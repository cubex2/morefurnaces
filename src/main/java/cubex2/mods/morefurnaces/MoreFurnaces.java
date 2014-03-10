package cubex2.mods.morefurnaces;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cubex2.mods.morefurnaces.blocks.BlockMoreFurnaces;
import cubex2.mods.morefurnaces.items.ItemMoreFurnaces;
import cubex2.mods.morefurnaces.proxies.CommonProxy;

import java.io.IOException;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION)
public class MoreFurnaces
{
    public static BlockMoreFurnaces blockFurnaces;

    @SidedProxy(clientSide = "cubex2.mods.morefurnaces.proxies.ClientProxy", serverSide = "cubex2.mods.morefurnaces.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Instance(ModInformation.ID)
    public static MoreFurnaces instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException
    {
        blockFurnaces = new BlockMoreFurnaces();
        GameRegistry.registerBlock(blockFurnaces, ItemMoreFurnaces.class, "furnaceBlock");
    }

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {
        for (FurnaceType typ : FurnaceType.values())
        {
            GameRegistry.registerTileEntity(typ.clazz, "CubeX2 " + typ.friendlyName);
        }
        FurnaceType.generateRecipes(blockFurnaces);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.registerRenderInformation();
    }
}
