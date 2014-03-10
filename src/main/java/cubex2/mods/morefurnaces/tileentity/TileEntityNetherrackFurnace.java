package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

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
            return worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.fire;
        return false;
    }
}
