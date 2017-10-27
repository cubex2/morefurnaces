package cubex2.mods.morefurnaces.items;

import cubex2.mods.morefurnaces.FurnaceType;

public enum Upgrades
{
    STONE_TO_IRON(null, FurnaceType.IRON, "stone_to_iron"),
    STONE_TO_NETHERRACK(null, FurnaceType.NETHERRACK, "stone_to_netherrack"),
    IRON_TO_GOLD(FurnaceType.IRON, FurnaceType.GOLD, "iron_to_gold"),
    IRON_TO_OBSIDIAN(FurnaceType.IRON, FurnaceType.OBSIDIAN, "iron_to_obsidian"),
    GOLD_TO_DIAMOND(FurnaceType.GOLD, FurnaceType.DIAMOND, "gold_to_diamond"),
    COPPER_TO_SILVER(FurnaceType.COPPER, FurnaceType.SILVER, "copper_to_silver"),
    IRON_TO_SILVER(FurnaceType.IRON, FurnaceType.SILVER, "iron_to_silver"),
    STONE_TO_COPPER(null, FurnaceType.COPPER, "stone_to_copper");

    private final FurnaceType from;
    private final FurnaceType to;
    private final String unlocalizedName;

    Upgrades(FurnaceType from, FurnaceType to, String unlocalizedName)
    {
        this.from = from;
        this.to = to;
        this.unlocalizedName = unlocalizedName;
    }

    public boolean isVanillaUpgrade()
    {
        return from == null;
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
