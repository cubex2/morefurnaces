package cubex2.mods.morefurnaces.client;

import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cubex2.mods.morefurnaces.CommonProxy;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderInformation()
    {
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
