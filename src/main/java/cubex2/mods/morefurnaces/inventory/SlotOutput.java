package cubex2.mods.morefurnaces.inventory;

import cubex2.cxlibrary.inventory.ISlotCX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceOutput;

public class SlotOutput extends SlotFurnaceOutput implements ISlotCX
{
    private final String name;

    public SlotOutput(String name, EntityPlayer player, IInventory inventoryIn, int slotIndex)
    {
        super(player, inventoryIn, slotIndex, -2000, -2000);
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
