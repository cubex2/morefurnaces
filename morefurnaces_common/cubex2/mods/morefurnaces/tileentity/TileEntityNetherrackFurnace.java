package cubex2.mods.morefurnaces.tileentity;

import net.minecraft.block.Block;
import cubex2.mods.morefurnaces.FurnaceType;

public class TileEntityNetherrackFurnace extends TileEntityIronFurnace
{

    public TileEntityNetherrackFurnace()
    {
        super(FurnaceType.NETHERRACK);
    }

    @Override
    public boolean isBurning()
    {
        if (worldObj != null)
            return worldObj.getBlockId(xCoord, yCoord + 1, zCoord) == Block.fire.blockID;
        return false;
    }
}
