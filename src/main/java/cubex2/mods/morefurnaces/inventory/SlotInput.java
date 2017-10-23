package cubex2.mods.morefurnaces.inventory;

import cubex2.cxlibrary.inventory.ISlotCX;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotInput extends SlotItemHandler implements ISlotCX
{
    private final String name;

    public SlotInput(String name, IItemHandler itemHandler, int index)
    {
        super(itemHandler, index, -2000, -2000);
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
