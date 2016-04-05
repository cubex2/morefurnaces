package cubex2.mods.morefurnaces;

import cubex2.mods.morefurnaces.blocks.BlockMoreFurnaces;
import cubex2.mods.morefurnaces.proxies.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION)
public class MoreFurnaces
{
    public static BlockMoreFurnaces blockFurnaces;

    @SidedProxy(clientSide = "cubex2.mods.morefurnaces.proxies.ClientProxy", serverSide = "cubex2.mods.morefurnaces.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(ModInformation.ID)
    public static MoreFurnaces instance;

    public static int ironSpeed;
    public static int goldSpeed;
    public static int diamondSpeed;
    public static int netherrackSpeed;
    public static int obsidianSpeed;

    public static float ironConsumptionRate;
    public static float goldConsumptionRate;
    public static float diamondConsumptionRate;
    public static float netherrackConsumptionRate;
    public static float obsidianConsumptionRate;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            config.load();
            ironSpeed = config.get("General", "ironFurnaceSpeed", FurnaceType.IRON.speed).getInt();
            goldSpeed = config.get("General", "goldFurnaceSpeed", FurnaceType.GOLD.speed).getInt();
            diamondSpeed = config.get("General", "diamondFurnaceSpeed", FurnaceType.DIAMOND.speed).getInt();
            netherrackSpeed = config.get("General", "netherrackFurnaceSpeed", FurnaceType.NETHERRACK.speed).getInt();
            obsidianSpeed = config.get("General", "obsidianFurnaceSpeed", FurnaceType.OBSIDIAN.speed).getInt();

            ironConsumptionRate = (float) config.get("General", "ironFurnaceConsumptionRate", FurnaceType.IRON.consumptionRate).getDouble(FurnaceType.IRON.consumptionRate);
            goldConsumptionRate = (float) config.get("General", "goldFurnaceConsumptionRate", FurnaceType.GOLD.consumptionRate).getDouble(FurnaceType.GOLD.consumptionRate);
            diamondConsumptionRate = (float) config.get("General", "diamondFurnaceConsumptionRate", FurnaceType.DIAMOND.consumptionRate).getDouble(FurnaceType.DIAMOND.consumptionRate);
            netherrackConsumptionRate = (float) config.get("General", "netherrackFurnaceConsumptionRate", FurnaceType.NETHERRACK.consumptionRate).getDouble(FurnaceType.NETHERRACK.consumptionRate);
            obsidianConsumptionRate = (float) config.get("General", "obsidianFurnaceConsumptionRate", FurnaceType.OBSIDIAN.consumptionRate).getDouble(FurnaceType.OBSIDIAN.consumptionRate);
        } finally
        {
            config.save();
        }
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent evt)
    {
        blockFurnaces = new BlockMoreFurnaces();

        for (FurnaceType typ : FurnaceType.values())
        {
            GameRegistry.registerTileEntity(typ.clazz, "CubeX2 " + typ.friendlyName);
        }
        FurnaceType.generateRecipes(blockFurnaces);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.registerRenderInformation();
    }
}
