package cubex2.mods.morefurnaces.proxies;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderInformation()
    {
        Item item = GameRegistry.findItem("morefurnaces", "furnaceBlock");
        ModelBakery.addVariantName(item, "morefurnaces:furnace_iron", "morefurnaces:furnace_gold", "morefurnaces:furnace_diamond", "morefurnaces:furnace_netherrack", "morefurnaces:furnace_obsidian");

        ModelResourceLocation l = new ModelResourceLocation("morefurnaces:furnace_iron", "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, l);

        l = new ModelResourceLocation("morefurnaces:furnace_gold", "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 1, l);

        l = new ModelResourceLocation("morefurnaces:furnace_diamond", "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 2, l);

        l = new ModelResourceLocation("morefurnaces:furnace_obsidian", "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 3, l);

        l = new ModelResourceLocation("morefurnaces:furnace_netherrack", "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 4, l);
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
