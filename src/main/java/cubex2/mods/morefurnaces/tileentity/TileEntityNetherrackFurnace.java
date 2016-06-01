package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class TileEntityNetherrackFurnace extends TileEntityIronFurnace
{

    public TileEntityNetherrackFurnace()
    {
        super(FurnaceType.NETHERRACK);
    }

    @Override
    public int getSpeed()
    {
        return MoreFurnaces.netherrackSpeed;
    }

    @Override
    public float getConsumptionRate()
    {
        return MoreFurnaces.netherrackConsumptionRate;
    }

    @Override
    public boolean isBurning()
    {
        if (worldObj != null)
            return worldObj.getBlockState(pos.up()).getBlock() == Blocks.FIRE;
        return false;
    }
}
