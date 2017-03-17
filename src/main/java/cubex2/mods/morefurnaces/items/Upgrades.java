package cubex2.mods.morefurnaces.items;

import cubex2.mods.morefurnaces.FurnaceType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public enum Upgrades
{
    STONE_TO_IRON(null, FurnaceType.IRON, "stone_to_iron", "III", "ISI", "III"),
    STONE_TO_NETHERRACK(null, FurnaceType.NETHERRACK, "stone_to_netherrack", "NNN", "NSN", "NNN"),
    IRON_TO_GOLD(FurnaceType.IRON, FurnaceType.GOLD, "iron_to_gold", "GGG", "GiG", "GGG"),
    IRON_TO_OBSIDIAN(FurnaceType.IRON, FurnaceType.OBSIDIAN, "iron_to_obsidian", "OOO", "OiO", "OOO"),
    GOLD_TO_DIAMOND(FurnaceType.GOLD, FurnaceType.DIAMOND, "gold_to_diamond", "DDD", "DgD", "DDD");

    private final FurnaceType from;
    private final FurnaceType to;
    private final String unlocalizedName;
    private final String[] recipe;

    Upgrades(FurnaceType from, FurnaceType to, String unlocalizedName, String... recipe)
    {
        this.from = from;
        this.to = to;
        this.unlocalizedName = unlocalizedName;
        this.recipe = recipe;
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

    public static void addRecipes(ItemUpgrade item)
    {
        for (Upgrades upgrade : values())
        {
            ShapedOreRecipe recipe = new ShapedOreRecipe(new ItemStack(item, 1, upgrade.ordinal()),
                                                         upgrade.recipe,
                                                         'S', Blocks.STONE,
                                                         'I', "ingotIron",
                                                         'i', "nuggetIron",
                                                         'G', "ingotGold",
                                                         'g', "nuggetGold",
                                                         'O', Blocks.OBSIDIAN,
                                                         'D', "gemDiamond",
                                                         'N', Blocks.NETHERRACK);

            GameRegistry.addRecipe(recipe);
        }
    }
}
