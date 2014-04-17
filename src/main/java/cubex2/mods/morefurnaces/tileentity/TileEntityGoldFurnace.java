package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;

public class TileEntityGoldFurnace extends TileEntityIronFurnace
{
    public TileEntityGoldFurnace()
    {
        super(FurnaceType.GOLD);
    }

    @Override
    public int getSpeed()
    {
        return MoreFurnaces.goldSpeed;
    }

    @Override
    public float getConsumptionRate()
    {
        return MoreFurnaces.goldConsumptionRate;
    }
}
