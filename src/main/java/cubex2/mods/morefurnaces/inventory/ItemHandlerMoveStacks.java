package cubex2.mods.morefurnaces.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.commons.lang3.ArrayUtils;

public class ItemHandlerMoveStacks extends RangedWrapper
{
    private final int[] destSlots;
    private final ItemHandlerFurnace furnace;

    public ItemHandlerMoveStacks(ItemHandlerFurnace furnace, int minSlot, int maxSlotInclusive, int... destSlots)
    {
        super(furnace, minSlot, maxSlotInclusive + 1);
        this.furnace = furnace;

        this.destSlots = destSlots;
    }

    public void moveStacks()
    {
        for (int i = 0; i < getSlots(); i++)
        {
            if (!ArrayUtils.contains(destSlots, i))
            {
                ItemStack src = getStackInSlot(i);
                if (!src.isEmpty())
                {
                    ItemStack remainder = moveStackToFirstDest(src);
                    setStackInSlot(i, remainder);
                }
            }
        }
    }

    private ItemStack moveStackToFirstDest(ItemStack stack)
    {
        furnace.slotChecksEnabled = false;

        ItemStack remainder = stack;
        for (int destSlot : destSlots)
        {
            remainder = insertItem(destSlot, remainder, false);
        }

        furnace.slotChecksEnabled = true;
        return remainder;
    }
}
