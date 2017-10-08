package cubex2.mods.morefurnaces.proxies;


import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.items.Upgrades;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderInformation()
    {
        Item item = Item.getItemFromBlock(MoreFurnaces.blockFurnaces);

        String[] suffixes = {"iron", "gold", "diamond", "obsidian", "netherrack"};
        for (int i = 0; i < suffixes.length; i++)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("morefurnaces:furnace_" + suffixes[i], "inventory"));
        }

        for (Upgrades upgrade : Upgrades.values())
        {
            ModelResourceLocation l = new ModelResourceLocation("morefurnaces:upgrade_" + upgrade.getUnlocalizedName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(MoreFurnaces.upgrade, upgrade.ordinal(), l);
        }
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().world;
    }
}
