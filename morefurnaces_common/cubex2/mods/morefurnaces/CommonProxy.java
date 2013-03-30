package cubex2.mods.morefurnaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cubex2.mods.morefurnaces.client.GuiMoreFurnace;

public class CommonProxy implements IGuiHandler
{

	public void registerRenderInformation()
	{

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityIronFurnace)
		{
			return GuiMoreFurnace.GUI.buildGui(FurnaceType.values()[ID], player.inventory, (TileEntityIronFurnace) te);
		}
		else
		{
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityIronFurnace)
		{
			TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
			return new ContainerIronFurnace(player.inventory, furnace, furnace.getType());
		}
		else
		{
			return null;
		}
	}

	public World getClientWorld()
	{
		return null;
	}

}
