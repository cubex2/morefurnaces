package cubex2.mods.morefurnaces.proxies;


import net.minecraft.client.Minecraft;
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
        Item item = Item.getByNameOrId("morefurnaces:furnaceBlock");
        ModelBakery.registerItemVariants(item, new ResourceLocation("morefurnaces:furnace_iron"),
                                         new ResourceLocation("morefurnaces:furnace_gold"),
                                         new ResourceLocation("morefurnaces:furnace_diamond"),
                                         new ResourceLocation("morefurnaces:furnace_netherrack"),
                                         new ResourceLocation("morefurnaces:furnace_obsidian"));

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
