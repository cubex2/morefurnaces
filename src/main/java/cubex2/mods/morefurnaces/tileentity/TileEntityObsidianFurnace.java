package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;

public class TileEntityObsidianFurnace extends TileEntityIronFurnace
{
    public TileEntityObsidianFurnace()
    {
        super(FurnaceType.OBSIDIAN);
    }

    @Override
    public int getSpeed()
    {
        return MoreFurnaces.obsidianSpeed;
    }

    @Override
    public float getConsumptionRate()
    {
        return MoreFurnaces.obsidianConsumptionRate;
    }

}
