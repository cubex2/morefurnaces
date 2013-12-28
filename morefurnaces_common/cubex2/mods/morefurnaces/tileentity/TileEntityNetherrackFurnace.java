package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
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
        if (worldObj != null)
            return worldObj.getBlockId(xCoord, yCoord + 1, zCoord) == Block.fire.blockID;
        return false;
    }
}
