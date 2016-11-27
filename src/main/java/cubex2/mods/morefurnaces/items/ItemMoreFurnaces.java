package cubex2.mods.morefurnaces.items;

import cubex2.mods.morefurnaces.FurnaceType;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemMoreFurnaces extends ItemBlock
{

    public ItemMoreFurnaces(Block block)
    {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int i)
    {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return FurnaceType.values()[stack.getItemDamage()].name() + "_furnace";
    }
}
