package cubex2.mods.morefurnaces.inventory;

import cubex2.cxlibrary.inventory.ISlotCX;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceFuel;

public class SlotFuel extends SlotFurnaceFuel implements ISlotCX
{
    private final String name;

    public SlotFuel(String name, IInventory inventoryIn, int slotIndex)
    {
        super(inventoryIn, slotIndex, -2000, -2000);
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
