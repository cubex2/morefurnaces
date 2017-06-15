package cubex2.mods.morefurnaces.items;

import cubex2.mods.morefurnaces.FurnaceType;

public enum Upgrades
{
    STONE_TO_IRON(null, FurnaceType.IRON, "stone_to_iron"),
    STONE_TO_NETHERRACK(null, FurnaceType.NETHERRACK, "stone_to_netherrack"),
    IRON_TO_GOLD(FurnaceType.IRON, FurnaceType.GOLD, "iron_to_gold"),
    IRON_TO_OBSIDIAN(FurnaceType.IRON, FurnaceType.OBSIDIAN, "iron_to_obsidian"),
    GOLD_TO_DIAMOND(FurnaceType.GOLD, FurnaceType.DIAMOND, "gold_to_diamond");

    private final FurnaceType from;
    private final FurnaceType to;
    private final String unlocalizedName;

    Upgrades(FurnaceType from, FurnaceType to, String unlocalizedName)
    {
        this.from = from;
        this.to = to;
        this.unlocalizedName = unlocalizedName;
    }

    public boolean canUpgrade(FurnaceType type)
    {
        return from != null && type == from;
    }

    public FurnaceType getUpgradedType()
    {
        return to;
    }

    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }
}
