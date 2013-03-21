package cubex2.mods.morefurnaces;

import net.minecraft.block.Block;

public class TileEntityNetherrackFurnace extends TileEntityIronFurnace
{

	public TileEntityNetherrackFurnace()
	{
		super(FurnaceType.NETHERRACK);
	}

	@Override
	public boolean isBurning()
	{
		if (this.worldObj != null)
		{
			return worldObj.getBlockId(xCoord, yCoord + 1, zCoord) == Block.fire.blockID;
		}
		return false;
	}
}
