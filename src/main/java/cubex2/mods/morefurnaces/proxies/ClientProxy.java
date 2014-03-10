package cubex2.mods.morefurnaces.proxies;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.world.World;

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
