package cubex2.mods.morefurnaces.client;

import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cubex2.mods.morefurnaces.CommonProxy;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture("/cubex2/mods/morefurnaces/client/textures/textures.png");
	}
	
	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
