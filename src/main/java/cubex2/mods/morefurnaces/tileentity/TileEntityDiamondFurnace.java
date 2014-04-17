package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;

public class TileEntityDiamondFurnace extends TileEntityIronFurnace
{
    public TileEntityDiamondFurnace()
    {
        super(FurnaceType.DIAMOND);
    }

    @Override
    public int getSpeed()
    {
        return MoreFurnaces.diamondSpeed;
    }

    @Override
    public float getConsumptionRate()
    {
        return MoreFurnaces.diamondConsumptionRate;
    }
}
