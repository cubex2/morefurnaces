package cubex2.mods.morefurnaces.proxies;


import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.items.Upgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderInformation()
    {
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        Item item = Item.getByNameOrId("morefurnaces:furnaceBlock");
        ModelBakery.registerItemVariants(item, new ResourceLocation("morefurnaces:furnace_iron"),
                                         new ResourceLocation("morefurnaces:furnace_gold"),
                                         new ResourceLocation("morefurnaces:furnace_diamond"),
                                         new ResourceLocation("morefurnaces:furnace_netherrack"),
                                         new ResourceLocation("morefurnaces:furnace_obsidian"));

        ModelResourceLocation l = new ModelResourceLocation("morefurnaces:furnace_iron", "inventory");
        mesher.register(item, 0, l);

        l = new ModelResourceLocation("morefurnaces:furnace_gold", "inventory");
        mesher.register(item, 1, l);

        l = new ModelResourceLocation("morefurnaces:furnace_diamond", "inventory");
        mesher.register(item, 2, l);

        l = new ModelResourceLocation("morefurnaces:furnace_obsidian", "inventory");
        mesher.register(item, 3, l);

        l = new ModelResourceLocation("morefurnaces:furnace_netherrack", "inventory");
        mesher.register(item, 4, l);

        for (Upgrades upgrade : Upgrades.values())
        {
            ModelBakery.registerItemVariants(MoreFurnaces.upgrade, new ResourceLocation("morefurnaces", "upgrade_" + upgrade.getUnlocalizedName()));
            l = new ModelResourceLocation("morefurnaces:upgrade_" + upgrade.getUnlocalizedName(), "inventory");
            mesher.register(MoreFurnaces.upgrade, upgrade.ordinal(), l);
        }
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
